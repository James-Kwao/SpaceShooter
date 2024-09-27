package classes;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundsManager {

    private static final Clip clip, effect;

    static {
        try {
            clip = AudioSystem.getClip();
            effect = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public SoundsManager() {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("res/sound/explosion.wav");
            assert is != null;
            AudioInputStream explosion = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            effect.open(explosion);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadRand() {
        try {
            InputStream is = switch (GamePanel.rand.nextInt(1, 4)) {
                case 1 -> ClassLoader.getSystemClassLoader().getResourceAsStream("res/sound/gameMusic_1.wav");
                case 2 -> ClassLoader.getSystemClassLoader().getResourceAsStream("res/sound/gameMusic_2.wav");
                case 3 -> ClassLoader.getSystemClassLoader().getResourceAsStream("res/sound/gameMusic_3.wav");
                default -> null;
            };
            assert is != null;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip.open(audioIn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void pauseMusic() {
        clip.setMicrosecondPosition(clip.getMicrosecondLength());
        clip.stop();
    }

    public void playMusic() {
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        clip.stop();
        clip.flush();
        clip.close();
    }

    public void playExplosion() {
        try {
            effect.setFramePosition(0);
            effect.setMicrosecondPosition(0);
            effect.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
