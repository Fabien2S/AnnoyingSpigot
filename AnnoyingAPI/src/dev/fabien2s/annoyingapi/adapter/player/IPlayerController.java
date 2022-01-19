package dev.fabien2s.annoyingapi.adapter.player;

import dev.fabien2s.annoyingapi.entity.EntityReference;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.sound.ISoundListener;
import dev.fabien2s.annoyingapi.util.ITickable;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface IPlayerController extends ITickable, ISoundListener, IEntityTracker {

    void init();

    void reset();

    void sendPacket(Object packet, boolean includeSelf);

    void sendActionBar(BaseComponent... baseComponents);

    void spectate(@Nullable Entity entity);

    void lookAt(Entity entity, EntityReference playerReference, EntityReference targetReference);

    Player getPlayer();

}
