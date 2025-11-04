package org.example;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager oneAndOnlyManager;
    private Map<String, MediaPlayer> backgroundMusic;
    private Map<String, AudioClip> soundEffects;
    private MediaPlayer currentBackgroundMusic;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private double musicVolume;
    private double effectsVolume;
    
    public MediaPlayer getCurrentBackgroundMusic() {
        return currentBackgroundMusic;
    }

    private SoundManager() {
        backgroundMusic = new HashMap<>();
        soundEffects = new HashMap<>();
        soundEnabled = true;
        musicEnabled = true;
        musicVolume = 1.0;
        effectsVolume = 1.0;
        loadSounds();
    }

    public static SoundManager oneAndOnly() {
        if (oneAndOnlyManager == null) {
            oneAndOnlyManager = new SoundManager();
        }
        return oneAndOnlyManager;
    }

    private void loadSounds() {
        try {
            // Load background music and themes
            loadBackgroundMusic("main_theme", "assets/audio/background_musics/main_theme.mp3");
            loadBackgroundMusic("menu_theme", "assets/audio/background_musics/menu_theme.mp3");

            // Load sound effects
            loadSoundEffect("brick_break", "assets/audio/sound_effects/brick_break.mp3");
            loadSoundEffect("fast_ball", "assets/audio/sound_effects/fast_ball.mp3");
            loadSoundEffect("triple_ball", "assets/audio/sound_effects/triple_ball.mp3");
            loadSoundEffect("bigger_paddle", "assets/audio/sound_effects/bigger_paddle.mp3");
            loadSoundEffect("life_lost", "assets/audio/sound_effects/life_lost.mp3");
            loadSoundEffect("game_over", "assets/audio/sound_effects/game_over.mp3");
            loadSoundEffect("victory", "assets/audio/sound_effects/victory.mp3");

        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    private void loadBackgroundMusic(String name, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Media media = new Media(file.toURI().toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(musicVolume);
                backgroundMusic.put(name, player);
            }
        } catch (Exception e) {
            System.err.println("Could not load background music: " + path);
        }
    }

    private void loadSoundEffect(String name, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                AudioClip clip = new AudioClip(file.toURI().toString());
                clip.setVolume(effectsVolume);
                soundEffects.put(name, clip);
            }
        } catch (Exception e) {
            System.err.println("Could not load sound effect: " + name);
        }
    }


    public void playBackgroundMusic(String musicName) {
        if (!musicEnabled) return;

        stopBackgroundMusic();

        MediaPlayer player = backgroundMusic.get(musicName);
        if (player != null) {
            currentBackgroundMusic = player;
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        }
    }

    public void stopBackgroundMusic() {
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.stop();
            currentBackgroundMusic = null;
        }
    }

    public void pauseBackgroundMusic() {
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.pause();
        }
    }

    public void resumeBackgroundMusic() {
        if (currentBackgroundMusic != null && musicEnabled) {
            currentBackgroundMusic.play();
        }
    }

    public void playSoundEffect(String effectName) {
        if (!soundEnabled) return;

        AudioClip clip = soundEffects.get(effectName);
        if (clip != null) {
            clip.play();
        }
    }

    public void playSoundEffect(String effectName, double volume) {
        if (!soundEnabled) return;

        AudioClip clip = soundEffects.get(effectName);
        if (clip != null) {
            double originalVolume = clip.getVolume();
            clip.setVolume(volume);
            clip.play();
            clip.setVolume(originalVolume);
        }
    }

    public void stopAllSoundEffects() {
        for (AudioClip clip : soundEffects.values()) {
            clip.stop();
        }
    }

    public void stopAllSounds() {
        stopBackgroundMusic();
        stopAllSoundEffects();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSoundEffects();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else if (currentBackgroundMusic != null) {
            resumeBackgroundMusic();
        }
    }

    public void setSoundEffectsEnabled(boolean enabled) {
    }

    public void setMusicVolume(double volume) {
        if (volume < 0.0 || volume > 1.0) {
            return;
        }
        this.musicVolume = volume;
        if (currentBackgroundMusic != null) {
            currentBackgroundMusic.setVolume(musicVolume);
        }
    }

    public void setEffectsVolume(double volume) {
        if (volume < 0.0 || volume > 1.0) {
            return;
        }
        this.effectsVolume = volume;
        for (AudioClip clip : soundEffects.values()) {
            clip.setVolume(effectsVolume);
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }


    public double getMusicVolume() {
        return musicVolume;
    }

    public double getEffectsVolume() {
        return effectsVolume;
    }

    public void cleanup() {
        stopAllSounds();
        backgroundMusic.clear();
        soundEffects.clear();
    }
}
