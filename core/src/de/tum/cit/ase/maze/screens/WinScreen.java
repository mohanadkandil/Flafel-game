package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.AudioManager;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.Player;

/**
 * The LoseScreen class is responsible for displaying lose state for the player
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class WinScreen implements Screen {

    private final Stage stage;
    private TextureRegion bronzeMedal, silverMedal, goldMedal;
    private int score;
    public static AssetManager assetManager;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public WinScreen(MazeRunnerGame game, int score) {
        this.score = score;
        bronzeMedal = new TextureRegion(new Texture(Gdx.files.internal("bronze_medal.png")));
        silverMedal = new TextureRegion(new Texture(Gdx.files.internal("silver_medal.png")));
        goldMedal = new TextureRegion(new Texture(Gdx.files.internal("gold_medal.png")));
        var camera = new OrthographicCamera();
        camera.zoom = 0.9f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch());

        AudioManager.stopAllMusic();
        AudioManager.playMusic("sound/audio/game-win.wav", false);

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("You Win!", game.getSkin(), "title")).padBottom(40).row();

        table.add(new Label("Your Score: " + score, game.getSkin(), "title")).padBottom(20).row();
        TextureRegion medal = getMedalForScore(score);
        if (medal != null) {
            table.add(new Image(medal)).padBottom(20).row();
        }

        // Create and add a button to go to the game screen
        TextButton goToGameMenu = (TextButton) new TextButton("Back to menu", game.getSkin()).pad(20f, 0f, 20f, 0f);
        table.add(goToGameMenu).width(300).padBottom(20).row();
        goToGameMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Player.numberOfLevels++;
                game.player.setWinner(true);
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });

        TextButton goToNextGame = (TextButton) new TextButton("Go to next GAME!", game.getSkin()).pad(20f, 0f, 20f, 0f);
        table.add(goToNextGame).width(300).padBottom(20).row();
        goToNextGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               game.nextLevel();
               Player.numberOfLevels++;
            }
        });

    }

    private TextureRegion getMedalForScore(int score) {
        // Replace these score thresholds with your game's logic
        if (score >= 100) {
            return goldMedal;
        } else if (score >= 50) {
            return silverMedal;
        } else {
            return bronzeMedal;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
        AudioManager.dispose();
    }

    @Override
    public void show() {

        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
