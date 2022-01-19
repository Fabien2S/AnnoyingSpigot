package dev.fabien2s.annoyingapi.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InteractionInterruptCause {

    SELF(false),
    SELF_BY_USER(false),

    CANCELLED(true),
    CANCELLED_BY_USER(true),
    CANCELLED_BY_GAME(true),
    CANCELLED_BY_INTERACTION(true);

    @Getter private final boolean cancelled;

}
