package itu.joker.screens.util;

import itu.joker.screens.AbstractScreen;
import itu.joker.screens.AvatarSelectScreen;
import itu.joker.screens.CreditsScreen;
import itu.joker.screens.GameScreen;
import itu.joker.screens.GameStartingScreen;
import itu.joker.screens.HintsScreen;
import itu.joker.screens.LevelSelectScreen;
import itu.joker.screens.LoadingScreen;
import itu.joker.screens.MainMenuScreen;
import itu.joker.screens.ScoresScreen;

public enum  ScreenEnum {

    MAIN_MENU {
        public AbstractScreen getScreen(Object... params){
            return  new MainMenuScreen();
        }
    },

    LEVEL_SELECT {
        public AbstractScreen getScreen(Object... params){
            return new LevelSelectScreen();
        }
    },

    GAME {
        public AbstractScreen getScreen(Object... params){
            return new GameScreen((Integer) params[0]);
        }
    },

    LOADING {
        public AbstractScreen getScreen(Object... params){
            return new LoadingScreen();
        }
    },

    GAME_STARTING {
        public AbstractScreen getScreen(Object... params){
            return new GameStartingScreen((Integer) params[0]);
        }
    },
    AVATAR_SELECT {
        public AbstractScreen getScreen(Object... params){
            return new AvatarSelectScreen((Integer) params[0]);
        }
    },

    CREDITS {
        public AbstractScreen getScreen(Object... params){
            return new CreditsScreen();
        }
    },

    HINTS {
        public AbstractScreen getScreen(Object... params){
            return new HintsScreen();
        }
    },
    SCORES {
        public AbstractScreen getScreen(Object... params){
            return new ScoresScreen();
        }
    };

    public abstract AbstractScreen getScreen(Object... params);

}
