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

public class DamageAmount {

    public Stage stage;
    private GameScreen screen;
    private Label label;
    private float stateTimer;
    public boolean isDisposed;
    public Vector2 pos;

    public DamageAmount(GameScreen screen, String damage, Vector2 pos, OrthographicCamera damagecam) {

        this.screen = screen;this.pos = pos;
        stateTimer = 0;
        stage = new Stage(new StretchViewport(256 * 10 * 0.6f, 265 * 7 * 0.6f,damagecam));

        Label.LabelStyle labelstyle = new Label.LabelStyle(screen.getGame().assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);
        label = new Label(damage, labelstyle);
        label.setBounds(pos.x * Main.PPM,pos.y* Main.PPM,stage.getWidth()/10,stage.getHeight()/10);
        label.setFontScale(0.5f);

        stage.addActor(label);


    }

    public void update(float dt){

        label.setPosition(label.getX(),label.getY()+1);
        stateTimer += dt;
        if(stateTimer > 2 && !isDisposed){
            isDisposed = true;
            stage.dispose();

        }
    }
}
