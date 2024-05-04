package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.maze.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private float deltaX;
    private float deltaY;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private float sinusInput = 0f;
    Map<Point<Integer, Integer>, Shape> spriteMap;
    private float characterX = 100f;  // Adjust initial X position as needed
    private float characterY = 100f;  // Adjust initial Y position as needed
    public enum State { RUNNING, IDLE, UP, DOWN };
    Rectangle playerRect;
    float prevX;
    float prevY;
    ShapeRenderer shapeRenderer;
    private Player player;
    private HUD hud;
    private int playerRemainingHearts = 3; // Initial number of hearts
    private OrthographicCamera hudCamera;
    private CollisionEngine collisionEngine;
    float stateTime = 0;
    private float countdownTimer;
    public static boolean isCountdownActive = true;
    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();
    private List<Enemy> enemies = new ArrayList<>();

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        countdownTimer = 3.0f; // Start from 3 seconds
        isCountdownActive = true; // Activate the countdown
        // Create and configure the camera for the game view
        camera = new OrthographicCamera(characterX, characterY);
        camera.setToOrtho(false);
        camera.zoom = 0.17f;
        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
        collisionEngine = new CollisionEngine();
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2f, hudCamera.viewportHeight / 2f, 0);
        hudCamera.update();
    }
    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        if (isCountdownActive) {
            countdownTimer -= delta;
            if (countdownTimer <= 0) {
                isCountdownActive = false;
                countdownTimer = 0;
            }
        } else {
            // existing game logic, including player movement
            game.player.handleInput(delta);  // Handle input
            game.player.update(delta);
        }

        shapeRenderer = new ShapeRenderer();
        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        stateTime += delta;
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);
        // Move text in a circular path as an example of a moving object
        camera.position.set(characterX, characterY,0);
        camera.update();
        camera.position.set(game.getPlayer().getX(), game.getPlayer().getY(), 0);
        camera.update();
        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();
        // Draw the character next to the text
        game.drawFloor(game.getSpriteBatch());
        for (Shape shape : MazeRunnerGame.spriteMap.values()) {
            if (shape.isVisible() && shape.getType() != SpriteType.TRAP && shape.getType() != SpriteType.WALL && shape.getType() != SpriteType.POWERUP && shape.getType() != SpriteType.EXTRA_LIFE && shape.getType() != SpriteType.COIN && shape.getType() != SpriteType.ENEMY) {
                shape.getSprite().draw(game.getSpriteBatch());
            }
            if (shape.getType() == SpriteType.WALL && shape.isVisible()) {
                game.getSpriteBatch().draw(MazeRunnerGame.wall3D.getTexture(), shape.getCoordinate().getX() * 16, shape.getCoordinate().getY() * 16, 16, 16);
            }
            if (shape.getType() == SpriteType.TRAP) {
                shape.scaleBy(0.7f, game.trapAnimation.getKeyFrame(stateTime), game.getSpriteBatch());
            }
            if (shape.getType() == SpriteType.EXTRA_LIFE && shape.isVisible()) {
                shape.scaleBy(0.5f, game.heartAnimation.getKeyFrame(stateTime), game.getSpriteBatch());
            }
            if (shape.getType() == SpriteType.POWERUP && shape.isVisible()) {
                shape.scaleBy(0.5f, game.powerups, game.getSpriteBatch());
            }
            if (shape.getType() == SpriteType.COIN && shape.isVisible()) {
                shape.scaleBy(0.5f, game.coinAnimation.getKeyFrame(stateTime), game.getSpriteBatch());
            }
        }

        for (Enemy enemy : game.enemies) {
            enemy.update(delta, enemies);
            if (collisionEngine.checkPlayerEnemyCollision(game.getPlayer().playerRectangle, enemy.getHitbox())) {
                game.getPlayer().onEnemyCollision();
            }
            enemy.draw(game.getSpriteBatch());

        }

        game.getPlayer().draw(game.getSpriteBatch());

        game.getSpriteBatch().setProjectionMatrix(hudCamera.combined);
        game.getPlayer().getHud().draw(game.getSpriteBatch(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 2.5f);

        if (isCountdownActive) {
            float scaleFactor = 20.0f; // Example scale factor
            TextureRegion countdownFrame = getCountdownFrame(countdownTimer);
            float width = countdownFrame.getRegionWidth() * scaleFactor;
            float height = countdownFrame.getRegionHeight() * scaleFactor;
            float x = (Gdx.graphics.getWidth() - width) / 2;
            float y = (Gdx.graphics.getHeight() - height) / 2;

            game.getSpriteBatch().draw(countdownFrame, x, y, width, height);
            AudioManager.playMusic("sound/audio/3-2-1.mp3", false);
        }

        game.getSpriteBatch().end();
        game.collisionEngine.moveObjectWithCollisionCheck(deltaX, deltaY);
        // TEST all map boxes as blue boxes
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToPauseScreen();
        }
        if (game.player.isPlayerOutOfMap() || game.player.getHud().getRemainingHearts() == 0) {
            ((MazeRunnerGame) Gdx.app.getApplicationListener()).goToLoseScreen();
        }
        if (!game.player.isWinner() && game.player.getHud().isTimeUp()) {
            game.goToLoseScreen();
        }
    }
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    private TextureRegion getCountdownFrame(float timer) {
        if (timer > 2) {
            return game.countdownThreeTexture; // Texture for "3"
        } else if (timer > 1) {
            return game.countdownTwoTexture; // Texture for "2"
        } else {
            return game.countdownOneTexture; // Texture for "1"
        }
    }
    private void update(float delta) {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        hudCamera.setToOrtho(false, width, height);
        hudCamera.position.set(hudCamera.viewportWidth / 2f, hudCamera.viewportHeight / 2f, 0);
        hudCamera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
        playerRect = new Rectangle(characterX, characterY, 16, 20);
        prevY = 0;
        prevX = 0;
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        game.player.getHud().dispose();
    }
    // Additional methods and logic can be added as needed for the game screen
}