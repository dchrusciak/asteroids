package eu.javeo.asteroids;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.Iterator;
import java.util.Queue;


public class Asteroid {

    private Sprite sprite;
    private float rotationSpeed = MathUtils.random(5f);
    private Vector2 direction;

    public Asteroid(Texture texture, float x, float y, float scale) {
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(x, y);
        this.sprite.setScale(scale);
        this.direction = new Vector2(MathUtils.random(3f), MathUtils.random(3f));
    }

    public void update() {
        sprite.translate(direction.x, direction.y);
        sprite.setX(sprite.getX() % Game.SCREEN_WIDTH);
        sprite.setY(sprite.getY() % Game.SCREEN_HEIGHT);
        sprite.rotate(rotationSpeed);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean checkCollisionWithMissile(Queue<Missile> missiles) {
        Iterator<Missile> iterator = missiles.iterator();
        while (iterator.hasNext()) {
            Missile missile = iterator.next();
            if (checkCollision(missile.getSprite())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void checkCollisionWithShip(Ship ship) {
        if (!ship.isDestroyed() && checkCollision(ship.getSprite())) {
            ship.destroy();
        }
    }

    public boolean checkCollision(Sprite other) {
        return sprite.getBoundingRectangle().overlaps(other.getBoundingRectangle());
    }

    public Sprite getSprite() {
        return sprite;
    }
}
