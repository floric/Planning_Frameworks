package org.floric.studies.dellogics.model.strips;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.floric.studies.dellogics.model.PlanningTask;

import java.util.Set;

@Data
@AllArgsConstructor
public class StripsPlanningTask implements PlanningTask {
    private Set<ActionScheme> actions;
    private Set<InstancedPredicate> startingState;
    private Set<InstancedPredicate> goalFormula;
}
