package itu.joker.tileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import itu.joker.Main;
import itu.joker.screens.GameScreen;

public class Coin {

    private TiledMap map;
    private GameScreen screen;
    private MapObject object;
    private Rectangle bounds;
    private Body body;
    private World world;
    private Fixture fixture;

    public Coin(GameScreen screen, MapObject object) {
        this.screen = screen;
        this.map = screen.getMap();
        this.object = object;
        this.bounds = ((RectangleMapObject)object).getRectangle();
        this.world = screen.getWorld();


        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Main.PPM , (bounds.getY() + bounds.getHeight() / 2) / Main.PPM);
        body = world.createBody(bdef);
        CircleShape shape = new CircleShape();
        shape.setRadius(bounds.getWidth() / 2 / Main.PPM );
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = Main.COIN_BIT;

        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
    }

    private TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(3);
        return layer.getCell((int)(body.getPosition().x * Main.PPM / Main.tileWidth),
                (int)(body.getPosition().y * Main.PPM / Main.tileHeight));
    }
    public void destroyCoin(){
        getCell().setTile(null);screen.removeBodyArray.add(body);
        if(screen.getGame().soundOn){
            screen.getGame().assets.get("sounds/glass.ogg", Sound.class).play();
        }
        screen.getGame().score += 100;
    }
}

