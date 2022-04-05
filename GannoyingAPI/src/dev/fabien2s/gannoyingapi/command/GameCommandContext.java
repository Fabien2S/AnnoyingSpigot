package dev.fabien2s.gannoyingapi.command;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameWorld;

import javax.annotation.Nullable;

public class GameCommandContext {

    @Nullable
    public static GameWorld getWorld(ICommandContext context) {
        AnnoyingPlugin plugin = context.getPlugin();
        GameWorld gameWorld = ((GamePlugin) plugin).getGameWorld();
        if (gameWorld == null)
            context.sendFailure("No game world found");
        return gameWorld;
    }

}
