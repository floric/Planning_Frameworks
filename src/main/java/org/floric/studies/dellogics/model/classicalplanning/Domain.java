package org.floric.studies.dellogics.model.classicalplanning;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Domain {
    private Set<Action> actions;
    private Set<State> states;
    private Set<StateTransitionFunction> transitionFunctions;
}
