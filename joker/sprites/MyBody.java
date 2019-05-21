package itu.joker.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import itu.joker.Main;
import itu.joker.screens.GameScreen;

public abstract class MyBody extends Sprite {

    public enum State {JUMPING, IDLE, RUNNING, DEAD, ATTACKING, HURT}

    public GameScreen screen;
    public World world;
    public Body b2body, weapon;
    public RevoluteJointDef revoluteJointDef;

    public boolean isDead;
    public boolean isHurt;
    public boolean onGround;
    public boolean attack;
    public boolean runningRight;

    public boolean weaponContact;

    public int attackkontrol;
    public float stateTimer;
    public State currentState, previousState;

    public Animation walkAnimation, attackAnimation, deadAnimation, jumpAnimation, idleAnimation, hurtAnimation;
    public Vector2 runSize, attackSize, deadSize, jumpSize, idleSize, hurtSize;

    public boolean runSound;

    public int attackPower;
    public int health;
    public int fullhealth;

    public Random rand;
    public float size;

    public boolean isBoss;

    public MyBody(GameScreen screen){
        this.screen = screen;
        this.world = screen.getWorld();

        rand = new Random();
        stateTimer = 0;
        attackkontrol = 1;
        size = 1;

        currentState = State.IDLE;
        previousState = State.IDLE;

    }

    public State getState(){if (isDead)
        return State.DEAD;
    else if(isHurt)
        return State.HURT;
    else if (attack)
        return State.ATTACKING;
    else if (!onGround)
        return State.JUMPING;
    else if (Math.abs(b2body.getLinearVelocity().x) > 0.5f && onGround )
        return State.RUNNING;
    else
        return State.IDLE;
    }

    public TextureRegion getFrame(State state, float dt) {

        TextureRegion region;
        switch (state) {
            case ATTACKING:
                region = (TextureRegion) attackAnimation.getKeyFrame(dt);
                setSize(attackSize.x / Main.PPM, attackSize.y / Main.PPM);

                if (attackAnimation.isAnimationFinished(dt)) {
                    attack = false;
                    if (attackkontrol == 0) {
                        screen.removeBodyArray.add(weapon);
                        attackkontrol = 1;
                    }
                }
                break;
            case HURT:
                region = (TextureRegion) hurtAnimation.getKeyFrame(dt);
                setSize(hurtSize.x / Main.PPM, hurtSize.y / Main.PPM);
                if (hurtAnimation.isAnimationFinished(dt))
                    isHurt = false;
                break;
            case DEAD:
                region = (TextureRegion) deadAnimation.getKeyFrame(dt);
                setSize(deadSize.x / Main.PPM, deadSize.y / Main.PPM);
                break;

            case RUNNING:
                region = (TextureRegion) walkAnimation.getKeyFrame(dt, true);
                setSize(runSize.x / Main.PPM, runSize.y / Main.PPM);
                break;
            case JUMPING:
                region = (TextureRegion) jumpAnimation.getKeyFrame(dt);
                setSize(jumpSize.x / Main.PPM, jumpSize.y / Main.PPM);
                break;
            case IDLE:
            default:
                region = (TextureRegion) idleAnimation.getKeyFrame(dt, true);
                setSize(idleSize.x / Main.PPM, idleSize.y / Main.PPM);
                break;
        }


        if ((b2body.getLinearVelocity().x >= 1.f || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;


        } else if ((b2body.getLinearVelocity().x <= -1.f || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;

        }

        setSize(getWidth()*size,getHeight()*size);
        return region;


    }

    public void getSound(){
        if(screen.getGame().soundOn) {
            if (currentState != previousState)
                runSound = true;
            switch (currentState) {
                case DEAD:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/troll_hit.ogg", Sound.class).play();
                        runSound = false;
                    }
                    break;
                case ATTACKING:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/troll_attack.ogg", Sound.class).play();
                        runSound = false;
                    }

                    break;
                case HURT:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/troll_hit.ogg", Sound.class).play();
                        runSound = false;
                    }
                    break;
            }
        }
    }
    public void initTrollAnimations(int id){

        String type;
        if (id % 3 == 0) {
            type = "Troll1";
            attackSize = new Vector2(574, 475);
            deadSize = new Vector2(425, 334);
            idleSize = new Vector2(379,257);
            jumpSize = new Vector2(544, 446);
            runSize = new Vector2(424, 267);
            hurtSize = new Vector2(427, 337);
        } else if (id % 3 == 1) {
            type = "Troll2";
            attackSize = new Vector2(579, 503);
            deadSize = new Vector2(425, 346);
            idleSize = new Vector2(376, 259);
            jumpSize = new Vector2(549, 452);
            runSize = new Vector2(424, 279);
            hurtSize = new Vector2(428, 334);
        } else {
            type = "Troll3";
            attackSize = new Vector2(588, 499);
            deadSize = new Vector2(431, 346);
            idleSize = new Vector2(384, 264);
            jumpSize = new Vector2(553, 458);
            runSize = new Vector2(430, 278);
            hurtSize = new Vector2(439, 345);
        }

        Array<TextureRegion> frames = new Array();
        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Attack", i)/*, 0, 0, (int) attackSize.x, (int) attackSize.y*/));
        attackAnimation = new Animation(0.07f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Dead", i)/*, 0, 0, (int) deadSize.x, (int) deadSize.y*/));
        deadAnimation = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Idle", i)/*, 0, 0, (int) idleSize.x, (int) idleSize.y*/));
        idleAnimation = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Jump", i)/*, 0, 0, (int) jumpSize.x, (int) jumpSize.y*/));
        jumpAnimation = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Hurt", i)/*, 0, 0, (int) hurtSize.x, (int) hurtSize.y*/));
        hurtAnimation = new Animation(0.07f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/troll/troll_animations.atlas", TextureAtlas.class).findRegion(type + "/Walk", i)/*, 0, 0, (int) runSize.x, (int) runSize.y*/));
        walkAnimation = new Animation(0.1f, frames);
        frames.clear();



    }

    public abstract void createBody(Vector2 pos);
    public abstract void createWeapon(Vector2 pos);
    public abstract void update(float dt);
}
