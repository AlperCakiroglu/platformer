package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import itu.joker.screens.util.ScreenEnum;
import itu.joker.screens.util.ScreenManager;
import itu.joker.screens.util.UIFactory;

public class AvatarSelectScreen extends AbstractScreen {

    private Skin skin;
    private int level;
    TextButton head1select, head2select, head3select;

    int previousbutton, currentbutton;
    boolean first;
    Color defaultColor;

    public AvatarSelectScreen(Integer level) {
        super();
        this.level = level;
        skin = new Skin();
        Gdx.input.setCatchBackKey(true);

        currentbutton = 1;previousbutton = 1;first = true;
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

        Image head1 = new Image(skin.getDrawable("head1"));
        Image head2 = new Image(skin.getDrawable("head2"));
        Image head3 = new Image(skin.getDrawable("head3"));

        head1select = UIFactory.createTextButton(skin,"Normal",game.assets.get("myfont.ttf", BitmapFont.class));head1select.getLabel().setFontScale(0.5f);
        head2select = UIFactory.createTextButton(skin,"Easy",game.assets.get("myfont.ttf", BitmapFont.class));head2select.getLabel().setFontScale(0.5f);
        head3select = UIFactory.createTextButton(skin,"Hard",game.assets.get("myfont.ttf", BitmapFont.class));head3select.getLabel().setFontScale(0.5f);


        Label.LabelStyle labelstyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);
        Label avatarHead = new Label("Select Difficulty",labelstyle);avatarHead.setFontScale(0.8f);

        Label property1 = new Label("Health:100\nPower:100",labelstyle);property1.setFontScale(0.5f);
        Label property2 = new Label("Health:150\nPower:150",labelstyle);property2.setFontScale(0.5f);
        Label property3 = new Label("Health:150\nPower:50",labelstyle);property3.setFontScale(0.5f);

        Table avatar = new Table();
        avatar.setBounds(0,getHeight()*0.1f,getWidth(),getHeight()*0.8f);

        avatar.add(avatarHead).height(getHeight()*0.1f).colspan(3);
        avatar.row();
        avatar.add(head2).height(getHeight()*0.2f).width(getHeight()*0.2f).pad(getHeight()*0.05f);
        avatar.add(head1).height(getHeight()*0.2f).width(getHeight()*0.2f).pad(getHeight()*0.05f);
        avatar.add(head3).height(getHeight()*0.2f).width(getHeight()*0.2f).pad(getHeight()*0.05f);
        avatar.row();
        avatar.add(property2).height(getHeight()*0.1f).width(getHeight()*0.2f).pad(getHeight()*0.05f).padTop(0).center();
        avatar.add(property1).height(getHeight()*0.1f).width(getHeight()*0.2f).pad(getHeight()*0.05f).padTop(0).center();
        avatar.add(property3).height(getHeight()*0.1f).width(getHeight()*0.2f).pad(getHeight()*0.05f).padTop(0).center();
        avatar.row();
        avatar.add(head2select).height(getHeight()*0.15f).width(getHeight()*0.25f);
        avatar.add(head1select).height(getHeight()*0.15f).width(getHeight()*0.25f);
        avatar.add(head3select).height(getHeight()*0.15f).width(getHeight()*0.25f);

        addActor(avatar);

        head1select.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.bestscores[11]=2;
                game.playerHealth = 2;game.playerPower=2;game.playerType = 0;
                ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
            }
        });
        head2select.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.bestscores[11]=1;
                game.playerHealth = 3;game.playerPower=3;game.playerType = 1;
                ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
            }
        });
        head3select.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.bestscores[11]=3;
                game.playerHealth = 3;game.playerPower=1;game.playerType = 2;
                ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
            }
        });

        defaultColor = new Color(head1select.getColor());
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);

        if(game.isAndroidTV){
            tvButtonSelect();
        }

    }


    private void tvButtonSelect(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
            previousbutton = currentbutton;
            if(currentbutton == 1)
                currentbutton = 3;
            else
                currentbutton--;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            previousbutton = currentbutton;
            if(currentbutton == 3)
                currentbutton = 1;
            else
                currentbutton++;
        }

        if(first || currentbutton != previousbutton) {
            switch (currentbutton) {
                case 1:
                    head2select.setColor(Color.MAROON);
                    break;
                case 2:
                    head1select.setColor(Color.MAROON);
                    break;
                case 3:
                    head3select.setColor(Color.MAROON);
                    break;

            }
            if(!first) {
                switch (previousbutton) {
                    case 1:
                        head2select.setColor(defaultColor);
                        break;
                    case 2:
                        head1select.setColor(defaultColor);
                        break;
                    case 3:
                        head3select.setColor(defaultColor);
                        break;

                }
            }
            first = false;

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)) {
            switch (currentbutton) {
                case 1:
                    game.bestscores[11]=1;
                    game.playerHealth = 1;game.playerPower=3;game.playerType = 1;
                    ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
                    break;
                case 2:
                    game.bestscores[11]=2;
                    game.playerHealth = 2;game.playerPower=2;game.playerType = 0;
                    ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
                    break;
                case 3:
                    game.bestscores[11]=3;
                    game.playerHealth = 3;game.playerPower=1;game.playerType = 2;
                    ScreenManager.getInstance().showScreen(ScreenEnum.GAME_STARTING,level);
                    break;

            }
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("disposeAvatarSelect");
    }
}
