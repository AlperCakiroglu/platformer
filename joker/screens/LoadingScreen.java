package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;

public class LoadingScreen extends AbstractScreen {

    private FreetypeFontLoader.FreeTypeFontLoaderParameter myfont;
    private float progress;
    private ShapeRenderer shapeRenderer;
    private boolean backgroundSet, quit;

    private Label continueFont;
    private float elapsedtime = 0;
    private static final float FADE_TIME = 1f;
    private ScrollPane scroller;
    private boolean isspan;

    public LoadingScreen() {
        super();

        myfont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myfont.fontFileName = "myfont.ttf";
        myfont.fontParameters.size = (int) getHeight() / 9;

        shapeRenderer = new ShapeRenderer();

    }


    private void queueAssets() {
        game.assets.load("myfont.ttf", BitmapFont.class, myfont);
        game.assets.load("levels/Background.png", Texture.class);
        game.assets.load("animations/troll/troll_animations.atlas", TextureAtlas.class);
        game.assets.load("animations/orc/orc_animations.pack", TextureAtlas.class);
        game.assets.load("menus.pack", TextureAtlas.class);
        game.assets.load("sounds/orc_attack.ogg", Sound.class);
        game.assets.load("sounds/orc_hit.ogg", Sound.class);
        game.assets.load("sounds/troll_attack.ogg", Sound.class);
        game.assets.load("sounds/troll_hit.ogg", Sound.class);
        game.assets.load("sounds/glass.ogg", Sound.class);
        game.assets.load("sounds/fail.ogg", Sound.class);
        game.assets.load("sounds/win.wav", Sound.class);
        game.assets.load("musics/bensound-goinghigher.mp3", Music.class);

    }


    @Override
    public void buildStage() {

    }

    @Override
    public void show() {
        super.show();
        progress = 0.f;
        queueAssets();
    }


    private void update() {
        progress = MathUtils.lerp(progress, game.assets.getProgress(), .1f);
        if (game.assets.update() && progress >= game.assets.getProgress() - 0.001f) {
            quit = true;
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update();

        if (game.assets.isLoaded("levels/Background.png", Texture.class) &&
                game.assets.isLoaded("myfont.ttf", BitmapFont.class) && !backgroundSet) {
            Image background = new Image((Texture) game.assets.get("levels/Background.png"));
            background.setSize(getWidth(), getHeight());
            addActor(background);

            Label.LabelStyle labelStyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class)
                    , Color.WHITE);
            Label gameTitle = new Label("Crazy Things", labelStyle);

            String hintString = game.hintsArray.random();
            if(game.isAndroidTV)
                hintString = "Use dpads center and navigation keys";
            Label hint = new Label("HINT : "+hintString,labelStyle);hint.setFontScale(0.6f);
            scroller = new ScrollPane(hint);
            scroller.addCaptureListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    isspan = true;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    isspan = false;
                }
            });


            Table headTable = new Table();//liveTable.setDebug(true);
            headTable.setBounds(getWidth()*0.05f,getHeight()*0.2f,getWidth()*0.9f,getHeight()*0.7f);
            headTable.add(gameTitle).height(getHeight()*0.2f);
            headTable.row();
            headTable.add(scroller).height(getHeight()*0.14f).padTop(getHeight()*0.2f);

            addActor(headTable);

            String tvorphone= "Touch to Continue";
            if(game.isAndroidTV)
                tvorphone = "Press center key";

            continueFont = new Label(tvorphone, labelStyle);
            continueFont.setFontScale(0.7f);
            continueFont.setVisible(false);

            Table table = new Table();
            table.setBounds(0,0,getWidth(),getHeight()/6);
            table.add(continueFont).center().top();
            addActor(table);
            backgroundSet = true;
        }


        if (backgroundSet && !quit) {

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.rect(getWidth() / 6, getHeight() / 12
                    , getWidth() * .66f, getHeight() / 15);

            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(getWidth() / 6, getHeight() / 12
                    , progress * getWidth() * .66f, getHeight() / 15);
            shapeRenderer.end();

        }
        else if(quit){
            continueFont.setVisible(true);
            elapsedtime += delta;
            continueFont.setColor(continueFont.getColor().r,continueFont.getColor().g,continueFont.getColor().b,(elapsedtime / FADE_TIME) % 1f);
            if(Gdx.input.justTouched() && !isspan){
                ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);

            }

            if(game.isAndroidTV){
                if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)){
                    ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
                }
            }
        }

    }


    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        System.out.println("disposeLoad");
    }


}
