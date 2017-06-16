package org.floric.studies.dellogics.model.classicalplanning;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.floric.studies.dellogics.model.PlanningTask;

import java.util.Set;

@Data
@AllArgsConstructor
public class ClassicalPlanningTask implements PlanningTask {
    private Domain domain;
    private State initialState;
    private Set<State> goalStates;
}
