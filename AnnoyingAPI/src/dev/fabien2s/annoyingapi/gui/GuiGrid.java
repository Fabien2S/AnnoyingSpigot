package dev.fabien2s.annoyingapi.gui;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.Inventory;

public class GuiGrid extends GuiWindow {

    @Getter private final Layout layout;

    public GuiGrid(BaseComponent title, GuiWindow parent, Inventory inventory, Layout layout) {
        super(title, parent, inventory);
        this.layout = layout;
    }

    public void setItem(int row, int column, GuiButton button) {
        Validate.inclusiveBetween(1, layout.row, row);
        Validate.inclusiveBetween(1, layout.column, column);

        int slot = (column - 1) + (row - 1) * layout.column;
        this.setItem(slot, button);
    }

    public enum Layout {
        GENERIC_9x1(9, 1),
        GENERIC_9x2(9, 2),
        GENERIC_9x3(9, 3),
        GENERIC_9x4(9, 4),
        GENERIC_9x5(9, 5),
        GENERIC_9x6(9, 6),
        GENERIC_3x3(3, 3);

        @Getter private final int column;
        @Getter private final int row;
        @Getter private final int slotCount;

        Layout(int column, int row) {
            this.column = column;
            this.row = row;
            this.slotCount = column * row;
        }
    }

}
