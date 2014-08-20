package eu.javeo.asteroids;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class Missile {
    private final static float SPEED = 7f;
    private Sprite sprite;
    private Vector2 direction;

    public Missile(Texture texture, Vector2 position, Vector2 direction) {
        this.direction = direction;
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(position.x, position.y);
    }

    public void update() {
        sprite.translate(direction.x * SPEED, direction.y * SPEED);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOutOfBounds() {
        return sprite.getX() < 0 || sprite.getX() > Game.SCREEN_WIDTH
            || sprite.getY() < 0 || sprite.getY() > Game.SCREEN_HEIGHT;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
