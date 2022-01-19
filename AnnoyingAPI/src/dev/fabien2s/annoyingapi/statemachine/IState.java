package dev.fabien2s.annoyingapi.statemachine;

import org.jetbrains.annotations.Nullable;

public interface IState<T extends IStateMachine<T>> {

    default IState<T> getPersistentState() {
        return this;
    }

    void onStateEnter(T t, @Nullable IState<T> previousState);

    void onStateUpdate(T t, double deltaTime);

    void onStateExit(T t);

}
