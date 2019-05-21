package itu.joker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;

public class Main extends Game {

	public static final int tileWidth = 256;
	public static final int tileHeight = 256;

	public static final int V_WIDTH = tileWidth * 10;
	public static final int V_HEIGHT = tileHeight * 7;
	public static final float PPM = 160;

	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short TROLL_BIT = 4;
	public static final short OBSTACLE_BIT = 8;
	public static final short WEAPON_BIT = 16;
	public static final short ORC_BIT = 32;
	public static final short LEG_BIT = 64;
	public static final short COIN_BIT = 128;
	public static final short CHEST_BIT = 256;
	public static final short WALL_BIT = 512;
	public static final short DESTROYED_BIT = 1024;

	public AssetManager assets;

	public int bodyCreateCounter = 0;

	public boolean soundOn,musicOn;
	public int lives;
	public int score;
	public float time;

	public Array<String> hintsArray;
	public Array<String> tvhintsArray;
	//player
	public int playerHealth,playerPower,playerType;
	public boolean isBossDead;

	public boolean isStory;
	public int lastLevel;
	public int[] bestscores;
	public Preferences preferences;
	public Json json;

	public boolean isAndroidTV;

	public Main(boolean isAndroidTV) {
		this.isAndroidTV = isAndroidTV;

	}



	@Override
	public void create () {



		assets = new AssetManager();
		FileHandleResolver resolver = new InternalFileHandleResolver();
		assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		assets.setLoader(TiledMap.class, new TmxMapLoader(resolver));

		lives = 2;

		preferences = Gdx.app.getPreferences("MyPrefs");
		if (!preferences.contains("lastlevel"))
			lastLevel = 1;
		else
			lastLevel=preferences.getInteger("lastlevel");

		json = new Json();
		bestscores = new int[12];
		if(!preferences.contains("bestscores")){
			for(int i=0;i<12;i++)
				bestscores[i]=0;
		}
		else
			bestscores = json.fromJson(int[].class,preferences.getString("bestscores"));


		hintsArray = new Array<String>();
		hintsArray.add("Back button can be used in menu");
		hintsArray.add("Completed level can be played again");
		hintsArray.add("You have three lives");
		hintsArray.add("If game is over you will start from level 1");
		hintsArray.add("You gain a live when you pass the level ");
		hintsArray.add("Enemies becomes stronger at upper levels");
		hintsArray.add("There are bosses at the end of levels");

		tvhintsArray = new Array<String>();
		tvhintsArray.add("Use down to open a menu in game ");
		tvhintsArray.add("Use navigation to move and center to attack");
		tvhintsArray.add("Use back button to back in menu");
		tvhintsArray.add("Completed level can be played again");
		tvhintsArray.add("You have 3 lives");
		tvhintsArray.add("If game is over you will start from level 1");
		tvhintsArray.add("You gain a live when you pass the level ");


		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.LOADING);
	}

	@Override
	public void render() {
		super.render();

	}

	@Override
	public void dispose() {

		super.dispose();
		getScreen().dispose();
		assets.dispose();
		hintsArray.clear();
		System.out.println("disposeMain");
	}



}
