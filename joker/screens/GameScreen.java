package itu.joker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import itu.joker.Main;
import itu.joker.screens.util.ScreenManager;
import itu.joker.screens.util.UIFactory;
import itu.joker.sprites.enemies.Orc;
import itu.joker.sprites.enemies.Troll;
import itu.joker.tools.B2WorldCreator;
import itu.joker.tools.WorldContactListener;

public class GameScreen extends AbstractScreen {


    public int level;private int currentlevel;

    private OrthogonalTiledMapRenderer renderer;
    private Viewport gamePort;
    private OrthographicCamera gameCam;
    private Batch batch;
    private Skin skin;
    private Stage gameStage;

    //hud
    private boolean isGamePaused;
    private Table pauseMenu,pauseback,hud,keys,gameOverTable,levelPassedTable ;
    private ShapeRenderer shapeRenderer;
    private Label scoreLabel,timeLabel;
    private Label playerhealthNumber;
    private float heathfirstwidth;
    Image health;
    //kontroll
    MyInput rightButtonInput,leftButtonInput;

    //box2d
    private TiledMap map;
    private World world;
    private Box2DDebugRenderer b2dr;
    public B2WorldCreator creator;
    private itu.joker.sprites.Player player;
    private WorldContactListener worldContactListener;

    //damage
    public Array<itu.joker.sprites.DamageAmount> damages;
    public OrthographicCamera damageCam;
    public Array<itu.joker.sprites.HealthWithNumber> healthWithNumbers;

    //game over and level passed
    private Sound dumbsound;
    private boolean showGameOver,showLevelPassed;
    private float gameOverTime,levelPassedCanQuitTime;
    private Label passedScoreLabel;
    public boolean levelPassed,levelPassedCanQuit;
    private int lastScore = 0;

    //tv dpad
    int previousbutton, currentbutton;
    Button voiceButton,musicButton;
    boolean first;
    Color defaultColor;

    TextButton resume, restart ,quit;///pause menu
    //remove bodies and create bodies outside step
    public Array<Body> removeBodyArray;
    public Array<Vector2> createPlayerArray;
    public Array<Vector2> createTrollArray;
    public Array<Vector2> createOrcArray;

    public GameScreen(Integer level) {
        super();
        this.level = level.intValue();
        currentlevel = level;

        skin = new Skin();
        shapeRenderer = new ShapeRenderer();

        batch = new SpriteBatch();
        gameCam = new OrthographicCamera();
        gamePort = new StretchViewport(Main.V_WIDTH * ((float) 3 / 5) / Main.PPM, Main.V_HEIGHT * ((float) 3 / 5) / Main.PPM, gameCam);
        gameStage = new Stage(gamePort);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        removeBodyArray = new Array<Body>();
        createPlayerArray = new Array<Vector2>();
        createOrcArray = new Array<Vector2>();
        createTrollArray = new Array<Vector2>();
    }

    @Override
    public void initialize() {
        game.isBossDead = false;
        game.time = 100;
        game.score = 0;

        damages = new Array<itu.joker.sprites.DamageAmount>();
        healthWithNumbers = new Array<itu.joker.sprites.HealthWithNumber>();
        damageCam = new OrthographicCamera();

        map = game.assets.get("levels/level" + level + ".tmx", TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        worldContactListener = new WorldContactListener();

        creator = new B2WorldCreator(this);
        world.setContactListener(worldContactListener);

        dumbsound = game.assets.get("sounds/orc_attack.ogg", Sound.class);
        if (game.soundOn && !game.musicOn) {
            dumbsound.loop(0);
        }

    }


    @Override
    public void buildStage() {

        skin.addRegions(game.assets.get("menus.pack", TextureAtlas.class));
        //////////////////hud///////////////////////
        Button pauseMenuButton = UIFactory.createButton(skin, "menu");
        Image health_back = new Image(skin.getDrawable("healthbar"));
        health = new Image(skin.getDrawable("health"));
        Image coin = new Image(skin.getDrawable("coin"));
        Image clock = new Image(skin.getDrawable("clock"));

        Label.LabelStyle labelstyle = new Label.LabelStyle(game.assets.get("myfont.ttf", BitmapFont.class), Color.WHITE);

        scoreLabel = new Label("x " + game.score, labelstyle);
        scoreLabel.setFontScale(0.5f);

        timeLabel = new Label(Math.round(game.time) + "", labelstyle);
        timeLabel.setFontScale(0.5f);


        hud = new Table();
        hud.setBounds(0, getHeight() * 0.9f, getWidth(), getHeight() * 0.1f);

        Stack stack = new Stack();
        stack.add(health_back);
        Table healthTable = new Table();heathfirstwidth = hud.getWidth()/4;
        healthTable.add(health).width(hud.getWidth() / 4).height(hud.getHeight() * 0.8f).expand().padLeft(hud.getWidth() / 4 / 5).padTop(hud.getHeight() * -0.05f);
        stack.add(healthTable);

        playerhealthNumber = new Label(player.health*50+"",labelstyle);playerhealthNumber.setFontScale(0.5f);
        Table healthNumber = new Table();
        healthNumber.add(playerhealthNumber).expand().center();
        stack.add(healthNumber);

        hud.add(stack).width(hud.getWidth() / 3).height(hud.getHeight());

        Table scoreTable = new Table();
        scoreTable.add(coin).width(hud.getHeight()).height(hud.getHeight());
        scoreTable.add(scoreLabel).height(hud.getHeight());
        Table clockTable = new Table();
        clockTable.add(clock).width(hud.getHeight()).height(hud.getHeight());
        clockTable.add(timeLabel).height(hud.getHeight());

        hud.add(scoreTable).width(hud.getWidth() / 3).height(hud.getHeight());
        hud.add(clockTable).width(hud.getWidth() / 4).height(hud.getHeight());
        hud.add(pauseMenuButton).width(getHeight() * 0.1f).height(getHeight() * 0.1f).expand().right();

        if(game.isAndroidTV)
            pauseMenuButton.setVisible(false);

        pauseMenuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isGamePaused = true;
            }
        });

        addActor(hud);

        //keys
        Button right = UIFactory.createButton(skin, "right");
        Button left = UIFactory.createButton(skin, "left");
        Button up = UIFactory.createButton(skin, "up");
        Button hit = UIFactory.createButton(skin, "hit");

        hit.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!player.isDead)
                    player.attack = true;
                return true;
            }
        });
        up.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player.onGround && !player.isDead)
                    player.b2body.applyLinearImpulse(new Vector2(0, 5.f), player.b2body.getWorldCenter(), true);
                return true;
            }
        });

        rightButtonInput = new MyInput();leftButtonInput = new MyInput();
        right.addListener(rightButtonInput);
        left.addListener(leftButtonInput);

        keys = new Table();
        keys.setBounds(0,getHeight()/3,getWidth(),getHeight()/3);
        keys.defaults().width(getHeight()/6).height(getHeight()/6);
        keys.add(up).expand().left();
        keys.add(hit).expand().right();
        keys.row();
        keys.add(left).expand().left();
        keys.add(right).expand().right();

        addActor(keys);
        ///////////////////pause menu//////////////////////////////
        pauseback = new Table();
        pauseback.setBounds(getWidth()/4,0,getWidth()/2,getHeight());
        pauseback.setBackground(skin.getDrawable("back"));
        pauseback.setColor(pauseback.getColor().r,pauseback.getColor().g,pauseback.getColor().b,0.5f);
        addActor(pauseback);

        resume = UIFactory.createTextButton(skin,"Resume",game.assets.get("myfont.ttf",BitmapFont.class));
        restart = UIFactory.createTextButton(skin,"Restart",game.assets.get("myfont.ttf",BitmapFont.class));
        quit = UIFactory.createTextButton(skin,"Quit",game.assets.get("myfont.ttf",BitmapFont.class));
        resume.getLabel().setFontScale(0.6f);restart.getLabel().setFontScale(0.6f);quit.getLabel().setFontScale(0.6f);
        voiceButton = UIFactory.createButton(skin,"voice");voiceButton.getStyle().checked = skin.getDrawable("buttons/voiceclick");
        musicButton = UIFactory.createButton(skin,"music");musicButton.getStyle().checked = skin.getDrawable("buttons/musicclick");
        pauseMenu = new Table();
        pauseMenu.setBounds(getWidth() / 4, getHeight()*0.1f, getWidth() / 2, getHeight() * 0.8f);
        pauseMenu.defaults().width(getWidth() / 4.5f).height(getWidth() / 9).padBottom(getHeight() * 0.01f);
        pauseMenu.add(resume).colspan(2);
        pauseMenu.row();
        pauseMenu.add(restart).colspan(2);
        pauseMenu.row();
        pauseMenu.add(musicButton).width(getWidth()/9).height(getWidth()/9);
        pauseMenu.add(voiceButton).width(getWidth()/9).height(getWidth()/9);
        pauseMenu.row();
        pauseMenu.add(quit).colspan(2);

        resume.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isGamePaused = false;
            }
        });
        restart.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.GAME_STARTING,level));
        quit.addListener(UIFactory.createListener(itu.joker.screens.util.ScreenEnum.MAIN_MENU));
        voiceButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(game.soundOn)
                    game.soundOn = false;
                else
                    game.soundOn = true;

                return true;
            }
        });
        musicButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(game.musicOn) {
                    game.musicOn = false;
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).pause();
                }else{
                    game.musicOn = true;
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).setLooping(true);
                    game.assets.get("musics/bensound-goinghigher.mp3", Music.class).play();
                }

                return true;
            }
        });

        addActor(pauseMenu);

        currentbutton = 1;previousbutton = 1;first = true;
        defaultColor = new Color(musicButton.getColor());

        ////game over
        Label gameOverLabel = new Label("Game Over", labelstyle);
        gameOverTable = new Table();
        gameOverTable.setBounds(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
        gameOverTable.add(gameOverLabel);
        addActor(gameOverTable);

        /////level passed
        String passed;
        if(level == 10)
            passed = "Victory";
        else
            passed = "Level Passed";
        Label levelPassedLabel = new Label(passed, labelstyle);
        passedScoreLabel = new Label(lastScore+"", labelstyle);
        levelPassedTable = new Table();
        levelPassedTable.setBounds(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
        levelPassedTable.add(levelPassedLabel);
        levelPassedTable.row();
        levelPassedTable.add(passedScoreLabel);
        addActor(levelPassedTable);
    }

    private void update(float dt) {

        //camera update
        if (player.b2body.getPosition().y > 0) {
            if (player.b2body.getPosition().x > gamePort.getWorldWidth() / 2 && player.b2body.getPosition().x < (game.tileWidth*100)/game.PPM - gamePort.getWorldWidth() /2)
                gameCam.position.x = player.b2body.getPosition().x;

            //arrange camera height
            if (player.b2body.getPosition().y > gamePort.getWorldHeight() * ((float) 3) / 4 && player.b2body.getPosition().y < gamePort.getWorldHeight() * ((float) 7) / 6)
                gameCam.position.y = player.b2body.getPosition().y;
            else if (player.b2body.getPosition().y > gamePort.getWorldHeight())
                gameCam.position.y = gamePort.getWorldHeight() * ((float) 7) / 6;
            else if (player.b2body.getPosition().y > gamePort.getWorldHeight() / 2)
                gameCam.position.y = player.b2body.getPosition().y;
            else
                gameCam.position.y = gamePort.getWorldHeight() / 2;
        }

        gameCam.update();
        renderer.setView(gameCam);

        damageCam.position.x = gameCam.position.x * game.PPM;
        damageCam.position.y = gameCam.position.y * game.PPM;
        damageCam.update();

        handleKeyboardInput();
        if(game.isAndroidTV && !isGamePaused)
            handleDpadInput();


        if (game.bodyCreateCounter==0)
            world.step(1 / 60.f, 6, 2);

        player.update(dt);

        for (Troll troll : creator.getTrolls()) {

            troll.update(dt);
            troll.health_bar.update();
            if (troll.canremove)
                creator.removeTroll(troll);

        }

        for (Orc orc : creator.getOrcs()) {

            orc.update(dt);
            orc.health_bar.update();
            if (orc.canremove)
                creator.removeOrc(orc);

        }

        for (itu.joker.sprites.DamageAmount damage : damages)
            damage.update(dt);

        for (itu.joker.sprites.HealthWithNumber health:healthWithNumbers)
            health.update(health.body.b2body.getPosition());

        playerhealthNumber.setText(player.health*50+"");
        ///hud update
        game.time -= dt;
        if (game.time < 0) game.time = 0;
        if (player.health < 0) player.health = 0;
        health.setSize(heathfirstwidth * ((float) (player.health) / player.fullhealth), health.getHeight());
        scoreLabel.setText("x " + game.score);
        timeLabel.setText(Math.round(game.time) + "");

        //kontroller
        if (!player.isDead) {
            if (rightButtonInput.activeTouch && player.b2body.getLinearVelocity().x < 4.f)
                player.b2body.applyLinearImpulse(new Vector2(0.5f, 0), player.b2body.getWorldCenter(), true);
            else if (leftButtonInput.activeTouch && player.b2body.getLinearVelocity().x > -4.f)
                player.b2body.applyLinearImpulse(new Vector2(-0.5f, 0), player.b2body.getWorldCenter(), true);
        }




    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        //b2dr.render(world, gameCam.combined);
        gameStage.act(delta);
        gameStage.draw();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.4f));
        if(showGameOver){
            update(delta);
            hud.setVisible(false);pauseMenu.setVisible(false);pauseback.setVisible(false);
            keys.setVisible(false);gameOverTable.setVisible(true);levelPassedTable.setVisible(false);
            shapeRenderer.rect(getWidth()/4, getHeight()/4
                    , getWidth()/2, getHeight() /2);
        }
        else if(showLevelPassed){
            update(delta);
            hud.setVisible(false);pauseMenu.setVisible(false);pauseback.setVisible(false);
            keys.setVisible(false);gameOverTable.setVisible(false);levelPassedTable.setVisible(true);
            shapeRenderer.rect(getWidth()/4, getHeight()/4
                    , getWidth()/2, getHeight() /2);
        }
        else if (!isGamePaused) {
            update(delta);
            hud.setVisible(true);pauseMenu.setVisible(false);pauseback.setVisible(false);
            keys.setVisible(true);gameOverTable.setVisible(false);levelPassedTable.setVisible(false);
            if(game.isAndroidTV)
                keys.setVisible(false);


            shapeRenderer.rect(0, getHeight() * 0.9f
                    , getWidth(), getHeight() * 0.1f);

        } else {
            hud.setVisible(false);pauseMenu.setVisible(true);pauseback.setVisible(true);
            keys.setVisible(false);gameOverTable.setVisible(false);levelPassedTable.setVisible(false);

            if(game.isAndroidTV)
                handleDpadMenu();

            shapeRenderer.rect(0, 0
                    , getWidth(), getHeight());
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //////////////////////
        for(Body body : removeBodyArray)
            world.destroyBody(body);
        removeBodyArray.clear();
        //////////////////////
        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();

        for (Troll troll : creator.getTrolls()) {
            troll.draw(batch);
            if (troll.b2body.isActive() && !troll.isDead)
                troll.health_bar.draw(batch);
        }
        for (Orc orc : creator.getOrcs()) {
            orc.draw(batch);

            if (orc.b2body.isActive() && !orc.isDead)
                orc.health_bar.draw(batch);
        }

        player.draw(batch);

        batch.end();

        batch.setProjectionMatrix(damageCam.combined);
        for (itu.joker.sprites.DamageAmount damage : damages) {
            if (damage.isDisposed)
                damages.removeValue(damage, true);
            else
                damage.stage.draw();
        }
        for(itu.joker.sprites.HealthWithNumber health : healthWithNumbers){
            if (health.isDisposed)
                healthWithNumbers.removeValue(health, true);
            else
                health.stage.draw();
        }

        act(delta);//hud and pause menu
        draw();

        //////game over and level pased

        if (gameOver() && !showGameOver) {
            if (game.lives == 0) {
                game.lives = 2;
                showGameOver = true;
                gameOverTime = 0;
                if(game.soundOn)
                    game.assets.get("sounds/fail.ogg", Sound.class).play();

            } else {
                game.lives--;
                ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.GAME_STARTING, level);
            }
        }

        if(showGameOver){
            gameOverTime+= delta;
            if(gameOverTime > 4)
                ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.MAIN_MENU);
        }

        if(levelPassed && ! showLevelPassed){
            showLevelPassed = true;
            if(game.soundOn)
                game.assets.get("sounds/win.wav", Sound.class).play();
            if(game.score > game.bestscores[level-1])
                game.bestscores[level-1] = game.score;

        }

        if(showLevelPassed){

            if(lastScore + 10 < game.score)
                lastScore+=10;
            else if(!levelPassedCanQuit){
                lastScore = game.score;
                levelPassedCanQuit = true;
                levelPassedCanQuitTime = 0;
            }

            passedScoreLabel.setText(lastScore+"");

            if(levelPassedCanQuit){
                levelPassedCanQuitTime += delta;
                if(levelPassedCanQuitTime > 4){
                    if(game.isStory ) {
                        if(game.lastLevel < level)
                            game.lastLevel = level;
                        if(game.lives<2)
                            game.lives++;

                        game.preferences.putInteger("lastlevel",game.lastLevel);
                        game.preferences.putString("bestscores",game.json.toJson(game.bestscores));
                        game.preferences.flush();
                        level++;
                        if(level == 11)
                            ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.MAIN_MENU);
                        else
                            ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.GAME_STARTING, level);
                    }
                    else {
                        game.preferences.putString("bestscores",game.json.toJson(game.bestscores));
                        game.preferences.flush();
                        ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.MAIN_MENU);
                    }
                }
            }
        }

        ///sounds
        if(game.soundOn)
            voiceButton.setChecked(true);
        else
            voiceButton.setChecked(false);

        if(game.musicOn){
            musicButton.setChecked(true);
        }
        else{
            musicButton.setChecked(false);
        }

        //System.out.println(Gdx.graphics.getFramesPerSecond());
    }

    public boolean gameOver() {
        if (player.isDead && player.stateTimer > 2)
            return true;
        return false;
    }

    private void handleKeyboardInput() {
        if (!player.isDead) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) && player.onGround)
                player.b2body.applyLinearImpulse(new Vector2(0, 5.f), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x < 4.f)
                player.b2body.applyLinearImpulse(new Vector2(0.5f, 0), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x > -4.f)
                player.b2body.applyLinearImpulse(new Vector2(-0.5f, 0), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
                player.attack = true;
        }
    }

    private void handleDpadInput(){
        if (!player.isDead) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_UP) && player.onGround)
                player.b2body.applyLinearImpulse(new Vector2(0, 5.f), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) && player.b2body.getLinearVelocity().x < 4.f)
                player.b2body.applyLinearImpulse(new Vector2(0.5f, 0), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) && player.b2body.getLinearVelocity().x > -4.f)
                player.b2body.applyLinearImpulse(new Vector2(-0.5f, 0), player.b2body.getWorldCenter(), true);
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER))
                player.attack = true;
            else if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_DOWN))
                isGamePaused = true;

        }
    }
    private void handleDpadMenu(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
            previousbutton = currentbutton;
            if(currentbutton == 1)
                currentbutton = 5;
            else
                currentbutton--;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            previousbutton = currentbutton;
            if(currentbutton == 5)
                currentbutton = 1;
            else
                currentbutton++;
        }

        if(first || currentbutton != previousbutton) {
            switch (currentbutton) {
                case 1:
                    resume.setColor(Color.MAROON);
                    break;
                case 2:
                    restart.setColor(Color.MAROON);
                    break;
                case 3:
                    musicButton.setColor(Color.MAROON);
                    break;
                case 4:
                    voiceButton.setColor(Color.MAROON);
                    break;
                case 5:
                    quit.setColor(Color.MAROON);
                    break;
            }
            if(!first) {
                switch (previousbutton) {
                    case 1:
                        resume.setColor(defaultColor);
                        break;
                    case 2:
                        restart.setColor(defaultColor);
                        break;
                    case 3:
                        musicButton.setColor(defaultColor);
                        break;
                    case 4:
                        voiceButton.setColor(defaultColor);
                        break;
                    case 5:
                        quit.setColor(defaultColor);
                        break;
                }
            }
            first = false;

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_CENTER)) {
            switch (currentbutton) {
                case 1:
                    isGamePaused = false;
                    break;
                case 2:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.GAME_STARTING,level);
                    break;
                case 3:
                    if(game.musicOn) {
                        game.musicOn = false;
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).pause();
                    }else{
                        game.musicOn = true;
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).setLooping(true);
                        game.assets.get("musics/bensound-goinghigher.mp3", Music.class).play();
                    }
                    break;
                case 4:
                    if(game.soundOn)
                        game.soundOn = false;
                    else
                        game.soundOn = true;
                    break;
                case 5:
                    ScreenManager.getInstance().showScreen(itu.joker.screens.util.ScreenEnum.MAIN_MENU);
                    break;

            }
        }
    }



    public itu.joker.sprites.Player getPlayer() {
        return player;
    }

    public void setPlayer(itu.joker.sprites.Player player) {
        this.player = player;
    }

    public TiledMap getMap() {
        return map;
    }
    public World getWorld() {
        return world;
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
        game.assets.unload("levels/level"+currentlevel+".tmx");
        renderer.dispose();
        b2dr.dispose();
        batch.dispose();
        skin.dispose();
        gameStage.dispose();
        shapeRenderer.dispose();
        dumbsound.dispose();
        System.out.println("disposeGame");
    }

    private class MyInput extends InputListener {
        public boolean activeTouch;

        public MyInput() {
            this.activeTouch = false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            activeTouch = true;
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            activeTouch = false;
        }
    }
}
