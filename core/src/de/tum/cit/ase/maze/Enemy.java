package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static de.tum.cit.ase.maze.screens.GameScreen.isCountdownActive;


public class Enemy {
    private float x, y;
    private Rectangle hitbox;
    private float speed;
    private Player player; // Reference to the player
    private CollisionEngine collisionEngine;
    private Point<Integer, Integer> currentTarget;
    private float targetRecalculationInterval = 0.5f; // Time interval in seconds to recalculate the path
    private float timeSinceLastRecalculation = 0;
    private static final float MIN_DISTANCE_FROM_OTHER_ENEMIES = 16f; // Assuming enemy size is 16x16
    private boolean isStuck = false;
    private float stuckTime = 0;
    private static final float MAX_STUCK_TIME = 1.0f;
    public Enemy(float x, float y, float speed, CollisionEngine collisionEngine, Player player) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.collisionEngine = collisionEngine;
        this.player = player;
        this.hitbox = new Rectangle(x, y, 8, 8);
        this.currentTarget = null;
    }

    public void update(float deltaTime, List<Enemy> allEnemies) {
        if (!isCountdownActive) {

        Vector2 direction = calculateDirectionTowardsPlayer();
        float newX = x + direction.x * speed * deltaTime;
        float newY = y + direction.y * speed * deltaTime;
        Rectangle futureHitbox = new Rectangle(newX, newY, hitbox.width, hitbox.height);

        if (!collisionEngine.enemyCheckCollisionAt(futureHitbox) && isSafeFromOtherEnemies(newX, newY, allEnemies)) {
            // Safe to move
            x = newX;
            y = newY;
        } else {
            // Try random movement to avoid being stuck
            tryRandomMovement(deltaTime, allEnemies);
        }
        hitbox.setPosition(x, y);
        }
    }
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(MazeRunnerGame.enemy, x, y, 8, 8);
    }
    private Vector2 calculateDirectionTowardsPlayer() {
        float dirX = player.getX() - x;
        float dirY = player.getY() - y;
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (length != 0) {
            dirX /= length;
            dirY /= length;
        }
        return new Vector2(dirX, dirY);
    }

    private boolean isSafeFromOtherEnemies(float newX, float newY, List<Enemy> allEnemies) {
        for (Enemy other : allEnemies) {
            if (other != this && new Rectangle(newX, newY, hitbox.width, hitbox.height)
                    .overlaps(new Rectangle(other.x, other.y, other.hitbox.width, other.hitbox.height))) {
                return false;
            }
        }
        return true;
    }

    private void tryRandomMovement(float deltaTime, List<Enemy> allEnemies) {
        List<Vector2> directions = new ArrayList<>();
        directions.add(new Vector2(1, 0));
        directions.add(new Vector2(-1, 0));
        directions.add(new Vector2(0, 1));
        directions.add(new Vector2(0, -1));
        Collections.shuffle(directions);

        for (Vector2 dir : directions) {
            float potentialX = x + dir.x * speed * deltaTime;
            float potentialY = y + dir.y * speed * deltaTime;
            Rectangle potentialHitbox = new Rectangle(potentialX, potentialY, hitbox.width, hitbox.height);

            if (!collisionEngine.enemyCheckCollisionAt(potentialHitbox) && isSafeFromOtherEnemies(potentialX, potentialY, allEnemies)) {
                x = potentialX;
                y = potentialY;
                break;
            }
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}

