package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.adapter.GameAdapters;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.util.ScoreboardHelper;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ServerEntityRendererManager extends EntityRendererManager {

    private final Scoreboard scoreboard;

    public ServerEntityRendererManager(Scoreboard scoreboard) {
        super(null, GameAdapters.INSTANCE.getEntityAdapter()::createTracker);
        this.scoreboard = scoreboard;
    }

    @Override
    protected EntityRenderer<?, ?> register(EntityRenderer<?, ?> parent, Entity entity, IEntityTracker tracker) {
        EntityRenderer<?, ?> entityRenderer = super.register(parent, entity, tracker);

        String teamName = EntityRendererHelper.getTeamName(entityRenderer);
        Team team = scoreboard.getTeam(teamName);
        if (team != null)
            team.unregister();

        team = scoreboard.registerNewTeam(teamName);

        String teamEntry = ScoreboardHelper.getNameForTeamEntry(entity);
        team.addEntry(teamEntry);

        return entityRenderer;
    }

    @Override
    protected void reset(EntityRenderer<?, ?> renderer) {
        String teamName = EntityRendererHelper.getTeamName(renderer);
        Team team = scoreboard.getTeam(teamName);
        if (team != null)
            team.unregister();

        super.reset(renderer);
    }
}
