package com.elements.game.utility.physics;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ShortArray;
import com.elements.game.view.GameCanvas;
import com.badlogic.gdx.graphics.Color;

/**
 * Arbitrary polygonal-shaped model to support collisions. <br> The polygon coordinates are all in
 * local space, relative to the object center.
 */
public class PolygonPhysicsBody extends PhysicsBody {
    /**
     * An ear-clipping triangular to make sure we work with convex shapes
     */
    private static final EarClippingTriangulator triangleCutter = new EarClippingTriangulator();

    /**
     * Shape information for this physics object
     */
    protected PolygonShape[] shapes;

    /**
     * The polygon points (vertex set)
     */
    private final float[] points;

    /**
     * The triangle indices, used for drawing
     */
    private short[] triangleIndices;

    /**
     * A cache value for the fixtures (for resizing)
     */
    private Fixture[] geometries;

    /**
     * The (width,height) values of the polygon bounding box
     */
    private Vector2 dimension;

    /**
     * A cache value for when the user wants to access the dimensions
     */
    private Vector2 sizeCache;

    /**
     * Cache of the polygon vertices, use for resizing and creating the shapes.
     */
    private float[] vertices;

    /**
     * whether a resizing operation was performed (since last status check)
     */
    private boolean resized;

    /**
     * This returns true upon initialization.
     *
     * @return whether the polygon has been resized (see {@link #setDimension(Vector2)}}) since last
     * call to this method.
     */
    public boolean isResized() {
        boolean result = resized;
        if (resized) {
            resized = false;
        }
        return result;
    }

    /**
     * NOTE: This allocates a new array. For effective usage, this should be called when
     * {@link #isResized()} returns true.
     *
     * @return polygon vertices that define the polygonal shape
     */
    public float[] getCoordinateVertices() {
        float[] out = new float[this.points.length];
        System.arraycopy(this.points, 0, out, 0, out.length);
        return out;
    }

    /**
     * NOTE: This allocates a new array. For effective usage, this should be called when
     * {@link #isResized()} returns true.
     *
     * @return triangle indices of this polygon's triangulation.
     */
    public short[] getTriangleIndices() {
        short[] out = new short[this.triangleIndices.length];
        System.arraycopy(this.triangleIndices, 0, out, 0, out.length);
        return out;
    }

    /**
     * This returns the same vector each time so this cannot be used as an allocator.
     *
     * @return the dimensions of the Axis-Aligned-Bounding-Box (AABB)
     */
    public Vector2 getDimension() {
        return sizeCache.set(dimension);
    }

    /**
     * Sets the dimensions of the Axis-Aligned-Bounding-Box (AABB) <br> This method does not keep a
     * reference to the parameter.
     *
     * @param value the dimensions of the AABB
     */
    public void setDimension(Vector2 value) {
        setDimension(value.x, value.y);
    }

    /**
     * Sets the dimensions of the Axis-Aligned-Bounding-Box (AABB)
     *
     * @param width  The width of this AABB
     * @param height The height of this AABB
     */
    public void setDimension(float width, float height) {
        resize(width, height);
        markDirty(true);
    }

    /**
     * @return The width of the Axis-Aligned-Bounding-Box (AABB)
     */
    public float getWidth() {
        return dimension.x;
    }

    /**
     * Sets the width of the Axis-Aligned-Bounding-Box (AABB)
     *
     * @param value the box width
     */
    public void setWidth(float value) {
        sizeCache.set(value, dimension.y);
        setDimension(sizeCache);
    }

    /**
     * @return the height of the Axis-Aligned-Bounding-Box (AABB)
     */
    public float getHeight() {
        return dimension.y;
    }

    /**
     * Sets the height of the Axis-Aligned-Bounding-Box (AABB)
     *
     * @param value the box height
     */
    public void setHeight(float value) {
        sizeCache.set(dimension.x, value);
        setDimension(sizeCache);
    }

    /**
     * Creates a (not necessarily convex) polygon at the origin.
     *
     * @param points The polygon vertices
     */
    public PolygonPhysicsBody(float[] points) {
        this(points, 0, 0);
    }

    /**
     * Creates a (not necessarily convex) polygon
     *
     * @param points The polygon vertices (in local coordinates)
     * @param x      Initial x position of the polygon center
     * @param y      Initial y position of the polygon center
     */
    public PolygonPhysicsBody(float[] points, float x, float y) {
        super(x, y);
        assert points.length % 2 == 0;

        // Compute the bounds.
        initShapes(points);
        initBounds();

        // record the polygon vertices
        this.points = new float[points.length];
        System.arraycopy(points, 0, this.points, 0, points.length);
        resized = true;
    }

    /**
     * Initializes the bounding box (and drawing scale) for this polygon
     */
    private void initBounds() {
        float minX = vertices[0];
        float maxX = vertices[0];
        float minY = vertices[1];
        float maxY = vertices[1];

        for (int ii = 2; ii < vertices.length; ii += 2) {
            if (vertices[ii] < minX) {
                minX = vertices[ii];
            } else if (vertices[ii] > maxX) {
                maxX = vertices[ii];
            }
            if (vertices[ii + 1] < minY) {
                minY = vertices[ii + 1];
            } else if (vertices[ii + 1] > maxY) {
                maxY = vertices[ii + 1];
            }
        }
        dimension = new Vector2((maxX - minX), (maxY - minY));
        sizeCache = new Vector2(dimension);
    }

    /**
     * Initializes the Box2D shapes for this polygon
     *
     * @param points The polygon vertices
     */
    private void initShapes(float[] points) {
        // Triangulate (this guarantees we work with convex shapes)
        ShortArray array = triangleCutter.computeTriangles(points);
        trimCollinear(points, array);

        triangleIndices = new short[array.items.length];
        System.arraycopy(array.items, 0, triangleIndices, 0, triangleIndices.length);

        // Allocate space for physics triangles.
        int tris = array.items.length / 3;
        vertices = new float[tris * 6];
        shapes = new PolygonShape[tris];
        geometries = new Fixture[tris];
        for (int ii = 0; ii < tris; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                vertices[6 * ii + 2 * jj] = points[2 * array.items[3 * ii + jj]];
                vertices[6 * ii + 2 * jj + 1] = points[2 * array.items[3 * ii + jj] + 1];
            }
            shapes[ii] = new PolygonShape();
            shapes[ii].set(vertices, 6 * ii, 6);
        }
    }

    /**
     * Removes collinear vertices from the given triangulation. <br> For some reason, LibGDX
     * ear-clipping triangulation will occasionally return collinear vertices.
     *
     * @param points  The polygon vertices
     * @param indices The triangulation indices
     */
    private void trimCollinear(float[] points, ShortArray indices) {
        int collinear = 0;
        for (int ii = 0; ii < indices.size / 3 - collinear; ii++) {
            float t1 = points[2 * indices.items[3 * ii]] * (points[2 * indices.items[3 * ii + 1] + 1] - points[2 * indices.items[3 * ii + 2] + 1]);
            float t2 = points[2 * indices.items[3 * ii + 1]] * (points[2 * indices.items[3 * ii + 2] + 1] - points[2 * indices.items[3 * ii] + 1]);
            float t3 = points[2 * indices.items[3 * ii + 2]] * (points[2 * indices.items[3 * ii] + 1] - points[2 * indices.items[3 * ii + 1] + 1]);
            if (Math.abs(t1 + t2 + t3) < 0.0000001f) {
                indices.swap(3 * ii, indices.size - 3 * collinear - 3);
                indices.swap(3 * ii + 1, indices.size - 3 * collinear - 2);
                indices.swap(3 * ii + 2, indices.size - 3 * collinear - 1);
                collinear++;
            }
        }
        indices.size -= 3 * collinear;
        indices.shrink();
    }

    /**
     * Resize this polygon (stretching uniformly out from origin)
     *
     * @param width  The new width
     * @param height The new height
     */
    private void resize(float width, float height) {
        float scaleX = width / dimension.x;
        float scaleY = height / dimension.y;

        for (int ii = 0; ii < shapes.length; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                vertices[6 * ii + 2 * jj] *= scaleX;
                vertices[6 * ii + 2 * jj + 1] *= scaleY;
            }
            shapes[ii].set(vertices, 6 * ii, 6);
        }

        // Resize the polygon vertex-set as well
        for (int ii = 0; ii < points.length; ii += 2) {
            points[ii] *= scaleX;
            points[ii + 1] *= scaleY;
        }

        dimension.set(width, height);
        resized = true;
    }

    @Override
    protected void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixtures
        for (int ii = 0; ii < shapes.length; ii++) {
            fixture.shape = shapes[ii];
            geometries[ii] = body.createFixture(fixture);
        }
        markDirty(false);
    }

    @Override
    protected void releaseFixtures() {
        if (geometries[0] != null) {
            for (Fixture fix : geometries) {
                body.destroyFixture(fix);
            }
        }
    }

    @Override
    public void debug(GameCanvas canvas, Vector2 drawScale) {
        for(PolygonShape tri : shapes) {
            canvas.drawPhysics(tri,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
        }
    }
}
