package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Batch;
public abstract class Character {
    public abstract void initAnimations();
    public abstract void handleInput(float delta);
    public abstract void update(float delta);
    public abstract void draw(Batch batch);
    public abstract CharacterState getState();
}