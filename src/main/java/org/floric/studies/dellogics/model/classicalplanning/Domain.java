package org.floric.studies.dellogics.model.classicalplanning;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Domain {
    private Set<Action> actions;
    private Set<State> states;
    private Set<StateTransitionFunction> transitionFunctions;

    // check if there is any state transition function matching the signature
    public boolean isActionApplicable(Action action, State state) {
        return !transitionFunctions.stream()
                .filter(fun -> fun.getAction().equals(action))
                .filter(fun -> fun.getOldState().equals(state))
                .collect(Collectors.toList())
                .isEmpty();
    }
}
