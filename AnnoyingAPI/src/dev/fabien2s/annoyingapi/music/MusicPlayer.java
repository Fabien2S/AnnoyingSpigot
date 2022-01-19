package dev.fabien2s.annoyingapi.music;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.EnumMap;

@RequiredArgsConstructor
public class MusicPlayer {

    private static final EnumMap<Instrument, Sound> SOUND_ENUM_MAP = new EnumMap<>(Instrument.class);

    static {
        SOUND_ENUM_MAP.put(Instrument.PIANO, Sound.BLOCK_NOTE_BLOCK_HARP);
        SOUND_ENUM_MAP.put(Instrument.BASS_DRUM, Sound.BLOCK_NOTE_BLOCK_BASEDRUM);
        SOUND_ENUM_MAP.put(Instrument.SNARE_DRUM, Sound.BLOCK_NOTE_BLOCK_SNARE);
        SOUND_ENUM_MAP.put(Instrument.STICKS, Sound.BLOCK_NOTE_BLOCK_HAT);
        SOUND_ENUM_MAP.put(Instrument.BASS_GUITAR, Sound.BLOCK_NOTE_BLOCK_BASS);
        SOUND_ENUM_MAP.put(Instrument.FLUTE, Sound.BLOCK_NOTE_BLOCK_FLUTE);
        SOUND_ENUM_MAP.put(Instrument.BELL, Sound.BLOCK_NOTE_BLOCK_BELL);
        SOUND_ENUM_MAP.put(Instrument.GUITAR, Sound.BLOCK_NOTE_BLOCK_GUITAR);
        SOUND_ENUM_MAP.put(Instrument.CHIME, Sound.BLOCK_NOTE_BLOCK_CHIME);
        SOUND_ENUM_MAP.put(Instrument.XYLOPHONE, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE);
        SOUND_ENUM_MAP.put(Instrument.IRON_XYLOPHONE, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE);
        SOUND_ENUM_MAP.put(Instrument.COW_BELL, Sound.BLOCK_NOTE_BLOCK_COW_BELL);
        SOUND_ENUM_MAP.put(Instrument.DIDGERIDOO, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO);
        SOUND_ENUM_MAP.put(Instrument.BIT, Sound.BLOCK_NOTE_BLOCK_BIT);
        SOUND_ENUM_MAP.put(Instrument.BANJO, Sound.BLOCK_NOTE_BLOCK_BANJO);
        SOUND_ENUM_MAP.put(Instrument.PLING, Sound.BLOCK_NOTE_BLOCK_PLING);
    }

    private final GamePlayer player;

    public void playNote(Instrument instrument, Note note) {
        IPlayerController controller = player.getController();
        Sound sound = SOUND_ENUM_MAP.get(instrument);
        float pitch = (float) Math.pow(2.0D, (note.getId() - 12.0D) / 12.0D);
        controller.playSound2D(sound, SoundCategory.MASTER, 1, pitch);
    }

}
