package dev.fabien2s.annoyingapi.entity;

import dev.fabien2s.annoyingapi.util.HandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.MainHand;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityHelper {

    public static HandType getHand(HumanEntity human, MainHand handSide) {
        MainHand mainHand = human.getMainHand();
        switch (handSide) {
            case LEFT:
                return mainHand == MainHand.LEFT ? HandType.MAIN_HAND : HandType.OFF_HAND;
            case RIGHT:
                return mainHand == MainHand.LEFT ? HandType.OFF_HAND : HandType.MAIN_HAND;
            default:
                return HandType.MAIN_HAND;
        }
    }

}
