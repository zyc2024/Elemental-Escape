/*
 * FreetypeFontParser.java
 *
 * This is an interface for parsing a JSON entry into a BitmapFont asset. It only
 * works for TrueType fonts. You must create another parser for alternate font
 * representations.
 *
 * @author Walker M. White
 * @data   04/20/2020
 */
package com.elements.game.utility.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * This class parses a JSON entry for TrueType font into a {@link BitmapFont}.
 *
 * The parser converts JSON entries into {@link FreetypeFontLoader.FreeTypeFontLoaderParameter}
 * values of the same name. It is also possible to specify a texture by simply
 * giving the name of the file.  In that case, the default parameters will be
 * used on loading.
 *
 * This class only works for TrueType fonts.  You must create another parser for
 * alternate font representations.
 */
public class FreetypeFontParser implements AssetParser<BitmapFont> {
    /** The current font entry in the JSON directory */
    private JsonValue root;

    /**
     * Returns the asset type generated by this parser
     *
     * @return the asset type generated by this parser
     */
    public Class<BitmapFont> getType() {
        return BitmapFont.class;
    }

    /**
     * Resets the parser iterator for the given directory.
     *
     * The value directory is assumed to be the root of a larger JSON structure.
     * The individual assets are defined by subtrees in this structure.
     *
     * @param directory    The JSON representation of the asset directory
     */
    public void reset(JsonValue directory) {
        root = directory;
        root = root.getChild( "fonts" );
    }

    /**
     * Returns true if there are still assets left to generate
     *
     * @return true if there are still assets left to generate
     */
    public boolean hasNext() {
        return root != null;
    }

    /**
     * Processes the next available font, loading it into the asset manager
     *
     * The parser converts JSON entries into {@link FreetypeFontLoader.FreeTypeFontLoaderParameter}
     * values of the same name. The file will be the contents of the file entry.  The 
     * key will be the name of the font object.
     *
     * If the JSON value is a string and not an object, it will interpret that
     * string as the file and use the default settings.
     *
     * This method fails silently if there are no available assets to process.
     *
     * @param manager    The asset manager to load an asset
     * @param keymap    The mapping of JSON keys to asset file names
     */
    public void processNext(AssetManager manager, ObjectMap<String,String> keymap) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		if (root.isString()) {
			params.fontParameters.size = 16;
			params.fontParameters.mono = false;
			params.fontParameters.hinting = FreeTypeFontGenerator.Hinting.AutoMedium;
			params.fontParameters.color = Color.WHITE;
			params.fontParameters.gamma = 1.8f;
			params.fontParameters.renderCount = 2;
			params.fontParameters.borderColor = Color.BLACK;
			params.fontParameters.borderStraight = false;
			params.fontParameters.borderGamma = 1.8f;
			params.fontParameters.shadowOffsetX = 0;
			params.fontParameters.shadowOffsetY = 0;
			params.fontParameters.shadowColor = new Color(0, 0, 0, 0.75f);
			params.fontParameters.spaceX = 0;
			params.fontParameters.spaceY = 0;
			params.fontParameters.padTop = 0;
			params.fontParameters.padLeft = 0;
			params.fontParameters.padBottom = 0;
			params.fontParameters.padRight  = 0;
			params.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
			params.fontParameters.kerning = true;
			params.fontParameters.flip = false;
			params.fontParameters.genMipMaps = false;
			params.fontParameters.minFilter = Texture.TextureFilter.Linear;
			params.fontParameters.magFilter = Texture.TextureFilter.Linear;
			params.fontFileName = root.toString();

			String uid = params.fontFileName;
			if (params.fontFileName.toLowerCase().endsWith(".ttf")) {
				uid = uid.substring( 0, uid.length()-4 )+":"+params.fontParameters.size+".ttf";
			}
			keymap.put(root.name(), uid);
			manager.load( uid, BitmapFont.class, params );
		} else {
			params.fontParameters.size = root.getInt( "size",16 );
			params.fontParameters.mono = root.getBoolean( "mono",false );
			params.fontParameters.hinting = ParserUtils.parseHinting(root.get("hinting"), FreeTypeFontGenerator.Hinting.AutoMedium);
			params.fontParameters.color = ParserUtils.parseColor(root.get("color"), Color.WHITE);
			params.fontParameters.gamma = root.getFloat("gamma", 1.8f);
			params.fontParameters.renderCount = root.getInt("renderCount", 2);
			params.fontParameters.borderColor = ParserUtils.parseColor(root.get("borderColor"), Color.BLACK);
			params.fontParameters.borderStraight = root.getBoolean( "borderStraight",false );
			params.fontParameters.borderGamma = root.getFloat("borderGamma", 1.8f);
			params.fontParameters.shadowOffsetX = root.getInt("shadowOffsetX", 0);
			params.fontParameters.shadowOffsetY = root.getInt("shadowOffsetY", 0);
			params.fontParameters.shadowColor = ParserUtils.parseColor(root.get("shadowColor"), new Color(0, 0, 0, 0.75f));
			params.fontParameters.spaceX = root.getInt("spaceX", 0);
			params.fontParameters.spaceY = root.getInt("spaceY", 0);
			params.fontParameters.padTop = root.getInt("padTop", 0);
			params.fontParameters.padLeft = root.getInt("padLeft", 0);
			params.fontParameters.padBottom = root.getInt("padBottom", 0);
			params.fontParameters.padRight = root.getInt("padRight", 0);
			params.fontParameters.characters = root.getString( "characters", FreeTypeFontGenerator.DEFAULT_CHARS );
			params.fontParameters.kerning = root.getBoolean( "kerning",true );
			params.fontParameters.flip = root.getBoolean( "flip",false );
			params.fontParameters.genMipMaps = root.getBoolean( "mipmaps",false );
			params.fontParameters.minFilter = ParserUtils.parseFilter(root.get("minFilter"), Texture.TextureFilter.Linear);
			params.fontParameters.magFilter = ParserUtils.parseFilter(root.get("magFilter"), Texture.TextureFilter.Linear);
			params.fontFileName = root.getString( "file", null );
			if (params.fontFileName != null) {
				String uid = params.fontFileName;
				if (params.fontFileName.toLowerCase().endsWith(".ttf")) {
					uid = uid.substring( 0, uid.length()-4 )+":"+params.fontParameters.size+".ttf";
				}
				keymap.put(root.name(),uid);
				manager.load( uid, BitmapFont.class, params );
			}
		}
        root = root.next();
    }

    /**
     * Returns true if o is another FreetypeFontParser
     *
     * @return true if o is another FreetypeFontParser
     */
    public boolean equals(Object o) {
        return o instanceof FreetypeFontParser;
    }


}