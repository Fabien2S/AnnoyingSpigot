package dev.fabien2s.annoyingapi.gui;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.event.player.inventory.GamePlayerRenameItemEvent;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GuiManager implements Listener {

    private static final Logger LOGGER = LogManager.getLogger(GuiManager.class);

    private final AnnoyingPlugin plugin;
    private final Map<HumanEntity, GuiView> windowMap = new HashMap<>();
    private final Map<Player, GuiPlayerInventory> playerInventoryMap = new HashMap<>();

    private @NotNull Inventory createInventory(InventoryType type, BaseComponent title) {
        Server server = plugin.getServer();
        String legacyText = title.toLegacyText();
        return server.createInventory(null, type, legacyText);
    }

    public GuiPlayerInventory createPlayer(Player player) {
        TextComponent displayName = new TextComponent(player.getDisplayName());
        PlayerInventory inventory = player.getInventory();
        GuiPlayerInventory playerInventory = new GuiPlayerInventory(displayName, null, inventory);
        this.playerInventoryMap.put(player, playerInventory);
        return playerInventory;
    }

    public GuiGrid createGrid(BaseComponent title, GuiGrid.Layout layout) {
        return createGrid(null, title, layout);
    }

    public GuiGrid createGrid(@Nullable GuiWindow parent, BaseComponent title, GuiGrid.Layout layout) {
        Server server = plugin.getServer();
        Inventory inventory = layout == GuiGrid.Layout.GENERIC_3x3 ?
                createInventory(InventoryType.DISPENSER, title) :
                server.createInventory(null, layout.getSlotCount(), title.toLegacyText());
        return new GuiGrid(
                title,
                parent,
                inventory,
                layout
        );
    }

    public GuiInput createInput(BaseComponent title) {
        return createInput(null, title);
    }

    public GuiInput createInput(@Nullable GuiWindow parent, BaseComponent title) {
        Server server = plugin.getServer();
        return new GuiInput(
                title,
                parent
        );
    }

    public void open(GuiWindow window, GamePlayer gamePlayer) {
        InventoryView inventoryView = window.open(gamePlayer);
        if (inventoryView == null)
            throw new IllegalStateException("Unable to open the inventory");

        GuiView view = new GuiView(window, inventoryView);
        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        this.windowMap.put(spigotPlayer, view);
        LOGGER.info("{} opened inventory {}", gamePlayer, view);
    }

    public void close(GamePlayer gamePlayer, boolean closeAll) {
        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        GuiView view = windowMap.remove(spigotPlayer);
        if (view == null)
            return;

        GuiWindow window = view.getWindow();
        GuiWindow parent = window.getParent();
        if (closeAll || parent == null) {
            LOGGER.info("{} closed inventory {}", gamePlayer, view);
            window.close(gamePlayer);
            return;
        }

        this.open(parent, gamePlayer);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.windowMap.remove(player);
        this.playerInventoryMap.remove(player);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        GuiView view = windowMap.remove(player);
        LOGGER.info("{} closed inventory {}", player, view);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();
        HumanEntity player = inventoryView.getPlayer();
        int slot = event.getSlot();

        PlayerList playerList = plugin.getPlayerList();
        playerList.forPlayer((Player) player, gamePlayer -> {

            GuiView view = windowMap.get(player);
            if (view != null) {
                LOGGER.info("{} clicked in inventory {} (slot: {})", gamePlayer, view, slot);

                GuiWindow window = view.getWindow();
                if (window.handleClick(gamePlayer, view, slot))
                    event.setResult(Event.Result.DENY);
                return;
            }

            GuiPlayerInventory playerInventory = playerInventoryMap.get(player);
            if (playerInventory != null) {
                if (playerInventory.handleClick(gamePlayer, view, slot))
                    event.setResult(Event.Result.DENY);
            }
        });
    }

    @EventHandler
    private void onItemRename(GamePlayerRenameItemEvent event) {
        GamePlayer gamePlayer = event.getGamePlayer();
        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        GuiView view = windowMap.get(spigotPlayer);
        if(view == null)
            return;

        String input = event.getInput();
        view.setInput(input);
    }

}
