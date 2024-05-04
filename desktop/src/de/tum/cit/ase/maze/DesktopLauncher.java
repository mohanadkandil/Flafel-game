package de.tum.cit.ase.maze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import games.spooky.gdx.nativefilechooser.desktop.DesktopFileChooser;

/**
 * The DesktopLauncher class is the entry point for the desktop version of the Maze Runner game.
 * It sets up the game window and launches the game using LibGDX framework.
 */
public class DesktopLauncher {
	/**
	 * The main method sets up the configuration for the game window and starts the application.
	 *
	 * @param arg Command line arguments (not used in this application)
	 */
	public static void main(String[] arg) {
		// Configuration for the game window
		showSplashScreen();
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Maze Runner"); // Set the window title

		// Get the display mode of the current monitor
		Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
		// Set the window size to 80% of the screen width and height
		config.setWindowedMode(
				Math.round(0.8f * displayMode.width),
				Math.round(0.8f * displayMode.height)
		);
		config.useVsync(true); // Enable vertical sync
		config.setForegroundFPS(60); // Set the foreground frames per second

		// Launch the game
		new Lwjgl3Application(new MazeRunnerGame(new DesktopFileChooser()), config);
	}
	private static void showSplashScreen() {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.disableAudio(true);
		config.setDecorated(false);
		config.setResizable(false);
		config.setTransparentFramebuffer(true);


		new Lwjgl3Application(new ApplicationAdapter() {
			private Batch batch;
			private Texture texture;
			private float time;
			TextureRegion region;
			Sprite sprite;

			@Override
			public void create() {
				// load your PNG
				Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
				texture = new Texture(Gdx.files.classpath("flafelLogo.png"));

				region = new TextureRegion(texture);
				sprite = new Sprite(region);

				sprite.setSize(1f * texture.getWidth(), 1f * texture.getHeight());

				float centerX = (displayMode.width - sprite.getWidth()) / 2;
				float centerY = (displayMode.height - sprite.getHeight()) / 2;
				sprite.setPosition(centerX, centerY);

				batch = new SpriteBatch();


			}
			@Override
			public void render() {

				time += Gdx.graphics.getDeltaTime();

				// render your PNG
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
				batch.begin();
				batch.draw(sprite, 0, 0, 1, 1);
				batch.end();

				if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || time > 2f){
					Gdx.app.exit();
				}
			}
			@Override
			public void dispose() {
				texture.dispose();
				batch.dispose();
			}
		}, config);
	}
}
