package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
public class Player extends Character {
    private float jumpTime = 0f;
    private float initialX = 20f;
    private float initialY = 15f;
    private float x = initialX; // Current X position
    private float y = initialY; // Current Y position
    private CharacterState currentState;
    private CharacterState previousState;
    private float stateTimer;
    private boolean runningRight;
    public float prevX;
    public float prevY;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterIdleAnimation;
    private Texture walkTexture;
    private Array<TextureRegion> walkFrames;
    public Circle playerFuturePositionCircle;
    public Circle playerCircle;
    public Map<Point<Integer, Integer>, Shape> spriteMap;
    public float scaledWidth;
    public float scaledHeight;
    public TextureRegion currentFrame; // Get the current frame to draw
    private final float radius = 4.0f; // Half of the scaled width
    TextureRegion region;
    private static final float MOVE_SPEED = 40f;
    private float speed = MOVE_SPEED;

    private boolean isJumping = false;
    private CollisionEngine collisionEngine;
    public static int MAX_HEARTS = 3;
    // trap timer
    private boolean isOnTrap = false;
    private float trapTimer = 0;
    private final float trapDuration = 3.0f;
    private float powerUpTimer = 0;
    private final float powerUpDuration = 5.0f;

    private boolean hasKey = false;
    public Rectangle playerRectangle;
    public int playerRectangleWidth = 7;
    public int playerRectangleHeight = 9;
    public boolean isWinner = false;
    private HUD hud;
    private boolean heartDecreasedForTrap = false;
    private int health = MAX_HEARTS;
    private boolean hasPowerUp = false;
    private int numberOfPowerUps;
    private boolean isCoinCollected = false;
    public static List<Boolean> collectedCoins; // Tracks the collected state of each coin
    public int TOTAL_NUMBER_OF_COINS = 2;
    private boolean isEnteredExit = false;
    private float elapsedTime = 0f; // Time elapsed since the start of the level
    private int score = 0;
    private boolean scoreCalculated = false;
    public static final int MAX_TIME = 300; // Example: 300 seconds or 5 minutes for the level
    private float remainingTime = MAX_TIME; // Initialize with the maximum time
    public static int numberOfLevels = 0;
    public static int numberOfCollectedCoins = 0;
    private boolean isRed;
    private float redTimer;
    private static final float RED_DURATION = 3.0f;
    private boolean isHit;
    private float hitCooldownTimer;
    private static final float HIT_COOLDOWN = 3.0f; // Cooldown duration in seconds
    Player () {
        MazeRunnerGame.assetManager.load("mario.png", Texture.class);
        MazeRunnerGame.assetManager.finishLoading();
        if (MazeRunnerGame.isIsMainCharacter()) {
            System.out.println("lol");
            initAnimations();
        } else {
            System.out.println("mario");
            initAnimationsPlayer2();
        }
        currentState = CharacterState.IDLE;
        previousState = CharacterState.IDLE;
        playerRectangle = new Rectangle(x, y, playerRectangleWidth, playerRectangleHeight);
        hud = new HUD(MAX_HEARTS, this, 110); // Pass 'this' to the HUD constructor
        health = MAX_HEARTS;
        hasKey = false;
        collectedCoins = new ArrayList<>();
        // Initialize with all coins as not collected
        for (int i = 0; i < TOTAL_NUMBER_OF_COINS; i++) {
            collectedCoins.add(false);
        }
        prevX = x;
        prevY = y;
        isHit = false;
        hitCooldownTimer = 0;
    }
    @Override
    public void initAnimations() {
        walkTexture = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;
        // libGDX internal Array instead of ArrayList because of performance
        walkFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkTexture, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
        }
        characterUpAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();
        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkTexture, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkTexture, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        characterRightAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

        walkFrames.add(new TextureRegion(walkTexture, 0, 0, frameWidth, frameHeight));
        characterIdleAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();
    }
    public void initAnimationsPlayer2() {
        // Assume "mario.png" is a 4x4 grid of 64x64 pixel frames
        var baseCharacter = MazeRunnerGame.assetManager.get("mario.png", Texture.class);
        int frameWidth = 64;
        int frameHeight = 64;
        int animationFrames = 4; // If each animation has 4 frames

        // Split the baseCharacter texture into regions
        var regions = TextureRegion.split(baseCharacter, frameWidth, frameHeight);

        // Initialize the walkFrames array
        walkFrames = new Array<>();

        // Extract the frames for the up animation (assuming it's on the 1st row)
        for (int col = 3; col < animationFrames + 2; col++) {
            walkFrames.add(regions[4][col]); // Adjust the row index if needed
        }
        characterUpAnimation = new Animation<>(0.1f, walkFrames);

        // Clear walkFrames for the next set of animations
        walkFrames.clear();

        // Extract the frames for the down animation (assuming it's on the 2nd row)
        for (int col = 0; col < animationFrames - 1; col++) {
            walkFrames.add(regions[4][col]); // Adjust the row index if needed
        }
        characterDownAnimation = new Animation<>(0.1f, walkFrames);

        // Clear walkFrames for the next set of animations
        walkFrames.clear();

        // Extract the frames for the right animation (assuming it's on the 3rd row)
        for (int col = 0; col < animationFrames - 1; col++) {
            walkFrames.add(regions[6][col]); // Adjust the row index if needed
        }
        characterRightAnimation = new Animation<>(0.1f, walkFrames);

        // Clear walkFrames for the next set of animations
        walkFrames.clear();

        // Assume idle is the first frame of the down animation (2nd row, 1st column)
        characterIdleAnimation = new Animation<>(0.1f, regions[4][0]);

        walkFrames.clear();

        // No need to clear walkFrames here since we're done setting up animations
    }
    public void onEnemyCollision() {
        // Player loses a heart and turns red
        if (!isHit) {
            AudioManager.playMusic("sound/audio/scream.mp3", false);
            hud.decreaseHearts(1);
            isHit = true;
            hitCooldownTimer = HIT_COOLDOWN;
            isRed = true; // Assuming you still want the player to turn red
            redTimer = RED_DURATION; // Assuming you have a red effect timer
        }
    }
    @Override
    public void draw(Batch batch) {

        if (isOnTrap || isRed) {
            batch.setColor(Color.RED);  // Set color to red
            batch.draw(currentFrame, getX(), getY(), scaledWidth, scaledHeight);
            batch.setColor(Color.WHITE); // Reset color to white
        } else if (currentFrame != null) {
            batch.draw(currentFrame, getX(), getY(), scaledWidth, scaledHeight);
        }
    }

    @Override
    public void update(float delta) {
        currentState = getState();
        currentFrame = getFrame(delta);
        scaledWidth = currentFrame.getRegionWidth() * (MazeRunnerGame.isIsMainCharacter() ? 0.5f : 0.2f);
        scaledHeight = currentFrame.getRegionHeight() * (MazeRunnerGame.isIsMainCharacter() ? 0.5f : 0.2f);
        float deltaX = 0, deltaY = 0;

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        // handle trap timer
        if (isRed || isOnTrap) {
            trapTimer += delta;
            if (trapTimer >= trapDuration) {
                isOnTrap = false;
                trapTimer = 0;
                heartDecreasedForTrap = false;
            } else if (!heartDecreasedForTrap) {
                hud.decreaseHearts(1);
                heartDecreasedForTrap = true;
            }
        }
        if (hasPowerUp) {
            powerUpTimer -= delta;
            if (powerUpTimer <= 0) {
                powerUpTimer = 0;
                hasPowerUp = false;
                setSpeed(MOVE_SPEED); // Reset speed to normal
            }
        }
        if (areAllCoinsCollected()) {
            System.out.println("Completed");
        }
        if (hasKey && isEnteredExit) {
            ((MazeRunnerGame) Gdx.app.getApplicationListener()).goToWinScreen();
        }
        elapsedTime += delta;

        // Calculate score when the level is completed and score is not yet calculated
        if (!scoreCalculated && isWinner) {
            calculateScore();
            scoreCalculated = true;
        }
        if (remainingTime > 0) {
            remainingTime -= delta;
        }
        if (hud.isTimeUp()) {
            isWinner = false;
        }
       if (isHit) {
            hitCooldownTimer -= delta;
            if (hitCooldownTimer <= 0) {
                isHit = false;
            }
        }

        // Update red state timer
        if (isRed) {
            redTimer -= delta;
            if (redTimer <= 0) {
                isRed = false;
            }
        }
    }
    @Override
    public void handleInput(float delta) {
        // Start jumping
        prevY = y;
        prevX = x;
        float deltaX = 0, deltaY = 0;
        if (!Gdx.input.justTouched()) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                deltaX -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                deltaX += speed * delta;
            }
            if (!isJumping) {
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    deltaY += speed * delta;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    deltaY -= speed * delta;
                }
            }
        }
        collisionEngine.moveObjectWithCollisionCheck(deltaX, deltaY);
    }
    public boolean isPlayerOutOfMap() {
        return x < -1 || y < -1;
    }
    public void reset() {
        setX(x + 20);
        setY(y + 15);
        setOnTrap(false);
        hud.setRemainingHearts(MAX_HEARTS);
        setEnteredExit(false);
        setHasKey(false);
        setRed(false);
        setSpeed(MOVE_SPEED);
        collectedCoins.replaceAll(coin -> false);
    }
    public void collectCoin(int index) {
        if (index >= 0 && index < TOTAL_NUMBER_OF_COINS && !collectedCoins.get(index)) {
            collectedCoins.set(index, true);
            numberOfCollectedCoins++;
            AudioManager.playMusic("sound/audio/bonus.wav", false); // Play coin collection sound
        }
    }

    public boolean isCoinCollected(int index) {
        return index >= 0 && index < collectedCoins.size() && collectedCoins.get(index);
    }

    // Reset coins (if needed)
    public void resetCoins() {
        Collections.fill(collectedCoins, Boolean.FALSE);
    }
    public boolean areAllCoinsCollected() {
        return collectedCoins.stream().allMatch(collected -> collected);
    }
    public TextureRegion getFrame(float delta) {
        currentState = getState();
        switch (currentState) {
            case RUNNING -> region = getCharacterRightAnimation().getKeyFrame(stateTimer, true);
            case UP -> region = getCharacterUpAnimation().getKeyFrame(stateTimer, true);
            case DOWN -> region = getCharacterDownAnimation().getKeyFrame(stateTimer, true);
            default -> region = getCharacterIdleAnimation().getKeyFrame(0, true);
        }
        if ((!runningRight) && !region.isFlipX()) {
            region.flip(true, false);
        } else if ((runningRight) && region.isFlipX()) {
            region.flip(true, false);
        }
        return region;
    }
    @Override
    public CharacterState getState() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            runningRight = true;
            prevX = getX() + 1f;
            return CharacterState.RUNNING;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            runningRight = false;
            prevX = getX() - 1f;
            return CharacterState.RUNNING;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            prevY = getY() + 1f;
            return CharacterState.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            prevY = getY() - 1f;
            return CharacterState.DOWN;
        }
        return CharacterState.IDLE;
    }
    public void updatePrevPosition() {
        this.prevX = this.x;
        this.prevY = this.y;
    }
    public void collectPowerUp() {
        if (hasPowerUp) {
            // If already has a power-up, add an extra 10 seconds to the timer
            powerUpTimer = Math.min(powerUpTimer + 10.0f, 20.0f); // Ensures maximum of 20 seconds
            AudioManager.playMusic("sound/audio/bonus.wav", false);
        } else {
            // If no power-up, start the timer
            hasPowerUp = true;
            powerUpTimer = 10.0f;
            setSpeed(70f); // Example speed increase
            AudioManager.playMusic("sound/audio/bonus.wav", false);
        }
    }
    public int calculateScore() {
        // Define maximum values and weights
        final int maxCoins = TOTAL_NUMBER_OF_COINS;
        final int maxTime = MAX_TIME; // Define what MAX_TIME is in your game
        final int maxHearts = MAX_HEARTS;
        final float weightCoins = 0.4f; // 40% weight
        final float weightTime = 0.3f; // 30% weight
        final float weightHearts = 0.3f; // 30% weight

        // Calculate each part of the score
        float scoreCoins = ((float) countCollectedCoins() / maxCoins) * weightCoins;
        float scoreTime = (remainingTime / maxTime) * weightTime; // remainingTime needs to be calculated
        float scoreHearts = ((float) hud.getRemainingHearts() / maxHearts) * weightHearts;

        // Sum up to get the total score
        int totalScore = Math.round((scoreCoins + scoreTime + scoreHearts) * 100);

        return totalScore;
    }

    private int countCollectedCoins() {
        // Assuming collectedCoins is a List<Boolean> tracking each coin's collected state
        return (int) collectedCoins.stream().filter(collected -> collected).count();
    }
    public void switchCharacter(boolean isMainCharacter) {
        MazeRunnerGame.setIsMainCharacter(isMainCharacter);

        if (isMainCharacter) {
            initAnimations(); // Load original character animations
        } else {
            initAnimationsPlayer2(); // Load Mario animations
        }

        // Reset state to idle to use the correct idle animation for the new character
        currentState = CharacterState.IDLE;
        previousState = CharacterState.IDLE;
        stateTimer = 0;

        // Immediately update the current frame to reflect the new character's idle animation
        currentFrame = getFrame(0);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
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

    public CharacterState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(CharacterState currentState) {
        this.currentState = currentState;
    }

    public CharacterState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(CharacterState previousState) {
        this.previousState = previousState;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void setStateTimer(float stateTimer) {
        this.stateTimer = stateTimer;
    }

    public boolean isRunningRight() {
        return runningRight;
    }

    public void setRunningRight(boolean runningRight) {
        this.runningRight = runningRight;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public void setCharacterDownAnimation(Animation<TextureRegion> characterDownAnimation) {
        this.characterDownAnimation = characterDownAnimation;
    }
    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }
    public void setCharacterUpAnimation(Animation<TextureRegion> characterUpAnimation) {
        this.characterUpAnimation = characterUpAnimation;
    }
    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }
    public void setCharacterRightAnimation(Animation<TextureRegion> characterRightAnimation) {
        this.characterRightAnimation = characterRightAnimation;
    }
    public Animation<TextureRegion> getCharacterIdleAnimation() {
        return characterIdleAnimation;
    }
    public void setCharacterIdleAnimation(Animation<TextureRegion> characterIdleAnimation) {
        this.characterIdleAnimation = characterIdleAnimation;
    }
    public Texture getWalkTexture() {
        return walkTexture;
    }
    public void setWalkTexture(Texture walkTexture) {
        this.walkTexture = walkTexture;
    }
    public Array<TextureRegion> getWalkFrames() {
        return walkFrames;
    }
    public void setWalkFrames(Array<TextureRegion> walkFrames) {
        this.walkFrames = walkFrames;
    }
    public Map<Point<Integer, Integer>, Shape> getSpriteMap() {
        return spriteMap;
    }
    public void setSpriteMap(Map<Point<Integer, Integer>, Shape> spriteMap) {
        this.spriteMap = spriteMap;
    }
    public CollisionEngine getCollisionEngine() {
        return collisionEngine;
    }
    public void setCollisionEngine(CollisionEngine collisionEngine) {
        this.collisionEngine = collisionEngine;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isOnTrap() {
        return isOnTrap;
    }

    public void setOnTrap(boolean onTrap) {
        isOnTrap = onTrap;
    }

    public float getTrapTimer() {
        return trapTimer;
    }

    public void setTrapTimer(float trapTimer) {
        this.trapTimer = trapTimer;
    }

    public float getTrapDuration() {
        return trapDuration;
    }

    public boolean isHasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public HUD getHud() {
        return hud;
    }

    public void setHud(HUD hud) {
        this.hud = hud;
    }

    public boolean isHasPowerUp() {
        return hasPowerUp;
    }

    public void setHasPowerUp(boolean hasPowerUp) {
        this.hasPowerUp = hasPowerUp;
    }

    public int getNumberOfPowerUps() {
        return numberOfPowerUps;
    }

    public void setNumberOfPowerUps(int numberOfPowerUps) {
        this.numberOfPowerUps = numberOfPowerUps;
    }

    public boolean isCoinCollected() {
        return isCoinCollected;
    }

    public void setCoinCollected(boolean coinCollected) {
        isCoinCollected = coinCollected;
    }

    public boolean isEnteredExit() {
        return isEnteredExit;
    }

    public void setEnteredExit(boolean enteredExit) {
        isEnteredExit = enteredExit;
    }
    public int getScore() {
        return score;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
}