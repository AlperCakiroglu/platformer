package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;

public class CreditsScreen extends AbstractScreen {

    private Skin skin;
    private ScrollPane scroller;
    private float counter = 0;

    public CreditsScreen() {
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
        Label credit1 = new Label("Music: Going Higher - Bensound.com",labelstyle);credit1.setFontScale(0.6f);
        Label credit2 = new Label("Tileset : Flat Tileset - LudicArts.com",labelstyle);credit2.setFontScale(0.6f);
        Label credit3 = new Label("Tileset : Volcano Tileset - LudicArts.com",labelstyle);credit3.setFontScale(0.6f);
        Label credit4 = new Label("Tileset : Desert Tileset - LudicArts.com",labelstyle);credit4.setFontScale(0.6f);
        Label credit5 = new Label("Animation : Trolls - craftpix.net",labelstyle);credit5.setFontScale(0.6f);
        Label credit6 = new Label("Animation : Orcs - craftpix.net",labelstyle);credit6.setFontScale(0.6f);
        Label credit7 = new Label("Sounds : Hits - qubodup.net",labelstyle);credit7.setFontScale(0.6f);
        Label credit13 = new Label("Font : Carnivalee Freakshow - 1001fonts.com",labelstyle);credit13.setFontScale(0.6f);
        Label credit14 = new Label("Gui : Fantasy - gameart2d.com",labelstyle);credit14.setFontScale(0.6f);
        Label credit15 = new Label("Sound : Boxing arena sound - soundbible.com",labelstyle);credit15.setFontScale(0.6f);
        Label credit16 = new Label("Sound : Sad trombone - soundbible.com",labelstyle);credit16.setFontScale(0.6f);
        Label credit17 = new Label("Icon:Clock-designed by freepik from Flaticon",labelstyle);credit17.setFontScale(0.6f);

        Label credit8 = new Label("images edited with editor.pho.to",labelstyle);credit8.setFontScale(0.6f);
        Label credit9 = new Label("and imagesplitter.net",labelstyle);credit9.setFontScale(0.6f);

        Label credit10 = new Label("physics engine: Box2D",labelstyle);credit10.setFontScale(0.6f);
        Label credit11 = new Label("powered by libgdx",labelstyle);credit11.setFontScale(0.6f);

        Label credit12 = new Label("created by itujoker",labelstyle);credit12.setFontScale(0.6f);

        Label empty = new Label("",labelstyle);

        Table creditTable = new Table();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(credit1);
        creditTable.row();
        creditTable.add(credit2);
        creditTable.row();
        creditTable.add(credit3);
        creditTable.row();
        creditTable.add(credit4);
        creditTable.row();
        creditTable.add(credit5);
        creditTable.row();
        creditTable.add(credit6);
        creditTable.row();
        creditTable.add(credit7);
        creditTable.row();
        creditTable.add(credit15);
        creditTable.row();
        creditTable.add(credit16);
        creditTable.row();
        creditTable.add(credit17);
        creditTable.row();
        creditTable.add(credit13);
        creditTable.row();
        creditTable.add(credit14);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(credit8);
        creditTable.row();
        creditTable.add(credit9);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(credit10);
        creditTable.row();
        creditTable.add(credit11);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(credit12);
        creditTable.row();
        creditTable.add(empty);
        creditTable.row();
        creditTable.add(empty);

        scroller = new ScrollPane(creditTable);
        Label header = new Label("Credits",labelstyle);

        Table scrollTable = new Table();
        scrollTable.setBounds(0,getHeight()*0.15f,getWidth(),getHeight()*0.70f);
        scrollTable.add(header).height(scrollTable.getHeight()/5).padBottom(scrollTable.getHeight()/10);
        scrollTable.row();
        scrollTable.add(scroller).height(scrollTable.getHeight()*0.7f);

        addActor(scrollTable);

        scroller.setTouchable(Touchable.disabled);

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);

        counter += 0.001;
        scroller.setScrollPercentY(counter);
    }


    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("disposeCredits");
    }
}
