package itu.joker.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import itu.joker.Main;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case Main.WEAPON_BIT | Main.TROLL_BIT:
                if (fixA.getFilterData().categoryBits == Main.TROLL_BIT)
                    ((itu.joker.sprites.enemies.Troll) fixA.getUserData()).weaponContact = true;
                else
                    ((itu.joker.sprites.enemies.Troll) fixB.getUserData()).weaponContact = true;
                break;
            case Main.WEAPON_BIT | Main.ORC_BIT:
                if (fixA.getFilterData().categoryBits == Main.ORC_BIT)
                    ((itu.joker.sprites.enemies.Orc) fixA.getUserData()).weaponContact = true;
                else
                    ((itu.joker.sprites.enemies.Orc) fixB.getUserData()).weaponContact = true;
                break;
            case Main.WEAPON_BIT | Main.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == Main.PLAYER_BIT) {
                    ((itu.joker.sprites.Player) fixA.getUserData()).weaponContact = true;
                    ((itu.joker.sprites.Player) fixA.getUserData()).attackBody = ((itu.joker.sprites.MyBody) fixB.getUserData());
                } else {
                    ((itu.joker.sprites.Player) fixB.getUserData()).weaponContact = true;
                    ((itu.joker.sprites.Player) fixB.getUserData()).attackBody = ((itu.joker.sprites.MyBody) fixA.getUserData());
                }
                break;
            case Main.LEG_BIT | Main.OBSTACLE_BIT:
            case Main.LEG_BIT | Main.GROUND_BIT:
            case Main.LEG_BIT | Main.COIN_BIT:
                if (fixA.getFilterData().categoryBits == Main.LEG_BIT)
                    ((itu.joker.sprites.MyBody) fixA.getUserData()).onGround = true;
                else
                    ((itu.joker.sprites.MyBody) fixB.getUserData()).onGround = true;
                break;
            case Main.WEAPON_BIT | Main.COIN_BIT:
                if (fixA.getFilterData().categoryBits == Main.COIN_BIT)
                    ((itu.joker.tileObjects.Coin) fixA.getUserData()).destroyCoin();
                else
                    ((itu.joker.tileObjects.Coin) fixB.getUserData()).destroyCoin();
                break;
            case Main.PLAYER_BIT | Main.CHEST_BIT:
                if(fixA.getFilterData().categoryBits == Main.PLAYER_BIT){
                    if(((itu.joker.sprites.Player) fixA.getUserData()).screen.getGame().isBossDead)
                        ((itu.joker.sprites.Player) fixA.getUserData()).screen.levelPassed = true;
                }else{
                    if(((itu.joker.sprites.Player) fixB.getUserData()).screen.getGame().isBossDead)
                        ((itu.joker.sprites.Player) fixB.getUserData()).screen.levelPassed = true;
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case Main.LEG_BIT | Main.OBSTACLE_BIT:
            case Main.LEG_BIT | Main.GROUND_BIT:
            case Main.LEG_BIT | Main.COIN_BIT:
                if (fixA.getFilterData().categoryBits == Main.LEG_BIT)
                    ((itu.joker.sprites.MyBody) fixA.getUserData()).onGround = false;
                else
                    ((itu.joker.sprites.MyBody) fixB.getUserData()).onGround = false;
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}