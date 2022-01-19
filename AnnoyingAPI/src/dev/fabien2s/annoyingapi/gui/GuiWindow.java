package dev.fabien2s.annoyingapi.gui;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
public class GuiWindow {

    @Getter private final BaseComponent title;
    @Getter private final GuiWindow parent;
    private final Map<Integer, GuiButton> buttonMap = new HashMap<>();

    @Getter(AccessLevel.PACKAGE) private Inventory inventory;

    void setItem(int slot, GuiButton button) {
        this.buttonMap.put(slot, button);
        if (inventory != null)
            this.inventory.setItem(slot, button.getItemStack());
    }

    boolean handleClick(GamePlayer gamePlayer, GuiView view, int slot) {
        GuiButton button = buttonMap.get(slot);
        if (button == null)
            return false;
        GuiButton.IClickHandler clickHandler = button.getClickHandler();
        if (clickHandler == null)
            return false;

        clickHandler.onClick(gamePlayer);
        return true;
    }

    @Nullable InventoryView open(GamePlayer gamePlayer) {
        if (inventory == null)
            return null;

        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        return spigotPlayer.openInventory(inventory);
    }

    void close(GamePlayer gamePlayer) {
        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        spigotPlayer.closeInventory();
    }

    void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
        for (Map.Entry<Integer, GuiButton> entry : buttonMap.entrySet()) {
            Integer slot = entry.getKey();
            GuiButton button = entry.getValue();
            this.inventory.setItem(slot, button.getItemStack());
        }
    }

    @Override
    public String toString() {
        return "GuiWindow{" +
                "name='" + title.toPlainText() + '\'' +
                ", parent=" + parent +
                '}';
    }

}
