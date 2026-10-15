# Level files

Each `*.txt` file here is one chapter, played in file-name order. A file is a
few `key: value` header lines, a `---` line, then the map drawn in ASCII, one
character per 16x16 tile. Rows don't need to be the same length.

```
name: The Cemetery
subtitle: Where the restless dead keep watch
theme: cemetery            # cemetery | crypt | inferno
music: music/level1-game-music-loop   # or "none"
hint.1: A D MOVE   SPACE JUMP          # text for hint trigger 1
---
(map)
```

To see a whole level as an image while editing:

```bash
java -cp out gothicvania.tools.MapRender 0 level.png
```

## Legend

Things that stand on the ground go in the cell directly above the ground.

| Char | Meaning |
|------|---------|
| `#` | Solid ground |
| `=` | Ledge: jump up through it, hold down + jump to drop through |
| `^` | Spikes (put solid ground under them) |
| `~` | Lava |
| `B` | Cracked wall: two sword hits break it |
| `C` | Crumbling block: gives way shortly after being stood on, then regrows |
| `P` | Hero start |
| `S` | Checkpoint statue |
| `T` | Trophy: claiming it ends the level |
| `s` | Skeleton, buried until you come close |
| `z` | Skeleton, already awake (faster and tougher) |
| `h` | Hell-hound: prowls, crouches, then pounces |
| `g` | Ghost: floats through walls, throws fireballs |
| `G` | Wraith: haloed ghost that fires three and blinks away when hit |
| `D` | The demon boss (needs an arena, see below) |
| `f` | Fire vent |
| `F` | Fire vent that stays dormant until the boss's third phase |
| `m` / `v` | Moving platform, horizontal / vertical, travelling to the `:` markers on its row / column |
| `:` | Path marker for moving platforms |
| `p` | Health potion |
| `a` | Armour: halves damage for 20 seconds |
| `o` | Soul |
| `H` | Heart: +20 maximum health for the rest of the run |
| `!` | Secret: counts as found when the hero reaches it. An enclosed room around it stays hidden behind fake rock until then |
| `1`-`9` | Hint trigger: shows `hint.N` from the header once |
| `R` | Rising lava starts when the hero passes this column |
| `Z` | Rising lava stops at this row |
| `[` `]` | Boss arena gates (a vertical run of each). Crossing `[` seals the arena and wakes the boss |
| `t` `r` `b` | Scenery: tree, gravestone, bush |
| `k` | Bush drawn in front of the hero |
| `i` `I` | Pillar, broken pillar |

## Jump reference

The hero is 1 tile wide and a little over 2 tiles tall, so passages need to be
3 tiles high.

- A full jump clears 4 tiles of height and about 5 tiles of gap.
- A dash adds about 3 tiles of gap (7-tile gaps need jump + dash).
- Wall-jumps climb any vertical shaft; 2-tile-wide chimneys work well.
