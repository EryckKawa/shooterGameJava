package com.mkbgames.main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

public class Sound {

    private Clip clip;

    public static final Sound musicBackground = new Sound("/music.wav");
    public static final Sound hitHurt = new Sound("/Hit_Hurt9.wav");
    public static final Sound gunShoot = new Sound("/Laser_Shoot.wav");

    
    
    private Sound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        try {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
