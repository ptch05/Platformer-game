package audio;

import city.cs.engine.SoundClip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioHandler {
    private static final SoundClip JUMP_SOUND_CLIP = loadSoundClip("./assets/sounds/player/jump.wav");
    private static final SoundClip ATTACK_SOUND_CLIP = loadSoundClip("./assets/sounds/player/attack.wav");
    private static final SoundClip HURT_SOUND_CLIP = loadSoundClip("./assets/sounds/player/hurt.wav");
    private static final SoundClip KILL_SOUND_CLIP = loadSoundClip("./assets/sounds/player/kill.wav");
    private static final SoundClip GAME_SOUND_CLIP = loadSoundClip("./assets/music/game-music-loop.wav");
    private static final SoundClip POTION_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/potion.wav");
    private static final SoundClip ARMOUR_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/armour.wav");
    private static final SoundClip VICTORY_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/victory.wav");
    private static final SoundClip SPAWN_SOUND_CLIP = loadSoundClip("./assets/sounds/player/spawn.wav");
    private static final SoundClip LOSE_ARMOUR_SOUND_CLIP = loadSoundClip("./assets/sounds/collectibles/lose-armour.wav");
    private static final SoundClip CROUCH_SOUND_CLIP = loadSoundClip("./assets/sounds/player/crouch.wav");
    private static final SoundClip MENU_SOUND_CLIP = loadSoundClip("./assets/music/menu-music-loop.wav");
    private static final SoundClip BUTTON_SOUND_CLIP = loadSoundClip("./assets/sounds/misc/button.wav");
    private static final SoundClip GAIN_SPECIAL_SOUND_CLIP = loadSoundClip("./assets/sounds/player/gain-special.wav");
    private static final SoundClip SPECIAL_ATTACK_SOUND_CLIP = loadSoundClip("./assets/sounds/player/special-attack.wav");

    private static final double GAME_VOLUME = 0.5;
    private static final double POTION_VOLUME = 2;
    private static final double ARMOUR_VOLUME = 1.5;
    private static final double VICTORY_VOLUME = 2;
    private static final double CROUCH_VOLUME = 1.2;
    private static final double MENU_VOLUME = 2;
    private static final double SPECIAL_ATTACK_VOLUME = 2;

    private static SoundClip loadSoundClip(String filePath) {
        try {
            return new SoundClip(filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

 //Added methods to play each sound clip
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

    public static void playGameMusic() {
        GAME_SOUND_CLIP.setVolume(GAME_VOLUME);
        GAME_SOUND_CLIP.loop();
    }

    public static void playPotionSound() {
        POTION_SOUND_CLIP.setVolume(POTION_VOLUME);
        POTION_SOUND_CLIP.play();
    }

    public static void playArmourSound() {
        ARMOUR_SOUND_CLIP.setVolume(ARMOUR_VOLUME);
        ARMOUR_SOUND_CLIP.play();
    }

    public static void loseArmourSound() {
        LOSE_ARMOUR_SOUND_CLIP.setVolume(ARMOUR_VOLUME);
        LOSE_ARMOUR_SOUND_CLIP.play();
    }

    public static void playVictorySound() {
        VICTORY_SOUND_CLIP.setVolume(VICTORY_VOLUME);
        GAME_SOUND_CLIP.stop();
        VICTORY_SOUND_CLIP.play();
    }

    public static void playSpawnSound() {
        SPAWN_SOUND_CLIP.play();
    }

    public static void playCrouchSound() {
        CROUCH_SOUND_CLIP.setVolume(CROUCH_VOLUME);
        CROUCH_SOUND_CLIP.play();
    }

    public static void playMenuSound() {
        MENU_SOUND_CLIP.setVolume(MENU_VOLUME);
        MENU_SOUND_CLIP.loop();
    }

    public static void stopMenuSound() {
        MENU_SOUND_CLIP.stop();
    }

    public static void playButtonSound() {
        BUTTON_SOUND_CLIP.play();
    }

    public static void playGainSpecialSound() {
        GAIN_SPECIAL_SOUND_CLIP.play();
    }

    public static void playSpecialAttackSound() {
        SPECIAL_ATTACK_SOUND_CLIP.setVolume(SPECIAL_ATTACK_VOLUME);
        SPECIAL_ATTACK_SOUND_CLIP.play();
    }

}
