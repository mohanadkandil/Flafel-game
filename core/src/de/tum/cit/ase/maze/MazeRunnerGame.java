package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import org.w3c.dom.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    public static AssetManager assetManager;
    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;
    // UI Skin
    private Skin skin;
    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterIdleAnimation;
    private Array<Integer> tilesPos;
    private TextureRegion floor;
    private TextureRegion surroundings;
    private Texture blockTexture;
    public static TextureRegion key;
    public static TextureRegion coin;
    public static TextureRegion wall;
    private TextureRegion trap;
    public static TextureRegion enemy;
    private TextureRegion hearts;
    public static TextureRegion doorClosed;
    public static TextureRegion doorOpened;
    public TextureRegion powerups;
    public Player player;
    public CollisionEngine collisionEngine;
    public Map<Point<Integer, Integer>, Shape> spriteMaps;
    private int maxX = 0;
    private int maxY = 0;
    public static ConcurrentHashMap<Point<Integer, Integer>, Shape> spriteMap;
    public Array<TextureRegion> animationFrames;
    public Array<TextureRegion> heartsAnimationFrames;
    public Array<TextureRegion> coinsAnimationFrames;
    public TextureRegion[][] trapFrames;
    public TextureRegion[][] countdownFrames;
    public TextureRegion[][] coinFrames;
    public Animation<TextureRegion> trapAnimation;
    public Animation<TextureRegion> heartAnimation;
    public Animation<TextureRegion> coinAnimation;

    private int levelNumber;
    private boolean isGameInProgress = false;
    public TextureRegion[][] countRegion;
    public TextureRegion countdownThreeTexture;
    public TextureRegion countdownTwoTexture;
    public TextureRegion countdownOneTexture;
    public static TextureRegion wall3D;
    public static TextureRegion floor3D;
    public static int coinCount = 0;
    public static final int TOTAL_LEVELS = 5;
    public static boolean isMainCharacter = true;
    public List<Enemy> enemies;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     *
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        assetManager = new AssetManager();
        enemies = new ArrayList<>();
        assetManager.load("basictiles.png", Texture.class); // Ensure this line is active
        assetManager.load("mobs.png", Texture.class);
        assetManager.load("things.png", Texture.class);
        assetManager.load("objects.png", Texture.class);
        assetManager.load("tilemap.png", Texture.class);
        wall3D = new TextureRegion(new Texture("wall.png"));
        floor3D = new TextureRegion(new Texture("floor2.png"));
        assetManager.finishLoading(); // Wait until the asset is fully loaded
        skin = new Skin(Gdx.files.internal("craft/comic-ui.json")); // Load UI skin
        var objectsRegion = assetManager.get("objects.png", Texture.class);
        countdownFrames = TextureRegion.split(objectsRegion, 16, 16);
        countdownOneTexture = countdownFrames[14][5];
        countdownTwoTexture = countdownFrames[14][6];
        countdownThreeTexture = countdownFrames[14][7];
        skin = new Skin(Gdx.files.internal("craft/comic-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation
        player = new Player();
        collisionEngine = new CollisionEngine();
        collisionEngine.setPlayer(player);
        player.setCollisionEngine(collisionEngine);
        goToMenu(); // Navigate to the menu screen
    }
    public Player getPlayer() {
        return player;
    }

    public void resetGameState() {
        // Reset player state
        player.reset(); // Assuming there's a reset method in Player class
        // Reset or clear any other game state variables
        // ...
    }
    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        this.setScreen(new GameScreen(this)); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    public void goToLoseScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        this.setScreen(new LoseScreen(this));
    }
    public void goToPauseScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        this.setScreen(new PauseScreen(this));
    }
    public void goToWinScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        this.setScreen(new WinScreen(this, player.calculateScore()));
    }
    public void goToMapSelection() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        this.setScreen(new CharacterSelectScreen(this));
    }
    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
        }
        characterUpAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        characterRightAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

        walkFrames.add(new TextureRegion(walkSheet, 0, 0, frameWidth, frameHeight));
        characterIdleAnimation = new Animation<>(0.1f, walkFrames);
        walkFrames.clear();

    }
    public void startLevel(int levelNumber) {
        this.levelNumber = levelNumber;
        enemies.clear(); // Clear existing enemies
        spriteMap = mapSprites(levelNumber); // Load the level map
        player.setSpriteMap(spriteMap);
        resetGameState();
        player.getHud().getTimer().resetTimer();
        goToGame(); // Start the game
    }
    public void nextLevel() {
        if (this.levelNumber < TOTAL_LEVELS) {
            this.levelNumber++;
            startLevel(levelNumber);
        }
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterIdleAnimation() {
        return characterIdleAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public static boolean isIsMainCharacter() {
        return isMainCharacter;
    }

    public static void setIsMainCharacter(boolean isMainCharacter) {
        MazeRunnerGame.isMainCharacter = isMainCharacter;
    }

    public void drawFloor(SpriteBatch spriteBatch) {
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                // Adjust the y position to account for the texture height
                spriteBatch.draw(floor3D, x * 16, y * 16, 16, 16);
            }
        }
    }
    public ConcurrentHashMap<Point<Integer, Integer>, Shape> mapSprites(int levelNumber) {
        String path = "../maps/level-" + levelNumber + ".properties";
        FileHandle file = Gdx.files.internal(path);
        String data = file.readString();
        List<String> filteredCoordinates = Arrays.asList(data.split("\n"));
        ConcurrentHashMap<Point<Integer, Integer>, Shape> cubeMap = new ConcurrentHashMap<>();

        var baseTexture = assetManager.get("basictiles.png", Texture.class);
        var baseMobs = assetManager.get("mobs.png", Texture.class);
        var baseThings = assetManager.get("things.png", Texture.class);
        var baseObjects = assetManager.get("objects.png", Texture.class);
        var tileObjects = assetManager.get("tilemap.png", Texture.class);


        var thingsRegion = TextureRegion.split(baseThings, 16, 16);
        var regions = TextureRegion.split(baseTexture, 16, 16);
        var mobsRegion = TextureRegion.split(baseMobs, 16, 16);
        var tileRegion = TextureRegion.split(tileObjects, 16, 16);
        trapFrames = TextureRegion.split(baseObjects, 16, 16);

        animationFrames = new Array<>();
        heartsAnimationFrames = new Array<>();
        coinsAnimationFrames = new Array<>();


        for (int i = 5; i < 11; i++) {
            animationFrames.add(trapFrames[3][i]); // Adjust indices based on your sprite sheet layout
        }
        for (int i = 4; i < 8; i++) {
            heartsAnimationFrames.add(trapFrames[8][i]); // Adjust indices based on your sprite sheet layout
        }
        for (int i = 0; i < 4; i++) {
            coinsAnimationFrames.add(trapFrames[4][i]); // Adjust indices based on your sprite sheet layout
        }
        trapAnimation = new Animation<>(0.1f, animationFrames, Animation.PlayMode.LOOP);
        heartAnimation = new Animation<>(0.1f, heartsAnimationFrames, Animation.PlayMode.LOOP);
        coinAnimation = new Animation<>(0.1f, coinsAnimationFrames, Animation.PlayMode.LOOP);

        wall = regions[0][2];
        surroundings = regions[1][5];
        floor = regions[8][1];
        trap = regions[7][6];
        enemy = mobsRegion[5][6];
        key = thingsRegion[0][6];
        doorOpened = thingsRegion[3][0];
        doorClosed = thingsRegion[0][0];
        powerups = tileRegion[9][7];
        coin = trapFrames[4][0];
        for (String coordinates : filteredCoordinates) {
            String[] parts = coordinates.split("=|,");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int value = Integer.parseInt(parts[2]);
            TextureRegion texture;
            SpriteType type;
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (value == 1) {
                player.setX(x);
                player.setY(y);
            }
            if (value == 3) {
                texture = trapAnimation.getKeyFrame(0);
                type = SpriteType.TRAP;
                Shape trapShape = new Shape(new Point<>(x, y), texture, type);
                cubeMap.put(new Point<>(x, y), trapShape);
            }
            if (value == 7) {
                texture = heartAnimation.getKeyFrame(0);
                type = SpriteType.EXTRA_LIFE;
                Shape trapShape = new Shape(new Point<>(x, y), texture, type);
                cubeMap.put(new Point<>(x, y), trapShape);
            }
            if (value == 8) {
                texture = coinAnimation.getKeyFrame(0);
                type = SpriteType.COIN;
                Shape trapShape = new Shape(new Point<>(x, y), texture, type, coinCount);
                cubeMap.put(new Point<>(x, y), trapShape);
                coinCount++;
            }
            switch (value) {
                case 0 -> {
                    texture = wall3D;
                    type = SpriteType.WALL;
                }
                case 2 -> {
                    texture = doorClosed;
                    type = SpriteType.EXIT;
                }
                case 4 -> {
                    texture = enemy;
                    type = SpriteType.ENEMY;
                    enemies.add(new Enemy(x * 16, y * 16, 35, collisionEngine, player));
                }
                case 5 -> {
                    texture = key;
                    type = SpriteType.KEY;
                }
                case 6 -> {
                    texture = powerups;
                    type = SpriteType.POWERUP;
                }
                default -> {
                    continue;
                }
            }
            Shape shape = new Shape(new Point<>(x, y), texture, type);
            cubeMap.put(new Point<>(x, y), shape);
        }
        return cubeMap;
    }
}
