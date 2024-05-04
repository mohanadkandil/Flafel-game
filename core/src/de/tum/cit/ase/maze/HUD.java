package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class HUD {
    private TextureRegion objectsTexture;
    private List<TextureRegion> heartRegions;
    private int remainingHearts;
    private float hudHeight;
    private int stage;
    private Player player;
    private int oldRemainingHearts = 0;
    private GameTimer timer;
    private BitmapFont font;

    public HUD(int initialHearts, Player player, float levelTime) {
        this.objectsTexture = new TextureRegion(new Texture("objects.png"));
        this.remainingHearts = initialHearts;
        this.heartRegions = createHeartRegions();
        hudHeight = 50f;
        this.player = player; // Set the player
        this.font = new BitmapFont(); // Initialize the font
        timer = new GameTimer(levelTime);
    }

    public float getHudHeight() {
        return hudHeight;
    }

    private List<TextureRegion> createHeartRegions() {
        int numHearts = remainingHearts;
        List<TextureRegion> heartRegions = new ArrayList<>();

        for (int i = 0; i < numHearts; i++) {
            heartRegions.add(extractHeartRegion(objectsTexture));
        }
        return heartRegions;
    }

    private TextureRegion extractHeartRegion(TextureRegion objectsTexture) {
        int heartColumn = 4; // Ensure the range is from 4 to 7
        int heartWidth = 16; // Adjust the width of the heart region
        int heartHeight = 16; // Adjust the height of the heart region

        return new TextureRegion(objectsTexture, heartColumn * heartWidth, 0, heartWidth, heartHeight);
    }

    public void draw(SpriteBatch batch, int screenWidth, int screenHeight, float scale) {
        // Check if heartRegions is not empty
        if (!heartRegions.isEmpty()) {
            float scaledWidth = heartRegions.get(0).getRegionWidth() * scale;
            float scaledHeight = heartRegions.get(0).getRegionHeight() * scale;
            float x = (screenWidth - remainingHearts * (scaledWidth + 5)) / 2f;
            float y = screenHeight - scaledHeight - 10;

            for (int i = 0; i < remainingHearts; i++) {
                batch.draw(heartRegions.get(i), x + i * (scaledWidth + 5), y, scaledWidth, scaledHeight);
            }
        }
        font.getData().setScale(scale); // Set the scale of the font

        // Timer drawing
        float timerX = 30; // Horizontal position of the timer text
        float timerY = screenHeight - 20; // Vertical position of the timer text
        timer.update(Gdx.graphics.getDeltaTime());
        font.draw(batch, timer.getTimeString(), timerX, timerY);

        float scaledWidth = MazeRunnerGame.key.getRegionWidth() * scale;
        float scaledHeight = MazeRunnerGame.key.getRegionHeight() * scale;
        float x = (screenWidth - scaledWidth) / 2; // Center horizontally
        float y = screenHeight - scaledHeight - 50;

        float scaledCoinWidth = MazeRunnerGame.coin.getRegionWidth() * scale;
        float scaledCoinHeight = MazeRunnerGame.coin.getRegionHeight() * scale;
        float keyOpacity = player.isHasKey() ? 1.0f : 0.5f; // Full opacity if the key is collected, else reduced opacity

        batch.setColor(1, 1, 1, keyOpacity); // Set the color with the desired opacity
        batch.draw(MazeRunnerGame.key, x - 70, y, scaledWidth, scaledHeight);
        batch.setColor(1, 1, 1, 1); // Reset color to full opacity

        for (int i = 0; i < player.TOTAL_NUMBER_OF_COINS; i++) {
            float coinOpacity = player.isCoinCollected(i) ? 1.0f : 0.5f;
            batch.setColor(1, 1, 1, coinOpacity); // Set the color with the desired opacity

            float coinX = x + (i * (scaledCoinWidth + 5)) - 20; // Adjust position for each coin
            batch.draw(MazeRunnerGame.coin, coinX, y - 5, scaledCoinWidth, scaledCoinHeight);
        }
        batch.setColor(1, 1, 1, 1); // Reset color to full opacity
    }

    public void decreaseHearts(int amount) {
        remainingHearts -= amount;
        if (remainingHearts <= 0) {
            remainingHearts = 0;
            if (this.player != null) {
                this.player.setWinner(false);
            }
        }
        updateHeartRegions();
    }
    public void increaseHearts() {
        if (remainingHearts < Player.MAX_HEARTS) {
            remainingHearts++;
            AudioManager.playMusic("sound/audio/bonus.wav", false);
            // Directly update the heartRegions list here if needed or call updateHeartRegions
            updateHeartRegions(); // Ensure heartRegions reflects the current number of hearts
        }
    }

    public void setRemainingHearts(int hearts) {
        this.remainingHearts = Math.min(hearts, Player.MAX_HEARTS);
        updateHeartRegions(); // Rebuild the heartRegions list based on the new remainingHearts value
    }

    private void updateHeartRegions() {
        heartRegions.clear(); // Clear the existing heart regions
        for (int i = 0; i < remainingHearts; i++) {
            heartRegions.add(extractHeartRegion(objectsTexture));
        }
    }

    public GameTimer getTimer() {
        return timer;
    }

    public void setTimer(GameTimer timer) {
        this.timer = timer;
    }

    public boolean isTimeUp() {
        return timer.getTimeLeft() <= 0;
    }
    public void dispose() {
        font.dispose();
    }

    public int getRemainingHearts() {
        return remainingHearts;
    }
}