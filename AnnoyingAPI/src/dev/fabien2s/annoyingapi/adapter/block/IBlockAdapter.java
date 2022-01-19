package dev.fabien2s.annoyingapi.adapter.block;

import org.bukkit.block.Block;

public interface IBlockAdapter {

    FluidType getFluid(Block block);

    void setChestStatus(Block chest, boolean open);

    void spawnEndGatewayBeam(Block endGateway);

    enum FluidType {
        EMPTY,
        WATER,
        FLOWING_WATER,
        LAVA,
        FLOWING_LAVA
    }

}
