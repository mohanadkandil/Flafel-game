package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Map;

public class CollisionEngine {

    public Player player;
    private HUD hud;

   public CollisionEngine() {
        player = new Player();
    }

    public void moveObjectWithCollisionCheck(float deltaX, float deltaY) {
        player.updatePrevPosition(); // Update previous position before moving

        float nextX = player.getX() + deltaX;
        float nextY = player.getY() + deltaY;

        boolean collisionX = checkCollisionAt(nextX, player.getY());
        boolean collisionY = checkCollisionAt(player.getX(), nextY);

        if (!collisionX) player.setX(nextX);
        if (!collisionY) player.setY(nextY);

        handleSpecialCollisions(player.getX(), player.getY());
    }
    // Check if moving to (x, y) will result in a collision with the environment (walls, obstacles)

//    public boolean checkEnemyCollisions(Enemy enemy) {
//        Rectangle enemyHitbox = enemy.getHitbox();
//        return player.spriteMap.values().stream()
//                .filter(shape -> shape.getType() == SpriteType.WALL)
//                .anyMatch(shape -> enemyHitbox.overlaps(shape.getRectangleBound()));
//    }

    // Separate method for checking if the player collides with an enemy
    public boolean checkPlayerEnemyCollision(Rectangle playerHitbox, Rectangle enemyHitbox) {
        return Intersector.overlaps(playerHitbox, enemyHitbox);
    }

    public boolean enemyCheckCollisionAt(Rectangle enemyHitbox) {
        for (Shape shape : player.spriteMap.values()) {
            if (isCollisionEnemy(shape, enemyHitbox)) {
                System.out.println("Collided at wall: " + shape.getCoordinate().getX() + " " + shape.getCoordinate().getY());
                System.out.println("Enemy at: " + enemyHitbox.x + " " + enemyHitbox.y);
                return true;
            }
        }
        return false;
    }


    private boolean isCollisionEnemy(Shape shape, Rectangle objectRectangle) {
        // Check collision with walls
        boolean isWallCollision = shape.isVisible() && shape.collidesWith(objectRectangle)
                && shape.getType() == SpriteType.WALL;

        // Check collision with the exit
        boolean isExitCollision = shape.isVisible() && shape.collidesWith(objectRectangle)
                && shape.getType() == SpriteType.EXIT;

        return isWallCollision || isExitCollision;
    }

    public boolean checkCollisionAt(float x, float y) {
        return player.spriteMap.values().stream()
                .anyMatch(shape -> isCollision(shape, x, y));
    }

    private boolean isCollision(Shape shape, float x, float y) {
        player.playerRectangle.setPosition(x, y + 2f);
        return shape.isVisible() && shape.collidesWith(player.playerRectangle)
                && shape.getType() != SpriteType.TRAP
                && shape.getType() != SpriteType.KEY
                && shape.getType() != SpriteType.POWERUP
                && shape.getType() != SpriteType.EXTRA_LIFE
                && shape.getType() != SpriteType.COIN
                && shape.getType() != SpriteType.ENEMY;
    }

    private void handleSpecialCollisions(float x, float y) {
        player.spriteMap.values().stream()
                .filter(shape -> shape.collidesWith(player.playerRectangle))
                .forEach(shape -> {
                    switch (shape.getType()) {
                        case TRAP -> {
                            AudioManager.playMusic("sound/audio/scream.mp3", false);
                            player.setOnTrap(true);
                            player.setTrapTimer(0);
                        }
                        case KEY -> collectKey(shape);
                        case POWERUP -> {
                            player.collectPowerUp();
                            shape.hide();
                        }
                        case EXTRA_LIFE -> {
                            player.getHud().increaseHearts();
                            shape.hide();
                        }
                        case COIN -> handleCoinCollision(shape);
                        case EXIT -> player.setEnteredExit(true);
                    }
                });
    }

    private void collectKey(Shape shape) {
        shape.hide();
        player.setHasKey(true);
        AudioManager.playMusic("sound/audio/bonus.wav", false);
    }
    private void handleCoinCollision(Shape shape) {
        // Assuming each coin has a unique identifier or index
        int coinIndex = shape.getIdentifier();
        if (!player.isCoinCollected(coinIndex)) {
            if (shape.isVisible()) AudioManager.playMusic("sound/audio/bonus.wav", false);
            player.collectCoin(coinIndex);
            shape.hide();
        }
    }

    private void openExitDoor(Point<Integer, Integer> keyPoint) {
        MazeRunnerGame.spriteMap.values().forEach((object) -> {
            if (object.getType() == SpriteType.EXIT) {
                Point<Integer, Integer> pointKey = object.getCoordinate();
                object.hide();
                MazeRunnerGame.spriteMap.remove(pointKey, object);
                TextureRegion textureRegion = new TextureRegion(MazeRunnerGame.doorOpened);
                Shape newShape = new Shape(pointKey, textureRegion, SpriteType.EXIT);
                MazeRunnerGame.spriteMap.put(pointKey, newShape);
            }
        });
        // If needed, handle key point removal here
        if (keyPoint != null) {
            MazeRunnerGame.spriteMap.remove(keyPoint);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}