package org.floric.studies.dellogics.model.classicalplanning;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StateTransitionFunction {
    private State oldState;
    private State newState;
    private Action action;
}
