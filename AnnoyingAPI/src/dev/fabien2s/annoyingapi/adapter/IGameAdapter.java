package dev.fabien2s.annoyingapi.adapter;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import dev.fabien2s.annoyingapi.npc.Npc;
import dev.fabien2s.annoyingapi.npc.NpcManager;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.adapter.block.IBlockAdapter;
import dev.fabien2s.annoyingapi.adapter.entity.IEntityAdapter;
import dev.fabien2s.annoyingapi.adapter.inventory.IInventoryAdapter;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.command.ICommandRegistry;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public interface IGameAdapter {

    Message translate(String arg, Object... args);

    ICommandRegistry<?> createCommandRegistry(Server server);

    IPlayerController createController(GamePlayer gamePlayer);

    Npc createNPC(NpcManager npcManager, Location location, GameProfile gameProfile);

    GameProfile getProfile(Player player);

    IBlockAdapter getBlockAdapter();

    IEntityAdapter getEntityAdapter();

    IInventoryAdapter getInventoryAdapter();

}
