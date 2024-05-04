package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.tum.cit.ase.maze.AudioManager;
import de.tum.cit.ase.maze.MazeRunnerGame;

public class CharacterSelectScreen implements Screen {

    private final Stage stage;
    private final SpriteBatch spriteBatch;
    private final Texture character1Texture;
    private final Texture characterMarioTexture;
    private final Image character1Image;
    private final Image characterMarioImage;
    private CheckBox characterCheckBox;
    private CheckBox characterMarioCheckBox;

    public CharacterSelectScreen(MazeRunnerGame game) {
        spriteBatch = new SpriteBatch();
        stage = new Stage();

        characterCheckBox = new CheckBox("fallah", game.getSkin());
        characterMarioCheckBox = new CheckBox("fello", game.getSkin());

        // Load textures
        MazeRunnerGame.assetManager.load("character.png", Texture.class);
        MazeRunnerGame.assetManager.finishLoading(); // Wait until the asset is fully loaded
        var characterObject = MazeRunnerGame.assetManager.get("character.png", Texture.class);
        var characterMarioObject = MazeRunnerGame.assetManager.get("mario.png", Texture.class);

        var characterRegion = TextureRegion.split(characterObject, 16, 32);
        var characterMarioRegion = TextureRegion.split(characterMarioObject, 64, 64);


        character1Texture = new Texture(Gdx.files.internal("character.png"));
        character1Image = new Image(characterRegion[0][0]);

        characterMarioTexture = new Texture(Gdx.files.internal("mario.png"));
        characterMarioImage = new Image(characterMarioRegion[4][0]);

        Table table = new Table();
        table.setFillParent(true); // The table will fill the stage
        table.add(character1Image).padRight(100); // 20 units of padding on the right
        table.add(characterMarioImage).padBottom(50).row(); // Move to the next row
        table.add(characterCheckBox).padRight(50);
        table.add(characterMarioCheckBox).padLeft(60).row();
        TextButton backToMenuButton = new TextButton("Back to Menu", game.getSkin());
        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        updateCharacterImagePositionsAndScales(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        character1Image.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Your logic here
            }
        });

        characterCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (characterCheckBox.isChecked()) {
                    characterMarioCheckBox.setChecked(false);
//                    MazeRunnerGame.setIsMainCharacter(true);
                    game.getPlayer().switchCharacter(true); // Switch to the main character
                    AudioManager.playMusic("sound/audio/switch.ogg", false);
                }
            }
        });

        characterMarioCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (characterMarioCheckBox.isChecked()) {
                    characterCheckBox.setChecked(false);
//                    MazeRunnerGame.setIsMainCharacter(false);
                    game.getPlayer().switchCharacter(false); // Switch to the main character
                    AudioManager.playMusic("sound/audio/switch.ogg", false);
                }
            }
        });

        table.add(backToMenuButton).colspan(2).padTop(100); // Span two columns and add padding on top
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private void updateCharacterImagePositionsAndScales(int width, int height) {
        // Calculate positions and scales based on screen size
        float scale = 2.0f; // Adjust this scale factor as needed

        float character1X = (width - character1Image.getWidth() * scale) / 2;
        float character1Y = (height - character1Image.getHeight() * scale) / 2;

        float characterMarioX = character1X + character1Image.getWidth() * scale + 20; // 20 pixels space
        float characterMarioY = character1Y;

        // Update positions and scales
        character1Image.setPosition(character1X, character1Y);
        character1Image.setScale(5f);

        characterMarioImage.setPosition(characterMarioX, characterMarioY);
        characterMarioImage.setScale(scale);
    }

    @Override
    public void resize(int width, int height) {
        updateCharacterImagePositionsAndScales(width, height);
    }

    @Override
    public void show() {
        // Implementation not shown for brevity
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.act(delta);
        spriteBatch.begin();
        stage.draw();
        spriteBatch.end();
    }

    @Override
    public void pause() {
        // Implementation not shown for brevity
    }

    @Override
    public void resume() {
        // Implementation not shown for brevity
    }

    @Override
    public void hide() {
        // Implementation not shown for brevity
    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        character1Texture.dispose();
    }
}
