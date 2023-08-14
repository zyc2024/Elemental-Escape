package com.elements.game.utility.json;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.utility.assets.AssetDirectory;

public class LevelParser {

    // Data Containers for Output of Parsing
    private JsonValue platformData;

    private JsonValue woodData;

    private JsonValue playerData;

    private int mapTileHeight;

    private int mapTileWidth;

    private int mapHeight;

    private final Vector2 dimensionCache = new Vector2();

    private final Vector2 positionCache = new Vector2();

    private final TilesetHelper tilesetHelper;

    private static final int LOWER28BITMASK = 0xFFFFFFF;

    public static final int DEFAULT_TILE_SIZE = 32;

    /**
     * Collection of helper methods associated with tileset related properties.
     */
    private static class TilesetHelper {
        private final JsonValue[] tiles;

        public static final String DEFAULT_TYPE = "MISSING";

        // assume single tileset collection
        // implementation will need changes for multi-sets
        public TilesetHelper(JsonValue objectTileset) {
            JsonValue tilesJson = objectTileset.get("tiles");
            this.tiles = new JsonValue[tilesJson.size];
            for (JsonValue tile : tilesJson) {
                tiles[tile.getInt("id")] = tile;
            }
        }

        /**
         * @param id processed ID (non-negative)
         * @return complete json data for corresponding tile ID
         */
        public JsonValue getTileById(int id) {
            return id > tiles.length ? null : tiles[id - 1];
        }

        /**
         * @param id processed id (non-negative)
         * @return class/type of tile with given ID
         */
        public String getTileType(int id) {
            return id > tiles.length ? DEFAULT_TYPE : tiles[id - 1].getString("type", DEFAULT_TYPE);
        }

        private int getTileDimension(int id, String property) {
            if (id > tiles.length) return DEFAULT_TILE_SIZE;
            return tiles[id - 1].getInt(property, DEFAULT_TILE_SIZE);
        }

        public int getTileWidth(int id) {
            return getTileDimension(id, "imagewidth");
        }

        public int getTileHeight(int id) {
            return getTileDimension(id, "imageheight");
        }

        public JsonValue getTileProperty(int id, String propertyName) {
            return id > tiles.length ? null : tiles[id - 1].get(propertyName);
        }
    }

    public LevelParser(AssetDirectory directory) {
        // this.directory = directory;
        // this.globalConstants = directory.getEntry("constants", JsonValue.class);
        JsonValue objTileset = directory.getEntry("tileset", JsonValue.class);
        this.tilesetHelper = new TilesetHelper(objTileset);
    }

    /**
     * @param levelData raw/unprocessed level data,
     * @return processed data ready for rendering level
     */
    public JsonValue parse(JsonValue levelData) {
        // load some necessary map data
        mapTileHeight = levelData.getInt("tileheight", DEFAULT_TILE_SIZE);
        mapTileWidth = levelData.getInt("tilewidth", DEFAULT_TILE_SIZE);
        mapHeight = levelData.getInt("height");

        // initialize output containers
        // json object = {}
        // json array = []
        platformData = new JsonValue(JsonValue.ValueType.array);
        playerData = new JsonValue(JsonValue.ValueType.object);
        woodData = new JsonValue(JsonValue.ValueType.array);

        // get object layers from level data (layering helps provide depth and organization)
        JsonValue layers = levelData.get("layers");
        for (JsonValue layer : layers) {
            if (layer.getString("type").equals("objectgroup")) {
                // object layer found, parse all objects
                for (JsonValue object : layer.get("objects")) {
                    parseObject(object);
                }
            }
        }
        // somehow player was not found
        if (playerData.size == 0) {
            System.err.println("player not found");
            playerData.addChild("x", new JsonValue(2));
            playerData.addChild("y", new JsonValue(2));
        }
        JsonValue processedLevel = new JsonValue(JsonValue.ValueType.object);
        // TODO: add remaining needed map components
        processedLevel.addChild("player", playerData);
        processedLevel.addChild("platform", platformData);
        processedLevel.addChild("wood", woodData);
        return processedLevel;
    }


    /**
     * Given an object, convert to game data if applicable and store results.
     *
     * @param objectJson unprocessed object data
     */
    private void parseObject(JsonValue objectJson) {
        long gid = objectJson.getLong("gid", 0);
        if (gid == 0){
            return;
        }
        int tileID = getProcessedId(gid);
        String objType = tilesetHelper.getTileType(tileID);
        switch (objType) {
            case "player":
                if (playerData != null) {
                    playerData = parsePlayer(objectJson);
                } else {
                    System.err.println("multiple players found");
                }
                break;
            case "grass":
                platformData.addChild(parsePlatform(objectJson));
                break;
            case "wood":
                woodData.addChild(parsePlatform(objectJson));
                break;
            default:
                break;
        }
    }

    /**
     * @param data unprocessed player object
     * @return processed player object
     */
    private JsonValue parsePlayer(JsonValue data) {
        // there's only 1 player, and we really only need player position (unless other properties
        // can vary from level to level).
        JsonValue player = new JsonValue(JsonValue.ValueType.object);
        computePosition(data);
        player.addChild("x", new JsonValue(positionCache.x));
        player.addChild("y", new JsonValue(positionCache.x));
        return player;
    }

    /**
     * @param data unprocessed platform object
     * @return processed platform object
     */
    private JsonValue parsePlatform(JsonValue data) {
        JsonValue platform = new JsonValue(JsonValue.ValueType.object);
        computePosition(data);
        platform.addChild("x", new JsonValue(positionCache.x));
        platform.addChild("y", new JsonValue(positionCache.y));
        computeDisplayDimensions(data);
        platform.addChild("width", new JsonValue(dimensionCache.x));
        platform.addChild("height", new JsonValue(dimensionCache.y));
        // TODO: use some implied inheritance of data in constructors if object is a box so no need
        //  to duplicate information
        JsonValue hitbox = new JsonValue(JsonValue.ValueType.object);
        hitbox.addChild("width", new JsonValue(dimensionCache.x));
        hitbox.addChild("height", new JsonValue(dimensionCache.y));
        platform.addChild("hit-box", hitbox);
        return platform;
    }

    /**
     * @param gid unprocessed object gid
     * @return tile id of object, where gid is processed with flag bits removed.
     */
    private int getProcessedId(long gid) {
        return (int) (gid & LOWER28BITMASK);
    }

    /**
     * loads the corresponding game width and height for the specified dimensions into dimension
     * cache.
     *
     * @param objectJson unprocessed json data with (width, height) properties
     * @apiNote requires map tile dimensions to be preloaded.
     */
    private void computeDisplayDimensions(JsonValue objectJson) {
        float width = objectJson.getFloat("width");
        float height = objectJson.getFloat("height");
        dimensionCache.set(width / mapTileWidth, height / mapTileHeight);
    }

    /**
     * loads the corresponding game position for the specified position (tx,ty) into position
     * cache.
     *
     * @param objectJson unprocessed json data with (x,y) properties
     * @apiNote requires map tile dimensions to be preloaded.
     */
    public void computePosition(JsonValue objectJson) {
        // Tiled-maps have inverted y-axis
        float tx = objectJson.getFloat("x");
        float ty = objectJson.getFloat("y");
        positionCache.set(tx / mapTileWidth, mapHeight - ty / mapTileHeight);
    }
}
