package itu.joker.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import itu.joker.Main;
import itu.joker.screens.GameScreen;

public class Player extends MyBody {

    public MyBody attackBody;
    private Vector2 startPos;

    public Player(GameScreen screen, Vector2 startPos, int health, int attackPower, int type) {
        super(screen);
        this.startPos = startPos;
        runningRight = true;
        createBody(startPos);
        initTrollAnimations(type);
        this.health = health;this.fullhealth=health;
        this.attackPower = attackPower;

    }

    public void createBody(Vector2 pos) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(pos);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = Main.PLAYER_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.TROLL_BIT | Main.ORC_BIT  |
                Main.OBSTACLE_BIT | Main.COIN_BIT | Main.CHEST_BIT | Main.WALL_BIT;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Main.tileWidth / 5 / Main.PPM , Main.tileHeight / 6 / Main.PPM);
        fdef.shape = shape;
        fdef.density = 1.f;
        b2body.createFixture(fdef).setUserData(this);

        CircleShape circledown = new CircleShape();
        circledown.setRadius(Main.tileWidth / 4 / Main.PPM);
        circledown.setPosition(new Vector2(0,-(Main.tileHeight / 2 - 20) / Main.PPM+ Main.tileWidth / 4 / Main.PPM));
        fdef.shape = circledown;fdef.density = 0.5f;
        b2body.createFixture(fdef).setUserData(this);
        CircleShape circleup = new CircleShape();
        circleup.setRadius(Main.tileWidth / 4 / Main.PPM);
        circleup.setPosition(new Vector2(0,(Main.tileHeight / 2 - 20) / Main.PPM- Main.tileWidth / 4 / Main.PPM));
        fdef.shape = circleup;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.TROLL_BIT | Main.ORC_BIT |
                Main.OBSTACLE_BIT | Main.COIN_BIT | Main.WEAPON_BIT | Main.CHEST_BIT;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape leg = new EdgeShape();
        leg.set(new Vector2(-Main.tileWidth / 4 / Main.PPM, -(Main.tileHeight / 2 - 20) / Main.PPM),
                new Vector2(Main.tileWidth / 4 / Main.PPM, -(Main.tileHeight / 2 - 20) / Main.PPM));
        fdef.filter.categoryBits = Main.LEG_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.OBSTACLE_BIT | Main.COIN_BIT | Main.CHEST_BIT;
        fdef.isSensor = true;
        fdef.shape = leg;
        b2body.createFixture(fdef).setUserData(this);


    }

    public void createWeapon(Vector2 pos) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(pos);
        weapon = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = Main.WEAPON_BIT;
        fdef.filter.maskBits = Main.TROLL_BIT | Main.ORC_BIT | Main.COIN_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20 / Main.PPM, (Main.tileHeight / 2 - 20) / Main.PPM);
        fdef.shape = shape;
        fdef.density = 0.001f;
        fdef.isSensor = true;
        weapon.createFixture(fdef).setUserData(this);

        try{
            revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.bodyA = b2body;
            revoluteJointDef.bodyB = weapon;
            revoluteJointDef.localAnchorA.set(0, 0);
            revoluteJointDef.localAnchorB.set(0, -(Main.tileHeight / 2 - 20) / Main.PPM);
            revoluteJointDef.motorSpeed = 0;
            revoluteJointDef.maxMotorTorque = 1;
            revoluteJointDef.enableMotor = true;
            if (runningRight) {
                revoluteJointDef.lowerAngle = (float) (-100 * Math.PI / 180);
                revoluteJointDef.upperAngle = 0;
            } else {
                revoluteJointDef.lowerAngle = 0;
                revoluteJointDef.upperAngle = (float) (100 * Math.PI / 180);
            }
            revoluteJointDef.enableLimit = true;
            world.createJoint(revoluteJointDef);
        }catch (Exception e){}
    }


    public void update(float dt) {
        currentState = getState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //killplayer
        if ((b2body.getPosition().y < -500/ Main.PPM || screen.getGame().time == 0) && !isDead) {
            stateTimer = 0;
            isDead = true;
            Filter filter = new Filter();
            filter.maskBits = Main.GROUND_BIT;
            for (Fixture fixture : b2body.getFixtureList())
                fixture.setFilterData(filter);
        }
        if (this.weaponContact) {
            this.weaponContact = false;
            health-= attackBody.attackPower;
            screen.damages.add(new DamageAmount(screen,Integer.toString(attackBody.attackPower*50)
                    ,b2body.getPosition(),screen.damageCam));
            if (health <= 0) {
                isDead = true;stateTimer = 0;
                Filter filter = new Filter();
                filter.maskBits = Main.GROUND_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
            } else {
                isHurt = true;
                if (attackkontrol == 0) {
                    screen.removeBodyArray.add(weapon);
                    attackkontrol = 1;
                    attack = false;
                }
            }
        }

        if (attack && attackkontrol == 1 && !isHurt) {
            screen.getGame().bodyCreateCounter++;
            createWeapon(new Vector2(b2body.getPosition().x, b2body.getPosition().y + 100 / Main.PPM));
            if (runningRight)
                revoluteJointDef.motorSpeed = -6;
            else
                revoluteJointDef.motorSpeed = 6;
            world.createJoint(revoluteJointDef);
            attackkontrol = 0;
            screen.getGame().bodyCreateCounter--;
        }


        getSound();
        setRegion(getFrame(currentState, stateTimer));
        if (isFlipX()) {
            if (currentState == State.DEAD)
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.65f);
            else if(currentState == State.ATTACKING)
                setPosition(b2body.getPosition().x - getWidth() * 0.6f, b2body.getPosition().y - getHeight() * 0.3f);
            else if(currentState == State.JUMPING)
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.3f);
            else
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.5f);
        } else {
            if (currentState == State.DEAD)
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.65f);
            else if(currentState == State.ATTACKING)
                setPosition(b2body.getPosition().x - getWidth() * 0.4f, b2body.getPosition().y - getHeight() * 0.3f);
            else if(currentState == State.JUMPING)
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.3f);
            else
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.5f);
        }



        previousState = currentState;


    }


}
