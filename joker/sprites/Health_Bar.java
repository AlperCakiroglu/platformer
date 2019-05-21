package itu.joker.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import itu.joker.Main;
import itu.joker.screens.GameScreen;

public class Health_Bar extends Sprite {

    GameScreen screen;
    MyBody body;

    public Health_Bar(GameScreen screen, MyBody body) {
        this.screen = screen;
        this.body = body;

        setRegion(new TextureRegion(screen.getGame().assets.get("menus.pack", TextureAtlas.class).findRegion("health"), 0, 0, 354, 72));
        setSize(300 / Main.PPM, 40 / Main.PPM);
    }

    public void update() {
        if (body.b2body.isActive()) {
            if(body.health<0)body.health = 0;
            if (body.currentState != MyBody.State.ATTACKING && body.currentState != MyBody.State.HURT )
                setBounds(body.b2body.getPosition().x - getWidth() / 2, body.b2body.getPosition().y + body.getHeight() / 2,
                        (200 * ((float) (body.health) / body.fullhealth)) / Main.PPM, getHeight());
            else
                setBounds(body.b2body.getPosition().x - getWidth() / 2, getY(),
                        (200 * ((float) (body.health) / body.fullhealth)) / Main.PPM, getHeight());
        }
    }
}