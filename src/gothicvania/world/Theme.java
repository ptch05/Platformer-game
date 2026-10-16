package gothicvania.world;

import java.awt.Color;
import java.util.List;

/**
 * The look and sound of a level: tiles, parallax layers, music, how dark it is
 * and what drifts through the air.
 */
public enum Theme {
    CEMETERY("cemetery", "music/level1-game-music-loop", new Color(8, 4, 22, 95), Ambience.FOG,
        List.of(
            Layer.fill("backgrounds/cemetery/sky.png", 0.02),
            Layer.bottom("backgrounds/cemetery/mountains.png", 0.12, 0.10, -18),
            Layer.bottom("backgrounds/cemetery/graveyard.png", 0.30, 0.22, 4))),
    CRYPT("crypt", "music/level2-game-music-loop", new Color(4, 6, 18, 175), Ambience.DUST,
        List.of(
            Layer.fill("backgrounds/crypt/back.png", 0.03),
            Layer.bottom("backgrounds/crypt/mid1.png", 0.15, 0.12, 40),
            Layer.bottom("backgrounds/crypt/mid2.png", 0.28, 0.20, 50),
            Layer.bottom("backgrounds/crypt/front.png", 0.42, 0.30, 70))),
    INFERNO("inferno", "music/level3-game-music-loop", new Color(20, 2, 2, 140), Ambience.EMBERS,
        List.of(
            Layer.fill("backgrounds/inferno/back.png", 0.06),
            Layer.bottom("backgrounds/inferno/pillars.png", 0.22, 0.15, 10),
            Layer.top("backgrounds/inferno/ceiling.png", 0.38, 0.25, -8)));

    public enum Ambience { FOG, DUST, EMBERS }

    /** One parallax background layer. */
    public record Layer(String image, double parallaxX, double parallaxY, Anchor anchor, int offsetY) {
        public enum Anchor { FILL, BOTTOM, TOP }

        static Layer fill(String image, double px) {
            return new Layer(image, px, 0, Anchor.FILL, 0);
        }

        static Layer bottom(String image, double px, double py, int offsetY) {
            return new Layer(image, px, py, Anchor.BOTTOM, offsetY);
        }

        static Layer top(String image, double px, double py, int offsetY) {
            return new Layer(image, px, py, Anchor.TOP, offsetY);
        }
    }

    public final String id;
    public final String music;
    public final Color darkness;
    public final Ambience ambience;
    public final List<Layer> layers;

    Theme(String id, String music, Color darkness, Ambience ambience, List<Layer> layers) {
        this.id = id;
        this.music = music;
        this.darkness = darkness;
        this.ambience = ambience;
        this.layers = layers;
    }

    public String tiles() {
        return "tiles/" + id + ".png";
    }

    public String spikes() {
        return "tiles/" + id + "-spikes.png";
    }

    public static Theme byId(String id) {
        for (Theme t : values()) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        throw new IllegalArgumentException("Unknown theme: " + id);
    }
}
