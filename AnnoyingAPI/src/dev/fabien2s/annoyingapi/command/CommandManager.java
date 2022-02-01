package dev.fabien2s.annoyingapi.command;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.command.CommandRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandManager {

    private static final Logger LOGGER = LogManager.getLogger(CommandManager.class);

    private final ICommandRegistry<?> registry;

    private final Map<String, CommandNode> commandMap = new HashMap<>();

    public CommandManager(AnnoyingPlugin plugin) {
        Server server = plugin.getServer();
        this.registry = new CommandRegistry(server);
    }

    public void registerCommand(Supplier<CommandNode> commandFunction) {
        CommandNode command = commandFunction.get();
        String commandName = command.getName();
        if (commandMap.containsKey(commandName))
            this.unregisterCommand(commandName);

        LOGGER.info("Registering command {}", commandName);
        this.commandMap.put(commandName, command);
        this.registry.registerCommand(command);
    }

    public void unregisterCommand(String name) {
        CommandNode command = commandMap.remove(name);
        if (command != null) {
            LOGGER.info("Unregistering command {}", name);
            this.registry.unregisterCommand(command);
        }
    }

    public void unregisterAll() {
        LOGGER.info("Unregistering {} command(s)", commandMap.size());
        this.commandMap.forEach((name, command) -> this.registry.unregisterCommand(command));
        this.commandMap.clear();
    }

}
