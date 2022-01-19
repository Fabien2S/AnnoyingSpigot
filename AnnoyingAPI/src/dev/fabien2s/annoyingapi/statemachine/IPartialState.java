package dev.fabien2s.annoyingapi.statemachine;

public interface IPartialState<T extends IStateMachine<T>> extends IState<T> {

    IState<T> getPreviousState();

    @Override
    default IState<T> getPersistentState() {
        IState<T> previousState;
        do {
            previousState = getPreviousState();
        } while (previousState instanceof IPartialState);
        return previousState;
    }

    default void restore(T stateMachine) {
        IState<T> persistentState = getPersistentState();
        stateMachine.setState(persistentState);
    }

}
