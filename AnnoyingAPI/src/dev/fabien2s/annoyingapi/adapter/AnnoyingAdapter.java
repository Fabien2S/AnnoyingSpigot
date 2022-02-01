package dev.fabien2s.annoyingapi.adapter;

import com.mojang.brigadier.Message;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.ChatMessage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnoyingAdapter {

//    public GameAdapter() {
//        try {
//            Field field = FastReflection.getField(Entity.class, AtomicInteger.class, 0);
//            AtomicInteger entityCount = (AtomicInteger) field.get(null);
//            entityCount.incrementAndGet();
//        } catch (IllegalAccessException ignored) {
//        }
//    }

    public static Message translate(String arg, Object... args) {
        return new ChatMessage(arg, args);
    }

}
