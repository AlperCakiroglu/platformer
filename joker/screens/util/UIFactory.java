package itu.joker.screens.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class UIFactory {

    public static Button createButton(Skin skin, String name){

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = skin.getDrawable("buttons/"+name);
        style.down = skin.getDrawable("buttons/"+name+"click");
        return new Button(style);
    }

    public static TextButton createTextButton(Skin skin, String text, BitmapFont font){
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("buttons/click_button");
        style.down = skin.getDrawable("buttons/normal_button");
        style.font =  font;
        return  new TextButton(text,style);

    }
    public static InputListener createListener(final ScreenEnum dstScreen, final Object... params){
        return new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ScreenManager.getInstance().showScreen(dstScreen, params);

            }
        };
    }
}
