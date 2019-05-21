package itu.joker.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import itu.joker.Main;
import itu.joker.screens.GameScreen;

public class HealthWithNumber {

    public Stage stage;
    private GameScreen screen;
    private Label label;
    private float stateTimer;
    public boolean isDisposed;
    public MyBody body;

    public HealthWithNumber(GameScreen screen, MyBody body, OrthographicCamera damagecam) {

        this.screen = screen;this.body = body;
        stateTimer = 0;
        stage = new Stage(new StretchViewport(256 * 10 * 0.6f, 265 * 7 * 0.6f,damagecam));

        Label.LabelStyle labelstyle = new Label.LabelStyle(screen.getGame().assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);
        label = new Label(body.health*50+"", labelstyle);
        label.setBounds(body.b2body.getPosition().x * Main.PPM,body.b2body.getPosition().y* Main.PPM,stage.getWidth()/10,stage.getHeight()/10);
        label.setFontScale(0.5f);

        stage.addActor(label);


    }

    public void update(Vector2 pos){

        if(body.isDead && !isDisposed){
            isDisposed = true;
            stage.dispose();
        }
        else {
            label.setText(body.health * 50 + "");
            if(!body.isBoss)
                label.setPosition((pos.x-0.2f) * Main.PPM, (pos.y + 0.8f) * Main.PPM);
            else
                label.setPosition((pos.x-0.2f) * Main.PPM, (pos.y + 1.2f) * Main.PPM);

        }
    }
}