package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static final Map<String, Music> musicTracks = new HashMap<>();

    // Play a music track
    public static void playMusic(String filePath, boolean loop) {
        // Check if the music is already loaded
        Music music = musicTracks.get(filePath);
        if (music == null) {
            // Load and play the new music
            music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            music.setLooping(loop);
            musicTracks.put(filePath, music);
        }

        // Play the music
        if (!music.isPlaying()) {
            music.play();
        }
    }

    // Stop a specific music track
    public static void stopMusic(String filePath) {
        Music music = musicTracks.get(filePath);
        if (music != null && music.isPlaying()) {
            music.stop();
        }
    }

    // Stop all music tracks
    public static void stopAllMusic() {
        for (Music music : musicTracks.values()) {
            if (music.isPlaying()) {
                music.stop();
            }
        }
    }

    // Dispose a specific music track
    public static void disposeMusic(String filePath) {
        Music music = musicTracks.get(filePath);
        if (music != null) {
            music.dispose();
            musicTracks.remove(filePath);
        }
    }

    // Dispose all music resources
    public static void dispose() {
        for (Music music : musicTracks.values()) {
            music.dispose();
        }
        musicTracks.clear();
    }
}