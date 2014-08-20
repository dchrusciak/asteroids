package eu.javeo.asteroids;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public class Ship {

    enum State { NORMAL, DESTROYED }

    private static final float SPEED = 4f;

    private State state = State.NORMAL;
    private Animation explosionAnimation;
    private Explosion explosion;
    private Sprite sprite;

    public Ship(Texture shipTexture, Animation explosionAnimation) {
        this.sprite = new Sprite(shipTexture);
        this.explosionAnimation = explosionAnimation;
        reset();
    }

    public void reset() {
        sprite.setPosition(Game.SCREEN_WIDTH / 2 - sprite.getWidth() / 2,
                           Game.SCREEN_HEIGHT / 2 - sprite.getHeight() / 2);
    }

    public void update(float touchpadX, float touchpadY) {
        if (isDestroyed()) {
            return;
        }
        move(touchpadX, touchpadY);
        rotate(touchpadX, touchpadY);
    }


    public void draw(SpriteBatch batch) {
        switch (state) {
            case NORMAL:    sprite.draw(batch);   break;
            case DESTROYED: drawExplosion(batch); break;
        }
    }

    public void destroy() {
        if (State.NORMAL.equals(state)) {
            state = State.DESTROYED;
            explosion = new Explosion(explosionAnimation, sprite.getX(), sprite.getY());
        }
    }

    public Vector2 getPosition() {
        return new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
    }

    public boolean isDestroyed() {
        return State.DESTROYED.equals(state);
    }

    public Vector2 getDirection() {
        Vector2 direction = new Vector2(1, 1);
        direction.setAngle(sprite.getRotation() - 90);
        direction.nor();
        return direction;
    }

    private void move(float touchpadX, float touchpadY) {
        float x = (sprite.getX() + touchpadX * SPEED + sprite.getBoundingRectangle().getWidth() / 2) % Game.SCREEN_WIDTH;
        float y = (sprite.getY() + touchpadY * SPEED + sprite.getBoundingRectangle().getHeight() / 2) % Game.SCREEN_HEIGHT;
        if (x < 0) x += Game.SCREEN_WIDTH;
        if (y < 0) y += Game.SCREEN_HEIGHT;
        sprite.setX(x - sprite.getBoundingRectangle().getWidth() / 2);
        sprite.setY(y - sprite.getBoundingRectangle().getHeight() / 2);
    }

    private void rotate(float touchpadX, float touchpadY) {
        if (touchpadX != 0 && touchpadY != 0) {
            float rotation = (float) Math.atan2(touchpadX, -touchpadY) * MathUtils.radiansToDegrees;
            sprite.setRotation(rotation);
        }
    }

    private void drawExplosion(SpriteBatch batch) {
        if (explosion.isFinished()) {
            state = State.NORMAL;
            reset();
        } else {
            explosion.draw(batch);
        }
    }

    public Sprite getSprite() {
        return sprite;
    }
}
