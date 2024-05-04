package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Shape {
    private Point<Integer, Integer> coordinate;
    private Rectangle rectangleBound;
    private Sprite sprite;
    private SpriteType spriteType;
    private boolean isVisible = true;
    private int identifier = 0;


    public Shape(Point<Integer, Integer> coordinate, float width, float height) {
        this.coordinate = coordinate;
        this.rectangleBound = new Rectangle(coordinate.x, coordinate.y, width, height);
    }
    public Shape(Point<Integer, Integer> coordinate, float width, float height, Sprite sprite) {
        this(coordinate, width, height);
        this.sprite = sprite;
    }
    public Shape(Point<Integer, Integer> coordinate, int size) {
        this(coordinate, size, size);
    }
    public Shape(Point<Integer, Integer> coordinate, TextureRegion texture, SpriteType spriteType) {
        this.coordinate = coordinate;
        this.sprite = new Sprite(texture);
        this.rectangleBound = new Rectangle(coordinate.x * 16, coordinate.y * 16, 16, 16);
        this.sprite.setPosition(this.rectangleBound.x, this.rectangleBound.y);
        this.spriteType = spriteType;
    }
    public Shape(Point<Integer, Integer> coordinate, TextureRegion texture, SpriteType spriteType, int identifier) {
        this.coordinate = coordinate;
        this.sprite = new Sprite(texture);
        this.rectangleBound = new Rectangle(coordinate.x * texture.getRegionWidth(), coordinate.y * texture.getRegionHeight(), texture.getRegionWidth(), texture.getRegionHeight());
        this.sprite.setPosition(this.rectangleBound.x, this.rectangleBound.y);
        this.spriteType = spriteType;
        this.identifier = identifier;
    }
    public Shape(Point<Integer, Integer> coordinate, Texture texture, SpriteType spriteType) {
        this.coordinate = coordinate;
        this.sprite = new Sprite(texture);
        this.rectangleBound = new Rectangle(coordinate.x * texture.getWidth(), coordinate.y * texture.getHeight(), texture.getWidth(), texture.getHeight());        this.sprite.setPosition(this.rectangleBound.x, this.rectangleBound.y);
        this.spriteType = spriteType;
    }
    public Point<Integer, Integer> getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point<Integer, Integer> coordinate) {
        this.coordinate = coordinate;
    }

    public Rectangle getRectangleBound() {
        return rectangleBound;
    }

    public void setRectangleBound(Rectangle rectangleBound) {
        this.rectangleBound = rectangleBound;
    }
    public void scaleBy(float scaleFactor, TextureRegion textureRegion, Batch batch) {

        float originalWidth = textureRegion.getRegionWidth();
        float originalHeight = textureRegion.getRegionHeight();

        float scaledWidth = originalWidth * scaleFactor;
        float scaledHeight = originalHeight * scaleFactor;

        float offsetX = (scaledWidth - originalWidth) / 2; // Calculate offset for X
        float offsetY = (scaledHeight - originalHeight) / 2; // Calculate offset for Y

        float drawX = this.getCoordinate().getX() * originalWidth - offsetX;
        float drawY = this.getCoordinate().getY() * originalHeight - offsetY;

        batch.draw(textureRegion, drawX, drawY, scaledWidth, scaledHeight);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public void setSprite(TextureRegion textureRegion) {
        this.sprite = new Sprite(textureRegion);
    }

    public SpriteType getType() {
        return spriteType;
    }

    public void setSpriteType(SpriteType spriteType) {
        this.spriteType = spriteType;
    }

    public boolean collidesWith(Circle player) {
        return Intersector.overlaps(player, this.getRectangleBound());
    }
    public boolean collidesWith(Rectangle player) {
        return Intersector.overlaps(player, this.getRectangleBound());
    }
    public void hide() {
        isVisible = false;
    }
    public boolean isVisible() {
        return isVisible;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
