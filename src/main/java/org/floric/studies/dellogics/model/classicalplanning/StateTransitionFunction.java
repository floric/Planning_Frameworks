package org.floric.studies.dellogics.model.classicalplanning;

import lombok.Data;

@Data
public class StateTransitionFunction {

    private State oldState;
    private State newState;
    private Action action;

    public StateTransitionFunction(State oldState, State newState, Action action) {
        this.oldState = oldState;
        this.newState = newState;
        this.action = action;
    }
}
