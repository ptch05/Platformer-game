package gothicvania.engine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A small software mixer. Every sound is decoded once into 44.1kHz stereo samples
 * and mixed on a background thread into a single output line, which lets us play
 * many overlapping effects, vary their pitch and cross-fade music without the
 * limits of {@code javax.sound.sampled.Clip}. If no audio device is available the
 * game simply runs silently.
 */
public final class Audio {
    private static final float RATE = 44100f;
    private static final int BUFFER_FRAMES = 512;
    private static final int MAX_VOICES = 32;
    private static final int MAX_SAME_SOUND = 4;
    private static Audio instance;

    private final Map<String, short[]> cache = new HashMap<>();
    private final List<Voice> voices = new ArrayList<>();
    private final SourceDataLine line;
    private Voice music;
    private String musicName;
    private volatile boolean running = true;
    private boolean muted;
    private float musicVolume = 0.55f;
    private float sfxVolume = 0.8f;

    private static final class Voice {
        final String name;
        final short[] data;
        final boolean loop;
        final double step;
        double position;
        float gain;
        float target;
        float fadePerFrame;
        boolean done;

        Voice(String name, short[] data, float gain, double pitch, boolean loop) {
            this.name = name;
            this.data = data;
            this.gain = gain;
            this.target = gain;
            this.step = pitch;
            this.loop = loop;
        }
    }

    public static synchronized Audio get() {
        if (instance == null) instance = new Audio();
        return instance;
    }

    private Audio() {
        SourceDataLine opened = null;
        if (!Boolean.getBoolean("gv.silent")) {
            try {
                AudioFormat format = new AudioFormat(RATE, 16, 2, true, false);
                opened = AudioSystem.getSourceDataLine(format);
                opened.open(format, BUFFER_FRAMES * 4 * 4);
                opened.start();
            } catch (Exception | LinkageError e) {
                System.err.println("Audio disabled: " + e.getMessage());
                opened = null;
            }
        }
        line = opened;
        if (line != null) {
            Thread mixer = new Thread(this::mixLoop, "audio-mixer");
            mixer.setDaemon(true);
            mixer.start();
        }
    }

    // ------------------------------------------------------------------ public API

    /** Plays a sound effect, e.g. {@code play("sounds/player/jump")}. */
    public void play(String name) {
        play(name, 1f, 1f);
    }

    /** Plays a sound effect with a slight random pitch so repeated sounds don't feel mechanical. */
    public void playVaried(String name, float volume) {
        play(name, volume, 0.92f + ThreadLocalRandom.current().nextFloat() * 0.16f);
    }

    public void play(String name, float volume, float pitch) {
        if (line == null || volume <= 0.01f) return;
        short[] data = load(name);
        if (data == null) return;
        synchronized (voices) {
            int same = 0;
            Voice oldestSame = null;
            for (Voice v : voices) {
                if (v.name.equals(name) && !v.done) {
                    same++;
                    if (oldestSame == null) oldestSame = v;
                }
            }
            if (same >= MAX_SAME_SOUND) oldestSame.done = true;
            if (voices.size() >= MAX_VOICES) return;
            voices.add(new Voice(name, data, volume * sfxVolume, pitch, false));
        }
    }

    /** Starts a looping music track, cross-fading from whatever was playing. */
    public void music(String name) {
        if (line == null || name.equals(musicName)) return;
        stopMusic(0.8f);
        musicName = name;
        short[] data = load(name);
        if (data == null) return;
        Voice v = new Voice(name, data, 0f, 1.0, true);
        fade(v, musicVolume, 1.2f);
        synchronized (voices) {
            music = v;
            voices.add(v);
        }
    }

    /** Plays a one-shot music sting (victory jingle) at music volume. */
    public void sting(String name) {
        if (line == null) return;
        short[] data = load(name);
        if (data == null) return;
        synchronized (voices) {
            voices.add(new Voice(name, data, musicVolume * 1.4f, 1.0, false));
        }
    }

    public void stopMusic(float fadeSeconds) {
        synchronized (voices) {
            if (music != null) {
                fade(music, 0f, fadeSeconds);
                music = null;
            }
            musicName = null;
        }
    }

    /** Temporarily lowers the music, e.g. while paused. */
    public void duckMusic(boolean ducked) {
        synchronized (voices) {
            if (music != null) fade(music, ducked ? musicVolume * 0.35f : musicVolume, 0.3f);
        }
    }

    public boolean toggleMute() {
        muted = !muted;
        return muted;
    }

    public boolean isMuted() {
        return muted;
    }

    /** Loads (and caches) sounds in the background so the first play has no hitch. */
    public void preload(String... names) {
        if (line == null) return;
        Thread t = new Thread(() -> {
            for (String n : names) load(n);
        }, "audio-preload");
        t.setDaemon(true);
        t.start();
    }

    public void shutdown() {
        running = false;
    }

    // ------------------------------------------------------------------ mixing

    private static void fade(Voice v, float target, float seconds) {
        v.target = target;
        v.fadePerFrame = seconds <= 0 ? 1f : Math.abs(target - v.gain) / (seconds * RATE);
        if (v.fadePerFrame == 0) v.fadePerFrame = 1f;
    }

    private void mixLoop() {
        float[] mix = new float[BUFFER_FRAMES * 2];
        byte[] out = new byte[BUFFER_FRAMES * 4];
        ByteBuffer bytes = ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN);
        while (running) {
            Arrays.fill(mix, 0f);
            synchronized (voices) {
                for (Voice v : voices) mixVoice(v, mix);
                voices.removeIf(v -> v.done);
            }
            bytes.clear();
            float master = muted ? 0f : 1f;
            for (float s : mix) {
                float x = s * master;
                // soft clip so loud moments saturate gracefully instead of crackling
                x = x > 1f ? 1f : x < -1f ? -1f : x - (x * x * x) / 3f * 0.15f;
                bytes.putShort((short) (x * 32000));
            }
            line.write(out, 0, out.length);
        }
        line.drain();
        line.close();
    }

    private static void mixVoice(Voice v, float[] mix) {
        short[] d = v.data;
        int frames = d.length / 2;
        for (int i = 0; i < BUFFER_FRAMES && !v.done; i++) {
            if (v.gain != v.target) {
                v.gain = v.gain < v.target ? Math.min(v.target, v.gain + v.fadePerFrame) : Math.max(v.target, v.gain - v.fadePerFrame);
                if (v.gain == 0f && v.target == 0f) {
                    v.done = true;
                    break;
                }
            }
            int p = (int) v.position;
            if (p >= frames - 1) {
                if (v.loop) {
                    v.position -= frames - 1;
                    p = (int) v.position;
                } else {
                    v.done = true;
                    break;
                }
            }
            float frac = (float) (v.position - p);
            int a = p * 2, b = a + 2;
            float l = d[a] + (d[b] - d[a]) * frac;
            float r = d[a + 1] + (d[b + 1] - d[a + 1]) * frac;
            mix[i * 2] += l / 32768f * v.gain;
            mix[i * 2 + 1] += r / 32768f * v.gain;
            v.position += v.step;
        }
    }

    // ------------------------------------------------------------------ decoding

    private short[] load(String name) {
        synchronized (cache) {
            if (cache.containsKey(name)) return cache.get(name);
        }
        short[] data = decode(name + ".wav");
        synchronized (cache) {
            cache.put(name, data);
        }
        return data;
    }

    /** Decodes a WAV file into interleaved 16-bit stereo at 44.1kHz. */
    private static short[] decode(String path) {
        try (InputStream raw = new BufferedInputStream(Assets.open(path));
             AudioInputStream source = AudioSystem.getAudioInputStream(raw)) {
            AudioFormat in = source.getFormat();
            AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, in.getSampleRate(), 16,
                in.getChannels(), in.getChannels() * 2, in.getSampleRate(), false);
            byte[] bytes;
            try (AudioInputStream converted = AudioSystem.getAudioInputStream(pcm, source)) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                converted.transferTo(buffer);
                bytes = buffer.toByteArray();
            }
            int channels = in.getChannels();
            int frames = bytes.length / (2 * channels);
            ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            short[] stereo = new short[frames * 2];
            for (int f = 0; f < frames; f++) {
                short l = bb.getShort((f * channels) * 2);
                short r = channels > 1 ? bb.getShort((f * channels + 1) * 2) : l;
                stereo[f * 2] = l;
                stereo[f * 2 + 1] = r;
            }
            return resample(stereo, in.getSampleRate());
        } catch (IOException | RuntimeException | UnsupportedAudioFileException e) {
            System.err.println("Could not load sound " + path + ": " + e.getMessage());
            return null;
        }
    }

    private static short[] resample(short[] stereo, float fromRate) {
        if (Math.abs(fromRate - RATE) < 1f) return stereo;
        int inFrames = stereo.length / 2;
        int outFrames = (int) ((long) inFrames * RATE / fromRate);
        short[] out = new short[outFrames * 2];
        double ratio = fromRate / RATE;
        for (int f = 0; f < outFrames; f++) {
            double src = f * ratio;
            int i = Math.min((int) src, inFrames - 2);
            double t = src - i;
            for (int c = 0; c < 2; c++) {
                out[f * 2 + c] = (short) (stereo[i * 2 + c] + (stereo[(i + 1) * 2 + c] - stereo[i * 2 + c]) * t);
            }
        }
        return out;
    }
}
