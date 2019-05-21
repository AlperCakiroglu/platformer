package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import itu.joker.screens.util.ScreenManager;

public class GameStartingScreen extends AbstractScreen {

    private Skin skin;
    private Image head1,head2,head3;

    private float progress;
    private ShapeRenderer shapeRenderer;
    private boolean done;

    private Label continueFont;
    private float elapsedtime;
    private static final float FADE_TIME = 1f;
    private int level;

    public GameStartingScreen(Integer level) {
        super();
        this.level = level;
        shapeRenderer = new ShapeRenderer();
        skin = new Skin();
        elapsedtime = 0;


    }

    @Override
    public void initialize() {
        progress = 0.f;
        queueAssets();
    }

    private void queueAssets() {
        game.assets.load("levels/level"+level+".tmx", TiledMap.class);

    }

    private void update(){
        progress = MathUtils.lerp(progress, game.assets.getProgress(), .1f);
        if (game.assets.update() && progress >= game.assets.getProgress() - 0.001f){
            done = true;
        }

    }


    @Override
    public void buildStage() {

        skin.addRegions(game.assets.get("menus.pack", TextureAtlas.class));

        Image background = new Image((Texture)game.assets.get("levels/Background.png"));
        background.setSize(getWidth(),getHeight());
        addActor(background);

        Label.LabelStyle labelstyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);
        Label gameName = new Label("Crazy Things",labelstyle);
        head1 = new Image(skin.getDrawable("head1"));
        head2 = new Image(skin.getDrawable("head2"));
        head3 = new Image(skin.getDrawable("head3"));

        Label levelName = new Label("Level "+level,labelstyle);
        levelName.setFontScale(0.7f);


        Table liveTable = new Table();
        liveTable.setBounds(getWidth()/4,getHeight()*0.2f,getWidth()/2,getHeight()*0.7f);
        liveTable.add(gameName).colspan(3).center().height(getHeight()*0.2f);
        liveTable.row();
        liveTable.add(levelName).colspan(3).center().height(getHeight()*0.2f).pad(getHeight()*0.05f).padTop(0);
        liveTable.row();
        liveTable.add(head2).width(getHeight()*0.2f).height(getHeight()*0.2f);
        liveTable.add(head1).width(getHeight()*0.2f).height(getHeight()*0.2f);
        liveTable.add(head3).width(getHeight()*0.2f).height(getHeight()*0.2f);
        addActor(liveTable);

        String tvorphone= "Touch to Continue";
        if(game.isAndroidTV)
            tvorphone = "Press Center Key";
        continueFont = new Label(tvorphone, labelstyle);
        continueFont.setFontScale(0.7f);
        continueFont.setVisible(false);

        Table table = new Table();
        table.setBounds(0,0,getWidth(),getHeight()/6);
        table.add(continueFont).center().top();
        addActor(table);

    }


    @Override
    public void render(float delta) {
        super.render(delta);

        update();
        if (!done) {

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.rect(getWidth() / 6, getHeight() / 12
                    , getWidth() * .66f, getHeight() / 15);

            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(getWidth() / 6, getHeight() / 12
                    , progress * getWidth() * .66f, getHeight() / 15);
            shapeRenderer.end();

        }
        else{

            continueFont.setVisible(true);
            elapsedtime += delta;
            continueFont.setColor(continueFont.getColor().r,continueFont.getColor().g,continueFont.getColor().b,(elapsedtime / FADE_TIME) % 1f);
            if(Gdx.input.justTouched()){
                ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.GAME,level);
            }


        }

        if(game.isAndroidTV){
            if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)){
                ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.GAME,level);
            }
        }


        if(game.lives <=1){
            head3.setColor(head3.getColor().r,head3.getColor().g,head3.getColor().b,0.3f);
            if(game.lives == 0)
                head1.setColor(head1.getColor().r,head1.getColor().g,head1.getColor().b,0.3f);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        shapeRenderer.dispose();
        System.out.println("disposeGameStart");
    }
}
