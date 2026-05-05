package com.ror.engine;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager implements Runnable {

    Clip clip;
    URL[] soundURL = new URL[5];
    String action = "play";

    public static final int BGM_MAIN = 0;

    public static SoundManager bgm;

    public SoundManager() {
        soundURL[BGM_MAIN] = getClass().getResource("/images/BGM/bgm_main.wav");
    }

    public void setFile(int i) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            if (soundURL[i] == null) return;

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        action = "play";
        new Thread(this).start();
    }

    public void loop() {
        action = "loop";
        new Thread(this).start();
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public static SoundManager getBGM() {
        if (bgm == null) {
            bgm = new SoundManager();
            bgm.setFile(BGM_MAIN);
            bgm.loop();
        }
        return bgm;
    }

    @Override
    public void run() {
        if (clip != null) {
            clip.setFramePosition(0);

            if (action.equals("loop")) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }
}