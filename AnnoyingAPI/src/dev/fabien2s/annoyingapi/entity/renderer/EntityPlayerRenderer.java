package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.util.BitSet;
import dev.fabien2s.annoyingapi.util.SkinPart;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class EntityPlayerRenderer extends EntityLivingRenderer<Player, EntityPlayerRenderer> {

    public EntityPlayerRenderer(EntityPlayerRenderer parent, Player entity, IEntityController controller) {
        super(parent, entity, controller);
    }

    @Override
    public void applyTo(EntityPlayerRenderer other) {
        super.applyTo(other);

        SkinPart[] skinParts = getSkinParts();
        other.setSkinPart(skinParts);
    }

    public SkinPart[] getSkinParts() {
        Set<SkinPart> skinPartSet = new HashSet<>();
        SkinPart[] values = SkinPart.values();
        byte skinPartMask = controller.getSkinPartMask();
        for (int i = 0; i < values.length; i++) {
            if (BitSet.has(skinPartMask, 1 << i))
                skinPartSet.add(values[i]);
        }
        return skinPartSet.toArray(new SkinPart[0]);
    }

    public void setSkinPart(SkinPart... skinParts) {
        byte mask = 0;
        for (SkinPart part : skinParts)
            mask |= part.getMask();
        this.controller.setSkinPartMask(mask);
    }

}
