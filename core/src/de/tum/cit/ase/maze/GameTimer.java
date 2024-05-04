package de.tum.cit.ase.maze;

import static de.tum.cit.ase.maze.screens.GameScreen.isCountdownActive;

public class GameTimer {
    private boolean isRunning;
    private float timeLeft; // Time left for the level in seconds
    private String timeString; // String representation of the time
    private float duration;

    public GameTimer(float startTime) {
        this.timeLeft = startTime;
        this.duration = startTime;
        updateTimeString();
    }
    public void update(float delta) {
        if (timeLeft > 0 && !isCountdownActive) {
            timeLeft -= delta;
            updateTimeString();
        } else if (timeLeft <= 0) {
        }
    }
    private void updateTimeString() {
        int minutes = (int) timeLeft / 60;
        int seconds = (int) timeLeft % 60;
        timeString = String.format("%02d:%02d", minutes, seconds);
    }
    public void resetTimer() {
        this.timeLeft = this.duration;
        this.isRunning = true;
        updateTimeString();
    }
    private void onTimeOut() {
        isRunning = false;
    }
    public String getTimeString() {
        return timeString;
    }

    public float getTimeLeft() {
        return timeLeft;
    }
}
