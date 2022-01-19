package dev.fabien2s.annoyingapi.statemachine;

public interface IStateMachine<T extends IStateMachine<T>> {

    void setState(IState<T> state);

}
