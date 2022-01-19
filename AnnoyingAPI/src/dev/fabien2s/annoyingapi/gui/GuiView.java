package dev.fabien2s.annoyingapi.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.InventoryView;

@RequiredArgsConstructor
public class GuiView {

    @Getter private final GuiWindow window;
    @Getter private final InventoryView view;
    @Getter @Setter private String input;

    @Override
    public String toString() {
        return "GuiView{" +
                "window=" + window +
                ", input='" + input + '\'' +
                '}';
    }

}
