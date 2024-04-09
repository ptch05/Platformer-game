package audio;

import city.cs.engine.SoundClip;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * This class handles all audio functionality for the game, including sound effects and music.
 * It provides methods to play, loop, and stop various sound clips.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class AudioHandler {
    private static final SoundClip JUMP_SOUND_CLIP = loadSoundClip("./assets/sounds/player/jump.wav");
    private static final SoundClip ATTACK_SOUND_CLIP = loadSoundClip("./assets/sounds/player/attack.wav");
    private static final SoundClip HURT_SOUND_CLIP = loadSoundClip("./assets/sounds/player/hurt.wav");
    private static final SoundClip KILL_SOUND_CLIP = loadSoundClip("./assets/sounds/player/kill.wav");
    private static final SoundClip LEVEL1_SOUND_CLIP = loadSoundClip("./assets/music/level1-game-music-loop.wav");
    private static final SoundClip POTION_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/potion.wav");
    private static final SoundClip ARMOUR_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/armour.wav");
    private static final SoundClip VICTORY_SOUND_CLIP = loadSoundClip("./assets/music/victory.wav");
    private static final SoundClip LOSE_ARMOUR_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/lose-armour.wav");
    private static final SoundClip CROUCH_SOUND_CLIP = loadSoundClip("./assets/sounds/player/crouch.wav");
    private static final SoundClip MENU_SOUND_CLIP = loadSoundClip("./assets/music/menu-music-loop.wav");
    private static final SoundClip BUTTON_SOUND_CLIP = loadSoundClip("./assets/sounds/misc/button.wav");
    private static final SoundClip GAIN_SPECIAL_SOUND_CLIP = loadSoundClip("./assets/sounds/player/gain-special.wav");
    private static final SoundClip SPECIAL_ATTACK_SOUND_CLIP = loadSoundClip("./assets/sounds/player/special-attack.wav");
    private static final SoundClip FIREBALL_SOUND_CLIP = loadSoundClip("./assets/sounds/misc/fireball.wav");
    private static final SoundClip LEVEL2_SOUND_CLIP = loadSoundClip("./assets/music/level2-game-music-loop.wav");
    private static final SoundClip LEVEL3_SOUND_CLIP = loadSoundClip("./assets/music/level3-game-music-loop.wav");
    private static final SoundClip DEMON_IDLE_SOUND_CLIP = loadSoundClip("./assets/sounds/demon/demon-idle.wav");
    private static final SoundClip DEMON_ATTACK_SOUND_CLIP = loadSoundClip("./assets/sounds/demon/demon-attack.wav");
    private static final SoundClip FIRE_TRAP_SOUND_CLIP = loadSoundClip("./assets/sounds/misc/fire-trap.wav");

    /**
     * Loads a sound clip from a file path.
     * @param filePath the relative path to the audio file.
     * @return SoundClip object or null if an error occurs.
     */

    private static SoundClip loadSoundClip(String filePath) {
        try {
            return new SoundClip(filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void playJumpSound() {
        JUMP_SOUND_CLIP.play();
    }

    public static void playAttackSound() {
        ATTACK_SOUND_CLIP.play();
    }

    public static void playHurtSound() {
        HURT_SOUND_CLIP.play();
    }

    public static void playKillSound() {
        KILL_SOUND_CLIP.play();
    }

    public static void playLevel1Music() {
        LEVEL1_SOUND_CLIP.loop();
        
    }

    public static void playLevel2Music() {
        LEVEL2_SOUND_CLIP.setVolume(1.3);
        LEVEL2_SOUND_CLIP.loop();
        
    }

    public static void playLevel3Music() {
        LEVEL3_SOUND_CLIP.setVolume(1.3);
        LEVEL3_SOUND_CLIP.loop();
        
    }

    public static void stopAllGameMusic() {
        LEVEL1_SOUND_CLIP.stop();
        LEVEL2_SOUND_CLIP.stop();
        LEVEL3_SOUND_CLIP.stop();
    }

    public static void playPotionSound() {
        POTION_SOUND_CLIP.setVolume(2);
        POTION_SOUND_CLIP.play();
    }

    public static void playArmourSound() {
        ARMOUR_SOUND_CLIP.setVolume(1.2);
        ARMOUR_SOUND_CLIP.play();
    }

    public static void loseArmourSound() {
        LOSE_ARMOUR_SOUND_CLIP.setVolume(1.2);
        LOSE_ARMOUR_SOUND_CLIP.play();
    }

    public static void playVictorySound() {
        stopAllGameMusic();
        VICTORY_SOUND_CLIP.setVolume(2);
        VICTORY_SOUND_CLIP.play();
        
    }

    public static void playCrouchSound() {
        CROUCH_SOUND_CLIP.setVolume(1.2);
        CROUCH_SOUND_CLIP.play();
    }

    public static void playMenuSound() {
        MENU_SOUND_CLIP.setVolume(2);
        MENU_SOUND_CLIP.loop();
    }

    public static void stopMenuSound() {
        MENU_SOUND_CLIP.stop();
    }

    public static void playButtonSound() {
        BUTTON_SOUND_CLIP.play();
    }

    public static void playGainSpecialSound() {
        GAIN_SPECIAL_SOUND_CLIP.setVolume(0.7);
        GAIN_SPECIAL_SOUND_CLIP.play();
    }

    public static void playSpecialAttackSound() {
        SPECIAL_ATTACK_SOUND_CLIP.setVolume(1.6);
        SPECIAL_ATTACK_SOUND_CLIP.play();
    }

    public static void playFireballSound() {
        FIREBALL_SOUND_CLIP.setVolume(1.25);
        FIREBALL_SOUND_CLIP.play();
    }

    public static void playDemonIdleSound() {
        DEMON_IDLE_SOUND_CLIP.setVolume(2);
        DEMON_IDLE_SOUND_CLIP.loop();
    }

    public static void stopDemonIdleSound(){
        DEMON_IDLE_SOUND_CLIP.stop();
    }

    public static void playDemonAttackSound() {
        DEMON_ATTACK_SOUND_CLIP.setVolume(2);
        DEMON_ATTACK_SOUND_CLIP.play();
    }

    public static void stopDemonAttackSound(){
        DEMON_ATTACK_SOUND_CLIP.stop();
    }

    public static void playFireTrapSound() {
        FIRE_TRAP_SOUND_CLIP.setVolume(2);
        FIRE_TRAP_SOUND_CLIP.loop();
    }

    public static void stopFireTrapSound() {
        FIRE_TRAP_SOUND_CLIP.stop();
    }
}
