package dev.fabien2s.annoyingapi.gui;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public class GuiInput extends GuiWindow {

    @Getter @Setter
    @Nullable IInputHandler inputHandler;

    public GuiInput(BaseComponent title, GuiWindow parent, Inventory inventory) {
        super(title, parent, inventory);
    }

    @Override
    boolean handleClick(GamePlayer gamePlayer, GuiView view, int slot) {
        if (slot == 2 && inputHandler != null) {
            String input = view.getInput();
            if (input != null)
                this.inputHandler.onInput(gamePlayer, input);
        }

        return super.handleClick(gamePlayer, view, slot);
    }

    public void setInputLeft(GuiButton button) {
        this.setItem(0, button);
    }

    public void setInputRight(GuiButton button) {
        this.setItem(1, button);
    }

    public void setResult(GuiButton button) {
        this.setItem(2, button);
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

    public interface IInputHandler {
        void onInput(GamePlayer gamePlayer, String input);
    }

}
