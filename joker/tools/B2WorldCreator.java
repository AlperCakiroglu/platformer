package itu.joker.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import itu.joker.Main;
import itu.joker.screens.GameScreen;
import itu.joker.sprites.enemies.Troll;

public class B2WorldCreator {

    private Array<Troll> trolls;
    private Array<itu.joker.sprites.enemies.Orc> orcs;

    public B2WorldCreator(GameScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //ground
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            if (!object.getProperties().containsKey("player")) {
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);
                body = world.createBody(bdef);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
                fdef.shape = shape;
                if (!object.getProperties().containsKey("wall"))
                    fdef.filter.categoryBits = Main.GROUND_BIT;
                else
                    fdef.filter.categoryBits = Main.WALL_BIT;
                body.createFixture(fdef).setUserData(this);
            } else {
                screen.setPlayer(new itu.joker.sprites.Player(screen,new Vector2((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + 10 + rect.getHeight() / 2) / Main.PPM),
                        screen.getGame().playerHealth, screen.getGame().playerPower,screen.getGame().playerType));
            }
        }

        //obstacle
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);
            body = world.createBody(bdef);
            CircleShape shape = new CircleShape();
            shape.setRadius(rect.getHeight() / 2 / Main.PPM);
            fdef.shape = shape;
            if (object.getProperties().containsKey("chest"))
                fdef.filter.categoryBits = Main.CHEST_BIT;
            else
                fdef.filter.categoryBits = Main.OBSTACLE_BIT;

            body.createFixture(fdef).setUserData(this);
        }

        //coin

        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class))
            new itu.joker.tileObjects.Coin(screen, object);

        //trolls
        trolls = new Array<itu.joker.sprites.enemies.Troll>();
        int id = 0;
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            boolean isBoss;int health;
            if(screen.level%2 == 0)
                health = (screen.level/2)+1;
            else
                health = ((screen.level+1)/2)+1;

            if (object.getProperties().containsKey("boss")) {
                isBoss = true;
                health++;
            }else
                isBoss = false;

            trolls.add(new itu.joker.sprites.enemies.Troll(screen, new Vector2((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + 20 + rect.getHeight() / 2) / Main.PPM)
                    , health, 2, isBoss));
            id++;

        }

        //orcs
        orcs = new Array<itu.joker.sprites.enemies.Orc>();
        id = 0;
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            int health;
            if(screen.level%2 == 0)
                health = screen.level/2;
            else
                health = (screen.level+1)/2;
            if(health>3)
                health = 3;

            orcs.add(new itu.joker.sprites.enemies.Orc(screen, new Vector2((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + 10 + rect.getHeight() / 2) / Main.PPM)
                    , health, 1));
            id++;

        }

    }

    public Array<itu.joker.sprites.enemies.Troll> getTrolls() {
        return trolls;
    }

    public void removeTroll(itu.joker.sprites.enemies.Troll troll) {
        trolls.removeValue(troll, true);
    }

    public Array<itu.joker.sprites.enemies.Orc> getOrcs() {
        return orcs;
    }

    public void removeOrc(itu.joker.sprites.enemies.Orc orc) {
        orcs.removeValue(orc, true);
    }
}
