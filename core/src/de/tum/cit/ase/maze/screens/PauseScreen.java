package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import de.tum.cit.ase.maze.MazeRunnerGame;

public class PauseScreen implements Screen {
    private final MazeRunnerGame game;
    private final Stage stage;

    public PauseScreen(final MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage();

        // Create layout table
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Continue game button
        TextButton continueButton = (TextButton) new TextButton("Continue", game.getSkin()).pad(20f, 0f, 20f, 0f);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame();
            }
        });

        // Back to menu button
        TextButton menuButton = (TextButton) new TextButton("Back to Menu", game.getSkin()).pad(20f, 15f, 20f, 15f);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        table.add(continueButton).padBottom(20);
        table.row();
        table.add(menuButton).padBottom(20);
    }

    // Implement other required Screen methods here
    // ...

    @Override
    public void show() {
        // Set the input processor to receive input events
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen and draw the stage (buttons)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    // ... other methods like resize, pause, resume, hide, dispose ...
}
