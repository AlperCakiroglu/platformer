package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import itu.joker.screens.util.ScreenManager;
import itu.joker.screens.util.UIFactory;

public class MainMenuScreen extends AbstractScreen {

    private Skin skin ;
    private Button voiceButton,musicButton;

    private Button credits, levelSelect, hint, bestscores;
    private TextButton play;
    private boolean first;

    int currentbutton,previousbutton;
    Color defaultColor;

    public MainMenuScreen() {
        super();
        skin = new Skin();

        currentbutton = 1;previousbutton = 1;first = true;
    }

    @Override
    public void buildStage() {

        game.isStory = false;
        game.lives = 2;

        skin.addRegions(game.assets.get("menus.pack", TextureAtlas.class));

        Image background = new Image((Texture)game.assets.get("levels/Background.png"));
        background.setSize(getWidth(),getHeight());
        addActor(background);


        Label.LabelStyle labelStyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class)
                , Color.WHITE);
        Label gameTitle = new Label("Crazy Things", labelStyle);
        gameTitle.setPosition(getWidth()/2 - gameTitle.getWidth()/2 , getHeight()*0.5f
                + gameTitle.getHeight());
        addActor(gameTitle);

        credits = UIFactory.createButton(skin,"credit");
        play = UIFactory.createTextButton(skin,"PLAY",game.assets.get("myfont.ttf", BitmapFont.class));play.getLabel().setFontScale(0.8f);
        levelSelect = UIFactory.createButton(skin,"level");
        //rate = UIFactory.createButton(skin,"rate");
        hint = UIFactory.createButton(skin,"hint");
        bestscores = UIFactory.createButton(skin,"score");

        Table menuTable = new Table();
        menuTable.setBounds(0,0,getWidth(),getHeight()*0.45f);
        menuTable.add(play).colspan(4).width(2*getHeight()/5).height(getHeight()/5).center().padBottom(getHeight()*0.05f);
        menuTable.row();
        menuTable.defaults().width(getHeight()/5).height(getHeight()/5);
        menuTable.add(credits).expand().left();
        menuTable.add(levelSelect);
        menuTable.add(bestscores);
        //menuTable.add(hint);
        menuTable.add(hint).expand().right();
        addActor(menuTable);

        play.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.isStory = true;
                ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.AVATAR_SELECT,1);
            }
        });
        levelSelect.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.LEVEL_SELECT));
        credits.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.CREDITS));
        hint.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.HINTS));
        bestscores.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.SCORES));

        /*rate.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.sharing.rateApp();
            }
        });*/


        voiceButton = UIFactory.createButton(skin,"voice");voiceButton.getStyle().checked = skin.getDrawable("buttons/voiceclick");
        musicButton = UIFactory.createButton(skin,"music");musicButton.getStyle().checked = skin.getDrawable("buttons/musicclick");
        Table soundTable = new Table();
        soundTable.setBounds(getWidth()-0.35f*getHeight(),0.85f*getHeight(),0.35f*getHeight(),0.15f*getHeight());
        soundTable.defaults().width(0.15f*getHeight()).height(0.15f*getHeight());
        soundTable.add(musicButton).padRight(getHeight()*0.05f);
        soundTable.add(voiceButton);
        addActor(soundTable);

        voiceButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(game.soundOn)
                    game.soundOn = false;
                else
                    game.soundOn = true;

                return true;
            }
        });
        musicButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(game.musicOn) {
                    game.musicOn = false;
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).pause();
                }else{
                    game.musicOn = true;
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).setLooping(true);
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).play();
                }

                return true;
            }
        });

        defaultColor = new Color(hint.getColor());
    }

    @Override
    public void render(float delta) {
        if(game.soundOn)
            voiceButton.setChecked(true);
        else
            voiceButton.setChecked(false);

        if(game.musicOn){
            musicButton.setChecked(true);
        }
        else{
            musicButton.setChecked(false);
        }

        super.render(delta);

        if(game.isAndroidTV){
            /*if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)){
                ScreenManager.getInstance().showScreen(ScreenEnum.AVATAR_SELECT,1);
            }*/
            tvButtonSelect();
        }
    }

    private void tvButtonSelect(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
            previousbutton = currentbutton;
            if(currentbutton == 1)
                currentbutton = 7;
            else
                currentbutton--;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            previousbutton = currentbutton;
            if(currentbutton == 7)
                currentbutton = 1;
            else
                currentbutton++;
        }

        if(first || currentbutton != previousbutton) {
            switch (currentbutton) {
                case 1:
                    play.setColor(Color.MAROON);
                    break;
                case 2:
                    credits.setColor(Color.MAROON);
                    break;
                case 3:
                    levelSelect.setColor(Color.MAROON);
                    break;
                case 4:
                    bestscores.setColor(Color.MAROON);
                    break;
                case 5:
                    hint.setColor(Color.MAROON);
                    break;
                /*case 6:
                    rate.setColor(Color.MAROON);
                    break;*/
                case 6:
                    voiceButton.setColor(Color.MAROON);
                    break;
                case 7:
                    musicButton.setColor(Color.MAROON);
                    break;
            }
            if(!first) {
                switch (previousbutton) {
                    case 1:
                        play.setColor(defaultColor);
                        break;
                    case 2:
                        credits.setColor(defaultColor);
                        break;
                    case 3:
                        levelSelect.setColor(defaultColor);
                        break;
                    case 4:
                        bestscores.setColor(defaultColor);
                        break;
                    case 5:
                        hint.setColor(defaultColor);
                        break;
                    /*case 6:
                        rate.setColor(defaultColor);
                        break;*/
                    case 6:
                        voiceButton.setColor(defaultColor);
                        break;
                    case 7:
                        musicButton.setColor(defaultColor);
                        break;
                }
            }
            first = false;


        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)) {
            switch (currentbutton) {
                case 1:
                    game.isStory = true;
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.AVATAR_SELECT,1);
                    break;
                case 2:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.CREDITS);
                    break;
                case 3:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.LEVEL_SELECT);
                    break;
                case 4:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.SCORES);
                    break;
                case 5:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.HINTS);
                    break;
                /*case 6:
                    game.sharing.rateApp();
                    break;*/
                case 6:
                    if(game.soundOn)
                        game.soundOn = false;
                    else
                        game.soundOn = true;
                    break;
                case 7:
                    musicButton.setColor(Color.GREEN);
                    if(game.musicOn) {
                        game.musicOn = false;
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).pause();
                    }else{
                        game.musicOn = true;
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).setLooping(true);
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).play();
                    }
                    break;
            }
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        System.out.println("disposeMenu");
    }
}
