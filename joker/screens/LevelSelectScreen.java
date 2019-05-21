package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;
import itu.joker.screens.util.UIFactory;

public class LevelSelectScreen extends AbstractScreen {

    Skin skin;

    int previousbutton, currentbutton;
    boolean first;
    Color defaultColor;
    TextButton[] buttons;

    public LevelSelectScreen() {
        super();
        skin = new Skin();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void buildStage() {

        skin.addRegions(game.assets.get("menus.pack", TextureAtlas.class));
        Image background = new Image((Texture) game.assets.get("levels/Background.png"));
        background.setSize(getWidth(), getHeight());
        addActor(background);

        Table menuback = new Table();
        menuback.setBounds(0,0,getWidth(),getHeight());
        menuback.setBackground(skin.getDrawable("back"));
        menuback.setColor(menuback.getColor().r,menuback.getColor().g,menuback.getColor().b,0.5f);
        addActor(menuback);

        buttons = new TextButton[10];
        for (int i = 0; i < 10; i++) {
            buttons[i] = UIFactory.createTextButton(skin,"", game.assets.get("myfont.ttf", BitmapFont.class));
            if(game.lastLevel < i+1)
                buttons[i].setText("?");
            else
                buttons[i].setText((i + 1) + "");
        }
        Table levelTable = new Table();
        levelTable.defaults().width(getHeight() / 5).height(getHeight() / 5);

        for (int i = 0; i < 10; i++){
            levelTable.add(buttons[i]);
            if(i == 4)
                levelTable.row();
        }

        for (int i = 0; i < 10; i++){
            if(!(game.lastLevel < i+1))
                buttons[i].addListener(UIFactory.createListener(ScreenEnum.AVATAR_SELECT,i+1));
        }




        Table head = new Table();
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class)
                , Color.WHITE);
        Label title = new Label("Play the level again", labelStyle);title.setFontScale(0.8f);
        head.add(title);

        Table fullscreen = new Table();
        fullscreen.setBounds(getWidth()*0.025f,getHeight()*0.025f,getWidth()*0.95f,getHeight()*0.95f);
        fullscreen.add(head).width(getWidth()*0.75f).height(getHeight()/4);
        fullscreen.row();
        fullscreen.add(levelTable).width(getWidth()*0.75f).height(getHeight()/2);

        addActor(fullscreen);

        currentbutton = 1;previousbutton = 1;first = true;
        defaultColor = new Color(buttons[0].getColor());

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);

        if(game.isAndroidTV)
            tvButtonSelect();
    }


    private void tvButtonSelect(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
            previousbutton = currentbutton;
            if(currentbutton == 1)
                currentbutton = game.lastLevel;
            else
                currentbutton--;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            previousbutton = currentbutton;
            if(currentbutton == game.lastLevel)
                currentbutton = 1;
            else
                currentbutton++;
        }

        if(first || currentbutton != previousbutton) {

            buttons[currentbutton-1].setColor(Color.MAROON);

            if(!first) {
                buttons[previousbutton].setColor(defaultColor);
            }
            first = false;


        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)) {
            ScreenManager.getInstance().showScreen(ScreenEnum.AVATAR_SELECT,currentbutton);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("dispose levelSelect");
    }
}
