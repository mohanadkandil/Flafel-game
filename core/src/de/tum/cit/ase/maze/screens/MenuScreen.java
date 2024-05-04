package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.AudioManager;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.Player;
/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final Image character1Image;
    private final Image characterMarioImage;
    private final Image levelLockImage;
    private final Image coinIconImage;
    private final Image falafelImage;

    private TextureRegion character1Texture;
    private boolean[] levelsUnlocked = new boolean[]{true, false, false, false, false};
    private final Image[] levelLockImages; // Array to hold lock images for each level
    private final Label coinsLabel;
    private final Label falafelLabel;
    private Table statsTable;
    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
//        camera.zoom = 0.9f; // Set camera zoom for a closer view
        levelLockImages = new Image[5]; // Assuming 5 levels

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch());

        AudioManager.stopAllMusic();
//        AudioManager.playMusic("sound/music/background.ogg", true);

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        MazeRunnerGame.assetManager.load("character.png", Texture.class);
        MazeRunnerGame.assetManager.load("mario.png", Texture.class);
        MazeRunnerGame.assetManager.load("lock-up.png", Texture.class);
        MazeRunnerGame.assetManager.load("objects.png", Texture.class);
        MazeRunnerGame.assetManager.load("falafel.png", Texture.class);
        MazeRunnerGame.assetManager.finishLoading(); // Wait until the asset is fully loaded
        var characterObject = MazeRunnerGame.assetManager.get("character.png", Texture.class);
        var characterMainObject = MazeRunnerGame.assetManager.get("mario.png", Texture.class);
        var levelLockObject = MazeRunnerGame.assetManager.get("lock-up.png", Texture.class);
        var falafelObject = MazeRunnerGame.assetManager.get("falafel.png", Texture.class);
        var coinIconObject = MazeRunnerGame.assetManager.get("objects.png", Texture.class);

        var characterRegion = TextureRegion.split(characterObject, 16, 32);
        var characterMarioRegion = TextureRegion.split(characterMainObject, 64, 64);
        var levelLockRegion = new TextureRegion(levelLockObject);
        var coinIconRegion = TextureRegion.split(coinIconObject, 16, 16);
        var falafelRegion = new TextureRegion(falafelObject);

        character1Image = new Image(characterRegion[0][0]);
        characterMarioImage = new Image(characterMarioRegion[4][0]);
        levelLockImage = new Image(levelLockObject);
        coinIconImage = new Image(coinIconRegion[4][0]);
        falafelImage = new Image(falafelRegion);

        coinsLabel = new Label("Coins: " + Player.numberOfCollectedCoins, game.getSkin());
        falafelLabel = new Label("Missions: " + 0, game.getSkin());
        // Calculate position to top right corner


        // Set position and scale
        if (MazeRunnerGame.isIsMainCharacter()) {
            float posX = Gdx.graphics.getWidth() - character1Image.getWidth() - 40; // 20 pixels padding from the right edge
            float posY = Gdx.graphics.getHeight() - character1Image.getHeight() - 40; // 20 pixels padding from the top edge
            character1Image.setPosition(posX, posY);
            character1Image.setScale(2.0f); // Scale up by a factor of 2, adjust as necessary
            character1Image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.goToMapSelection();
                    updateStatsTablePosition();
                }
            });
            // Add the image directly to the stage without using the table for layout
            stage.addActor(character1Image);
        } else {
            float posX = Gdx.graphics.getWidth() - characterMarioImage.getWidth(); // 20 pixels padding from the right edge
            float posY = Gdx.graphics.getHeight() - characterMarioImage.getHeight(); // 20 pixels padding from the top edge
            characterMarioImage.setPosition(posX, posY);
            characterMarioImage.setScale(0.9f); // Scale up by a factor of 2, adjust as necessary
            characterMarioImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.goToMapSelection();
                    updateStatsTablePosition();
                }
            });
            // Add the image directly to the stage without using the table for layout
            stage.addActor(characterMarioImage);
        }

        // Stats Table
        statsTable = new Table();
        statsTable.add(coinIconImage).size(32, 32);
        statsTable.add(coinsLabel).padLeft(10);
        statsTable.row(); // New row
        statsTable.add(falafelImage).size(32, 32).padTop(10);
        statsTable.add(falafelLabel).padLeft(10);

        // Positioning the stats table next to the character image
        float statsPosX = character1Image.getX() - statsTable.getWidth() - 80;
        float statsPosY = character1Image.getY();
        statsTable.setPosition(statsPosX, statsPosY);

        // Add the stats table to the stage
        stage.addActor(statsTable);

        // Add a label as a title
        table.add(new Label("Welcome to FLAFEL!", game.getSkin(), "title")).padBottom(80).row();
        System.out.println(Player.numberOfLevels);
        for (int i = 1; i <= 5; i++) {
            final int level = i;
            TextButton levelButton = new TextButton("Level " + i, game.getSkin());
            levelButton.pad(20f);

            Image lockImage = new Image(MazeRunnerGame.assetManager.get("lock-up.png", Texture.class));
            lockImage.setVisible(!isLevelUnlocked(level)); // Set visibility based on player's progress

            levelLockImages[i - 1] = lockImage; // Store the lock image for later reference

            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (isLevelUnlocked(level)) {
                        game.startLevel(level);
                    }
                }
            });

            table.add(levelButton).width(300).padBottom(20);
            table.add(lockImage).size(32, 32).padBottom(5).row();
        }

        Gdx.input.setInputProcessor(stage);
    }

    private boolean isLevelUnlocked(int levelNumber) {
        return Player.numberOfLevels >= levelNumber - 1;
    }

    public void updateLevelLocks() {
        for (int i = 0; i < levelLockImages.length; i++) {
            levelLockImages[i].setVisible(!isLevelUnlocked(i + 1));
        }
    }
    public void updateStats() {
        coinsLabel.setText("Coins: " + Player.numberOfCollectedCoins);
        falafelLabel.setText("Falafel: " + 0);
    }
    private void updateStatsTablePosition() {
        // Calculate the position so the stats table is always on the screen
        float margin = 20; // Margin from the edge of the screen
        float statsTableWidth = statsTable.getPrefWidth();
        float statsTableHeight = statsTable.getPrefHeight();

        // Position the stats table in the top-right corner of the screen
        float statsTablePosX = Gdx.graphics.getWidth() - statsTableWidth - margin;
        float statsTablePosY = Gdx.graphics.getHeight() - statsTableHeight - margin;

        statsTable.setPosition(statsTablePosX, statsTablePosY);
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
        updateLevelLocks();
        updateStats();
        updateStatsTablePosition(); // Update stats table position when screen is shown
        ((MazeRunnerGame) Gdx.app.getApplicationListener()).resetGameState();
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