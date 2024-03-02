package utilities;
import city.cs.engine.SoundClip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioHandler {
  private static final SoundClip JUMP_SOUND_CLIP;
  private static final SoundClip ATTACK_SOUND_CLIP;
  private static final SoundClip HURT_SOUND_CLIP;
  private static final SoundClip KILL_SOUND_CLIP;
  private static final SoundClip GAME_SOUND_CLIP;
  private static final SoundClip POTION_SOUND_CLIP;
  private static final SoundClip ARMOUR_SOUND_CLIP;
  private static final SoundClip VICTORY_SOUND_CLIP;

  static {
    JUMP_SOUND_CLIP = loadSoundClip("assets/sounds/jump.wav");
    ATTACK_SOUND_CLIP = loadSoundClip("assets/sounds/attack.wav");
    HURT_SOUND_CLIP = loadSoundClip("assets/sounds/hurt.wav");
    KILL_SOUND_CLIP = loadSoundClip("assets/sounds/kill.wav");
    GAME_SOUND_CLIP = loadSoundClip("assets/music/game-music-loop.wav");
    POTION_SOUND_CLIP = loadSoundClip("assets/sounds/potion.wav");
    ARMOUR_SOUND_CLIP = loadSoundClip("assets/sounds/armour.wav");
    VICTORY_SOUND_CLIP = loadSoundClip("assets/sounds/victory.wav");
  }

  private static SoundClip loadSoundClip(String filePath) {
      try {
          return new SoundClip(filePath);
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
          e.printStackTrace(); // This will print the stack trace of the exception to the console
          return null;
      }
  }

  // Added methods to play each sound clip
  public static void playJumpSound() {
      if (JUMP_SOUND_CLIP != null) {
         //JUMP_SOUND_CLIP.play();
      }
  }

  public static void playAttackSound() {
      if (ATTACK_SOUND_CLIP != null) {
         //ATTACK_SOUND_CLIP.play();
      }
  }

  public static void playHurtSound() {
      if (HURT_SOUND_CLIP != null) {
         //HURT_SOUND_CLIP.play();
      }
  }

  public static void playKillSound() {
    if (KILL_SOUND_CLIP != null) {
       //KILL_SOUND_CLIP.play();
      }
  }

  public static void playGameMusic() {
    if(GAME_SOUND_CLIP!= null){
        GAME_SOUND_CLIP.setVolume(0.6);
        //GAME_SOUND_CLIP.loop();
      }
  }

  public static void playPotionSound() {
    if (POTION_SOUND_CLIP != null) {
        POTION_SOUND_CLIP.setVolume(2);
        //POTION_SOUND_CLIP.play();
      }
  }

  public static void playArmourSound() {
    if (ARMOUR_SOUND_CLIP != null) {
        ARMOUR_SOUND_CLIP.setVolume(2);
        //ARMOUR_SOUND_CLIP.play();
      }
  }

  public static void playVictorySound() {
    if (VICTORY_SOUND_CLIP != null) {
        VICTORY_SOUND_CLIP.setVolume(2);
        GAME_SOUND_CLIP.stop();
       // VICTORY_SOUND_CLIP.play();
      }
  }


    
}
