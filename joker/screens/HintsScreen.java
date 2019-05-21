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
import com.badlogic.gdx.utils.Array;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;

public class HintsScreen extends AbstractScreen {

    private Skin skin;
    public HintsScreen() {
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
        Array<String> temp ;
        temp=game.hintsArray;
        if(game.isAndroidTV)
            temp=game.tvhintsArray;
        Label hint1 = new Label(temp.get(0),labelstyle);hint1.setFontScale(0.6f);
        Label hint2 = new Label(temp.get(1),labelstyle);hint2.setFontScale(0.6f);hint2.setColor(Color.GREEN);
        Label hint3 = new Label(temp.get(2),labelstyle);hint3.setFontScale(0.6f);
        Label hint4 = new Label(temp.get(3),labelstyle);hint4.setFontScale(0.6f);hint4.setColor(Color.GREEN);
        Label hint5 = new Label(temp.get(4),labelstyle);hint5.setFontScale(0.6f);
        Label hint6 = new Label(temp.get(5),labelstyle);hint6.setFontScale(0.6f);hint6.setColor(Color.GREEN);
        Label hint7 = new Label(temp.get(6),labelstyle);hint7.setFontScale(0.6f);

        ScrollPane scroller1 = new ScrollPane(hint1);
        ScrollPane scroller2 = new ScrollPane(hint2);
        ScrollPane scroller3 = new ScrollPane(hint3);
        ScrollPane scroller4 = new ScrollPane(hint4);
        ScrollPane scroller5 = new ScrollPane(hint5);
        ScrollPane scroller6 = new ScrollPane(hint6);
        ScrollPane scroller7 = new ScrollPane(hint7);

        Table scrollerTable = new Table();
        scrollerTable.add(scroller1);
        scrollerTable.row();
        scrollerTable.add(scroller2);
        scrollerTable.row();
        scrollerTable.add(scroller3);
        scrollerTable.row();
        scrollerTable.add(scroller4);
        scrollerTable.row();
        scrollerTable.add(scroller5);
        scrollerTable.row();
        scrollerTable.add(scroller6);
        scrollerTable.row();
        scrollerTable.add(scroller7);

        Label header = new Label("Hints",labelstyle);
        Table hintTable = new Table();
        hintTable.setBounds(getWidth()*0.1f,getHeight()*0.15f,getWidth()*0.8f,getHeight()*0.70f);
        hintTable.add(header).height(hintTable.getHeight()/5).padBottom(hintTable.getHeight()/10);
        hintTable.row();
        hintTable.add(scrollerTable).height(hintTable.getHeight()*0.7f);

        addActor(hintTable);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);

    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("disposeHints");
    }
}
