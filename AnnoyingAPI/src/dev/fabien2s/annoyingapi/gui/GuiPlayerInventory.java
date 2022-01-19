package dev.fabien2s.annoyingapi.gui;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

public class GuiPlayerInventory extends GuiWindow {

    public GuiPlayerInventory(BaseComponent title, GuiWindow parent, PlayerInventory inventory) {
        super(title, parent, inventory);
    }

    public void setEquipment(EquipmentSlot slot, GuiButton button) {
        switch (slot) {
            case HAND:
                int heldItemSlot = ((PlayerInventory) getInventory()).getHeldItemSlot();
                this.setItem(heldItemSlot, button);
                break;
            case OFF_HAND:
                this.setItem(40, button);
                break;
            case FEET:
                this.setItem(36, button);
                break;
            case LEGS:
                this.setItem(37, button);
                break;
            case CHEST:
                this.setItem(38, button);
                break;
            case HEAD:
                this.setItem(39, button);
                break;
        }
    }

    public void setItem(int row, int column, GuiButton button) {
        Validate.inclusiveBetween(1, 4, row);
        Validate.inclusiveBetween(1, 9, column);
        int slot = (column - 1) + (row * 9);
        this.setItem(slot, button);
    }

    public void setHotBar(int slot, GuiButton button) {
        Validate.inclusiveBetween(1, 9, slot);
        this.setItem(slot, button);
    }

}
