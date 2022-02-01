package dev.fabien2s.annoyingapi.gui;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GuiWindow {

    @Getter private final BaseComponent title;
    @Getter private final GuiWindow parent;
    @Getter(AccessLevel.PACKAGE) private final Inventory inventory;

    private final Map<Integer, GuiButton> buttonMap = new HashMap<>();


    void setItem(int slot, GuiButton button) {
        this.buttonMap.put(slot, button);
        if (inventory != null)
            this.inventory.setItem(slot, button.getItemStack());
    }

    boolean handleClick(AnnoyingPlayer annoyingPlayer, GuiView view, int slot) {
        GuiButton button = buttonMap.get(slot);
        if (button == null)
            return false;
        GuiButton.IClickHandler clickHandler = button.getClickHandler();
        if (clickHandler == null)
            return false;

        clickHandler.onClick(annoyingPlayer);
        return true;
    }

    @Nullable InventoryView open(AnnoyingPlayer annoyingPlayer) {
        if (inventory == null)
            return null;

        Player spigotPlayer = annoyingPlayer.getSpigotPlayer();
        return spigotPlayer.openInventory(inventory);
    }

    void close(AnnoyingPlayer annoyingPlayer) {
        Player spigotPlayer = annoyingPlayer.getSpigotPlayer();
        spigotPlayer.closeInventory();
    }

}
