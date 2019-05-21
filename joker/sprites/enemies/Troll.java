package itu.joker.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import itu.joker.Main;
import itu.joker.screens.GameScreen;
import itu.joker.sprites.MyBody;

public class Troll extends MyBody {

    public int id;
    private itu.joker.sprites.Player player;
    public boolean canremove;
    public itu.joker.sprites.Health_Bar health_bar;


    public Troll(GameScreen screen, Vector2 pos, int health, int attackPower, boolean isBoss) {
        super(screen);
        this.player = screen.getPlayer();
        this.id = rand.nextInt(3);

        health_bar = new itu.joker.sprites.Health_Bar(screen, this);

        runningRight = false;
        initTrollAnimations(id);


        if(isBoss && screen.level !=10)size=1.5f;
        else if(isBoss)size = 2.f;

        createBody(pos);
        b2body.setActive(false);
        this.attackPower = attackPower;
        this.health = health;this.fullhealth = health;
        this.isBoss = isBoss;

        screen.healthWithNumbers.add(new itu.joker.sprites.HealthWithNumber(screen, this, screen.damageCam));
    }

    public void createBody(Vector2 pos) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(pos);
        bdef.fixedRotation = true;
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = Main.TROLL_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.PLAYER_BIT |
                Main.OBSTACLE_BIT | Main.TROLL_BIT |
                Main.WEAPON_BIT| Main.WALL_BIT;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size* Main.tileWidth / 4 / Main.PPM, size*(Main.tileHeight / 2 - 20) / Main.PPM);
        fdef.shape = shape;
        fdef.density = 1.f;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape leg = new EdgeShape();
        leg.set(new Vector2(-size* Main.tileWidth / 4 / Main.PPM, -size*(Main.tileHeight / 2 - 20) / Main.PPM),
                new Vector2(size* Main.tileWidth / 4 / Main.PPM, -size*(Main.tileHeight / 2 - 20) / Main.PPM));
        fdef.filter.categoryBits = Main.LEG_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.OBSTACLE_BIT;
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
        fdef.filter.maskBits = Main.PLAYER_BIT;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size*20 / Main.PPM, size*(Main.tileHeight / 2 - 20) / Main.PPM);
        fdef.shape = shape;
        fdef.density = 0.001f;
        fdef.isSensor = true;
        weapon.createFixture(fdef).setUserData(this);
        try{
            revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.bodyA = b2body;
            revoluteJointDef.bodyB = weapon;
            revoluteJointDef.localAnchorA.set(0, 0);
            revoluteJointDef.localAnchorB.set(0, -size*(Main.tileHeight / 2 - 20) / Main.PPM);
            revoluteJointDef.motorSpeed = 0;
            revoluteJointDef.maxMotorTorque = size*1;
            revoluteJointDef.enableMotor = true;
            if (runningRight) {
                revoluteJointDef.lowerAngle = (float) (-100 * Math.PI / 180);
                revoluteJointDef.upperAngle = 0;
            } else {
                revoluteJointDef.lowerAngle = 0;
                revoluteJointDef.upperAngle = (float) (100 * Math.PI / 180);
            }
            revoluteJointDef.enableLimit = true;
            world.createJoint(revoluteJointDef);}catch (Exception e){}
    }


    public void update(float dt) {

        currentState = getState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;

        if (!isDead) {
            if (!player.isDead) {
                if (Math.abs(b2body.getPosition().x - player.b2body.getPosition().x) <= (Main.V_WIDTH / 2 / Main.PPM) + 120 / Main.PPM
                        || (player.b2body.getPosition().x < Main.V_WIDTH / 2 / Main.PPM && b2body.getPosition().x < Main.V_WIDTH / Main.PPM)) {
                    b2body.setActive(true);

                }else {
                    b2body.setActive(false);
                    currentState = State.IDLE;
                }
                if (player.b2body.getPosition().x < b2body.getPosition().x - 3* Main.tileWidth / 5 / Main.PPM && !isHurt )
                    b2body.setLinearVelocity(-1, b2body.getLinearVelocity().y);
                else if(player.b2body.getPosition().x > b2body.getPosition().x + 3* Main.tileWidth / 5 / Main.PPM  && !isHurt)
                    b2body.setLinearVelocity(1, b2body.getLinearVelocity().y);
                else
                    b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }

            //kill troll
            if (b2body.getPosition().y < -500 / Main.PPM) {
                isDead = true;
                screen.removeBodyArray.add(b2body);
                if(attackkontrol == 0)
                    screen.removeBodyArray.add(weapon);
                if(isBoss)
                    screen.getGame().isBossDead = true;
            }
            if (weaponContact) {
                stateTimer = 0;
                weaponContact = false;
                health-= player.attackPower;
                screen.damages.add(new itu.joker.sprites.DamageAmount(screen,Integer.toString(player.attackPower*50),b2body.getPosition(),screen.damageCam));
                if (health <= 0) {
                    isDead = true;
                    screen.removeBodyArray.add(b2body);
                    if(attackkontrol == 0){

                        Filter filter = new Filter();
                        filter.categoryBits = Main.DESTROYED_BIT;
                        weapon.getFixtureList().first().setFilterData(filter);

                        screen.removeBodyArray.add(weapon);

                    }

                    if(isBoss)
                        screen.getGame().isBossDead = true;
                } else {
                    isHurt = true;
                    /////////
                    ///////
                    if (attackkontrol == 0) {

                        Filter filter = new Filter();
                        filter.categoryBits = Main.DESTROYED_BIT;
                        weapon.getFixtureList().first().setFilterData(filter);

                        screen.removeBodyArray.add(weapon);
                        attackkontrol = 1;
                        attack = false;
                    }
                }
            }
            //attacking
            if (Math.sqrt(Math.pow(b2body.getPosition().x - player.b2body.getPosition().x, 2) + Math.pow(b2body.getPosition().y - player.b2body.getPosition().y, 2)) < size*300 / Main.PPM
                    && !player.isDead && !isHurt) {
                attack = true;
            }
            if (attack && attackkontrol == 1) {
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

        }


        getSound();
        setRegion(getFrame(currentState, stateTimer));


        if (isFlipX()) {
            if (currentState == State.DEAD)
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.65f);

            else if (currentState == State.ATTACKING)
                setPosition(b2body.getPosition().x - getWidth() * 0.6f, b2body.getPosition().y - getHeight() * 0.3f);
            else if (currentState == State.JUMPING)
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.3f);
            else
                setPosition(b2body.getPosition().x - getWidth() * 0.7f, b2body.getPosition().y - getHeight() * 0.5f);
        } else {
            if (currentState == State.DEAD )
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.65f);

            else if (currentState == State.ATTACKING)
                setPosition(b2body.getPosition().x - getWidth() * 0.4f, b2body.getPosition().y - getHeight() * 0.3f);
            else if (currentState == State.JUMPING)
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.3f);
            else
                setPosition(b2body.getPosition().x - getWidth() * 0.3f, b2body.getPosition().y - getHeight() * 0.5f);
        }

        previousState = currentState;

    }


    @Override
    public void draw(Batch batch) {
        if (!isDead || stateTimer < 3){
            super.draw(batch);
        }
        else canremove = true;


    }
}

