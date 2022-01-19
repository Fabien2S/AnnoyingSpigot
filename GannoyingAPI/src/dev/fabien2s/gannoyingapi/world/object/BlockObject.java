package dev.fabien2s.gannoyingapi.world.object;

import dev.fabien2s.gannoyingapi.world.GameObject;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.annoyingapi.block.BlockHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public abstract class BlockObject extends GameObject {

    protected Block block;
    private boolean barrierBlock;

    protected BlockObject(GameWorld gameWorld, String name, Location location) {
        super(gameWorld, name, location);
    }

    @Override
    public void init() {
        super.init();

        this.block = location.getBlock();
        this.updateBlock();
    }

    @Override
    public void reset() {
        super.reset();
        this.block.setType(Material.AIR, false);
        this.setBarrierBlock(false);
    }

    protected void updateBlock() {
        BlockData blockData = createBlockData();

        if (blockData instanceof Directional) {
            float yaw = location.getYaw();
            BlockFace blockFace = BlockHelper.getFace(yaw);
            ((Directional) blockData).setFacing(blockFace);
        }

        this.block.setBlockData(blockData, false);
    }

    protected abstract BlockData createBlockData();

    public void setBarrierBlock(boolean barrierBlock) {
        if (this.barrierBlock == barrierBlock)
            return;
        this.barrierBlock = barrierBlock;

        Block upperBlock = block.getRelative(BlockFace.UP);
        if (barrierBlock) {
            if (upperBlock.isPassable())
                upperBlock.setType(Material.BARRIER, false);
        } else if (upperBlock.getType() == Material.BARRIER)
            upperBlock.setType(Material.AIR, false);

    }

}
