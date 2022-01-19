package dev.fabien2s.annoyingapi.gui;

import lombok.*;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@AllArgsConstructor
@RequiredArgsConstructor
public class GuiButton {

    @Getter(AccessLevel.PACKAGE) private final ItemStack itemStack;

    @Getter @Setter
    @Nullable private IClickHandler clickHandler;

    public GuiButton(Material material) {
        this(material, null);
    }

    public GuiButton(Material material, @Nullable IClickHandler clickHandler) {
        this.itemStack = new ItemStack(material);
        this.clickHandler = clickHandler;
    }

    public interface IClickHandler {
        void onClick(GamePlayer gamePlayer);
    }

}
