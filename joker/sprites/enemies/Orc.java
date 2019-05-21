package itu.joker.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

import itu.joker.Main;
import itu.joker.screens.GameScreen;
import itu.joker.sprites.Health_Bar;
import itu.joker.sprites.MyBody;

public class Orc extends MyBody {

    public int id;
    private itu.joker.sprites.Player player;
    public boolean canremove;
    public Health_Bar health_bar;

    public Orc(GameScreen screen, Vector2 pos, int health, int attackPower) {
        super(screen);
        this.id = rand.nextInt(3);
        this.player = screen.getPlayer();

        health_bar = new Health_Bar(screen,this);

        runningRight = false;
        initOrcAnimations(id);

        createBody(pos);
        b2body.setActive(false);

        this.attackPower = attackPower;
        this.health = health;this.fullhealth = health;

        screen.healthWithNumbers.add(new itu.joker.sprites.HealthWithNumber(screen, this, screen.damageCam));
    }

    @Override
    public void createBody(Vector2 pos) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(pos);
        bdef.fixedRotation = true;
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = Main.ORC_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.PLAYER_BIT |
                Main.OBSTACLE_BIT | Main.ORC_BIT |
                Main.WEAPON_BIT| Main.WALL_BIT;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Main.tileWidth/8 / Main.PPM, Main.tileHeight/4 / Main.PPM);
        fdef.shape = shape;
        fdef.density = 1.f;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape leg = new EdgeShape();
        leg.set(new Vector2(-Main.tileWidth/8 / Main.PPM, -Main.tileHeight/4 / Main.PPM),
                new Vector2(Main.tileWidth/8 / Main.PPM, -Main.tileHeight/4 / Main.PPM));
        fdef.filter.categoryBits = Main.LEG_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT | Main.OBSTACLE_BIT;
        fdef.isSensor = true;
        fdef.shape = leg;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void createWeapon(Vector2 pos) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(pos);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = Main.WEAPON_BIT;
        fdef.filter.maskBits = Main.PLAYER_BIT;
        PolygonShape shape = new PolygonShape();
        weapon = world.createBody(bdef);
        shape.setAsBox(10 / Main.PPM, Main.tileHeight/4 / Main.PPM);
        fdef.shape = shape;
        fdef.density = 0.001f;
        fdef.isSensor = true;
        weapon.createFixture(fdef).setUserData(this);
        try{
            revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.bodyA = b2body;
            revoluteJointDef.bodyB = weapon;
            revoluteJointDef.localAnchorA.set(0, 0);
            revoluteJointDef.localAnchorB.set(0, -Main.tileHeight/4 / Main.PPM);
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
            world.createJoint(revoluteJointDef);}catch (Exception e){}
    }

    @Override
    public void update(float dt) {

        currentState = getState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;

        if (!isDead) {
            if (!player.isDead) {
                if (Math.abs(b2body.getPosition().x - player.b2body.getPosition().x) <= (Main.V_WIDTH / 2 / Main.PPM) + 100/Main.PPM
                        || (player.b2body.getPosition().x < Main.V_WIDTH / 2 / Main.PPM && b2body.getPosition().x < Main.V_WIDTH / Main.PPM))
                    b2body.setActive(true);
                else {
                    b2body.setActive(false);
                    currentState = State.IDLE;
                }
                if (player.b2body.getPosition().x < b2body.getPosition().x - 2*Main.tileWidth / 5 / Main.PPM )
                    b2body.setLinearVelocity(-1, b2body.getLinearVelocity().y);
                else if(player.b2body.getPosition().x > b2body.getPosition().x + 2*Main.tileWidth / 5 / Main.PPM)
                    b2body.setLinearVelocity(1, b2body.getLinearVelocity().y);
                else
                    b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
            }

            //kill orc
            if (b2body.getPosition().y < 0) {
                isDead = true;
                screen.removeBodyArray.add(b2body);
                if(attackkontrol == 0)
                    screen.removeBodyArray.add(weapon);
            }
            if (weaponContact) {
                stateTimer = 0;
                weaponContact = false;
                health -= player.attackPower;
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
                } else {
                    isHurt = true;
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
            if (Math.sqrt(Math.pow(b2body.getPosition().x - player.b2body.getPosition().x, 2) + Math.pow(b2body.getPosition().y - player.b2body.getPosition().y, 2)) < 170 / Main.PPM
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

        getOrcSound();
        setRegion(getFrame(currentState, stateTimer));
        if (!isDead)
            setPosition(b2body.getPosition().x - getWidth() * 0.5f, b2body.getPosition().y - getHeight() * 0.5f);


        previousState = currentState;


    }

    private void getOrcSound(){
        if(screen.getGame().soundOn) {
            if (currentState != previousState)
                runSound = true;
            switch (currentState) {
                case DEAD:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/orc_hit.ogg", Sound.class).play();
                        runSound = false;
                    }
                    break;
                case ATTACKING:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/orc_attack.ogg", Sound.class).play();
                        runSound = false;
                    }

                    break;
                case HURT:
                    if (runSound) {
                        screen.getGame().assets.get("sounds/orc_hit.ogg", Sound.class).play();
                        runSound = false;
                    }
                    break;
            }
        }
    }
    private void initOrcAnimations(int id) {
        String type;
        if (id % 3 == 0) {
            type = "orc1";
            attackSize = new Vector2(294, 256);
            deadSize = new Vector2(294, 256);
            idleSize = new Vector2(294, 256);
            jumpSize = new Vector2(294, 256);
            runSize = new Vector2(294, 256);
            hurtSize = new Vector2(294, 256);
        } else if (id % 3 == 1) {
            type = "orc2";
            attackSize = new Vector2(284, 256);
            deadSize = new Vector2(284, 256);
            idleSize = new Vector2(284, 256);
            jumpSize = new Vector2(284, 256);
            runSize = new Vector2(284, 256);
            hurtSize = new Vector2(284, 256);
        } else {
            type = "orc3";
            attackSize = new Vector2(287, 256);
            deadSize = new Vector2(287, 256);
            idleSize = new Vector2(287, 256);
            jumpSize = new Vector2(287, 256);
            runSize = new Vector2(287, 256);
            hurtSize = new Vector2(287, 256);
        }

        Array<TextureRegion> frames = new Array();
        for (int i = 1; i <= 10; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/orc/orc_animations.pack", TextureAtlas.class).findRegion(type + "/Armature-attack", i), 0, 0, (int) attackSize.x, (int) attackSize.y));
        attackAnimation = new Animation(0.07f, frames);
        frames.clear();

        for (int i = 1; i <= 10; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/orc/orc_animations.pack", TextureAtlas.class).findRegion(type + "/Armature-dead", i), 0, 0, (int) deadSize.x, (int) deadSize.y));
        deadAnimation = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/orc/orc_animations.pack", TextureAtlas.class).findRegion(type + "/Armature-idle", i), 0, 0, (int) idleSize.x, (int) idleSize.y));
        idleAnimation = new Animation(0.1f, frames);
        jumpAnimation = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/orc/orc_animations.pack", TextureAtlas.class).findRegion(type + "/Armature-hurt", i), 0, 0, (int) hurtSize.x, (int) hurtSize.y));
        hurtAnimation = new Animation(0.07f, frames);
        frames.clear();

        for (int i = 0; i <= 9; i++)
            frames.add(new TextureRegion(screen.getGame().assets.get("animations/orc/orc_animations.pack", TextureAtlas.class).findRegion(type + "/Armature-walk", i), 0, 0, (int) runSize.x, (int) runSize.y));
        walkAnimation = new Animation(0.1f, frames);
        frames.clear();
    }

    @Override
    public void draw(Batch batch) {
        if (!isDead || stateTimer < 3)
            super.draw(batch);
        else canremove = true;
    }
}

