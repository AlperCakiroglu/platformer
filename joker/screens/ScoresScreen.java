package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import itu.joker.screens.util.ScreenManager;

public class ScoresScreen extends AbstractScreen {

    private Skin skin;
    public ScoresScreen() {
        super();
        skin = new Skin();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void buildStage() {
        skin.addRegions(game.assets.get("menus.pack", TextureAtlas.class));

        Image background = new Image((Texture)game.assets.get("levels/Background.png"));
        background.setSize(getWidth(),getHeight());
        addActor(background);

        Table menuback = new Table();
        menuback.setBounds(0,0,getWidth(),getHeight());
        menuback.setBackground(skin.getDrawable("back"));
        menuback.setColor(menuback.getColor().r,menuback.getColor().g,menuback.getColor().b,0.5f);
        addActor(menuback);

        Label.LabelStyle labelstyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);
        Label[] labels = new Label[10];
        Table scrollerTable = new Table();
        for(int i=0;i<10;i++) {
            String score;
            score = game.bestscores[i] == 0 ? "?" : game.bestscores[i]+"";
            labels[i] = new Label("Level " + (i + 1) + " : " + score, labelstyle);labels[i].setFontScale(0.6f);
            scrollerTable.add(labels[i]);
            scrollerTable.row();
        }

        if(game.bestscores[10] == 1) {
            int completescore = 0;
            for(int i=0;i<10;i++)
                completescore += game.bestscores[i];
            String difficulty;
            if(game.bestscores[11]==1)
                difficulty="easy";
            else if(game.bestscores[11]==2)
                difficulty="normal";
            else
                difficulty = "hard";

            Label gameDone = new Label("Game is completed with "+completescore+" at "+difficulty, labelstyle);gameDone.setFontScale(0.6f);
            scrollerTable.add(gameDone);
            scrollerTable.row();

        }

        ScrollPane scroller = new ScrollPane(scrollerTable);

        Label header = new Label("Best Scores",labelstyle);
        Table scoresTable = new Table();
        scoresTable.setBounds(getWidth()*0.1f,getHeight()*0.15f,getWidth()*0.8f,getHeight()*0.70f);
        scoresTable.add(header).height(scoresTable.getHeight()/5).padBottom(scoresTable.getHeight()/10);
        scoresTable.row();
        scoresTable.add(scroller).height(scoresTable.getHeight()*0.7f);

        addActor(scoresTable);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.MAIN_MENU);

    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("disposeScores");
    }
}
