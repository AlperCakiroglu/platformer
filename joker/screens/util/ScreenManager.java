package itu.joker.screens.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import itu.joker.Main;
import itu.joker.screens.AbstractScreen;

public class ScreenManager {

    private static ScreenManager instance;
    private Main game;

    private ScreenManager() {
        super();
    }

    public static ScreenManager getInstance() {
        if (instance == null)
            instance = new ScreenManager();
        return instance;
    }

    public void initialize(Main game) {
        this.game = game;
    }

    public void showScreen(ScreenEnum screenEnum, Object... params) {

        Screen currentScreen = game.getScreen();

        if (currentScreen != null){
            currentScreen.dispose();
            Gdx.input.setCatchBackKey(false);
        }

        AbstractScreen newScreen = screenEnum.getScreen(params);
        newScreen.setGame(game);
        newScreen.initialize();
        newScreen.buildStage();

        game.setScreen(newScreen);



    }
}
