package dev.fabien2s.annoyingapi.adapter.inventory;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.InventoryView;

public interface IInventoryAdapter {

    InventoryView openAnvilInventory(GamePlayer gamePlayer, BaseComponent... title);

}
