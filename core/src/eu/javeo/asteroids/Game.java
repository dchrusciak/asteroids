package eu.javeo.asteroids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Game extends ApplicationAdapter {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 480;
    public static final int ASTEROIDS_COUNT = 3;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage stage;

    private Texture backgroundTexture;
    private Texture fireButtonTexture;
    private Texture shipTexture;
    private Texture asteroidTexture;
    private Texture explosionTexture;
    private Texture missileTexture;

    private Touchpad touchpad;
    private Button fireButton;

    private Ship ship;
    private Queue<Asteroid> asteroids = new ConcurrentLinkedQueue<Asteroid>();
    private Queue<Missile> missiles = new ConcurrentLinkedQueue<Missile>();

    @Override
    public void create() {
        loadTextures();
        Animation explosionAnimation = createExplosionAnimation();
        batch = new SpriteBatch();
        camera = createCamera();
        touchpad = createTouchpad();
        fireButton = createFireButton();
        ship = new Ship(shipTexture, explosionAnimation);
        asteroids = createAsteroids();
        stage = new Stage(new StretchViewport(SCREEN_WIDTH, SCREEN_HEIGHT), batch);
        stage.addActor(touchpad);
        stage.addActor(fireButton);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        ship.update(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
        fireMissile();
        batch.begin();
        batch.draw(backgroundTexture, 0, 0);
        drawAsteroids();
        drawMissiles();
        ship.draw(batch);
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    private Button createFireButton() {
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(fireButtonTexture));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(buttonDrawable, buttonDrawable, buttonDrawable);
        Button fireButton = new Button(btnStyle);
        fireButton.setBounds(690, 10, 100, 100);
        return fireButton;
    }

    private Animation createExplosionAnimation() {
        TextureRegion[][] explosionFrames = TextureRegion.split(explosionTexture, 134, 134);
        return new Animation(0.1f, explosionFrames[0]);
    }

    private Queue<Asteroid> createAsteroids() {
        for (int i = 0; i < ASTEROIDS_COUNT; i++) {
            asteroids.add(new Asteroid(asteroidTexture, 0, 0, 1f));
        }
        return asteroids;
    }

    private OrthographicCamera createCamera() {
        float aspectRatio = (float) SCREEN_WIDTH / SCREEN_HEIGHT;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 10f * aspectRatio, 10f);
        return camera;
    }

    private Touchpad createTouchpad() {
        Skin touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("analog_stick_bg.png"));
        touchpadSkin.add("touchKnob", new Texture("analog_stick_knob.png"));
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(15, 15, 120, 120);
        return touchpad;
    }

    private void loadTextures() {
        backgroundTexture = new Texture("background.png");
        shipTexture = new Texture("ship.png");
        fireButtonTexture = new Texture("fire.png");
        asteroidTexture = new Texture("asteroid.png");
        explosionTexture = new Texture("explosion.png");
        missileTexture = new Texture("missile.png");
    }

    private void fireMissile() {
        if (!ship.isDestroyed() && fireButton.isPressed()) {
            Missile missile = new Missile(missileTexture, ship.getPosition(), ship.getDirection());
            missiles.add(missile);
        }
    }

    private void drawMissiles() {
        Iterator<Missile> iterator = missiles.iterator();
        while (iterator.hasNext()) {
            Missile missile = iterator.next();
            missile.update();
            missile.draw(batch);
            if (missile.isOutOfBounds()) {
                iterator.remove();
            }
        }
    }

    private void drawAsteroids() {
        Iterator<Asteroid> iterator = asteroids.iterator();
        while (iterator.hasNext()) {
            Asteroid asteroid = iterator.next();
            asteroid.update();
            asteroid.draw(batch);
            asteroid.checkCollisionWithShip(ship);
            if (asteroid.checkCollisionWithMissile(missiles)) {
                burst(asteroid);
                iterator.remove();
            }
        }
    }

    private void burst(Asteroid asteroid) {
        if (asteroid.getSprite().getScaleX() > 0.3) {
            asteroids.add(createAsteroid(asteroid.getSprite()));
            asteroids.add(createAsteroid(asteroid.getSprite()));
        }
    }

    private Asteroid createAsteroid(Sprite asteroid) {
        return new Asteroid(asteroidTexture, asteroid.getX(), asteroid.getY(), asteroid.getScaleX() * 0.7f);
    }
}
