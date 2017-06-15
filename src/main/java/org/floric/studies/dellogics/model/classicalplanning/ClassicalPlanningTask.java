package org.floric.studies.dellogics.model.classicalplanning;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ClassicalPlanningTask {
    private Domain domain;
    private State initialState;
    private Set<State> goalStates;
}
