package org.floric.studies.dellogics.model.strips;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class StripsSolverTest {
    @Test
    public void checkSolution() {
        StripsExample example = new StripsExample();

        StripsSolver solver = new StripsSolver();
        StripsPlanningTask task = example.createTask();

        Optional<List<ActionApplication>> solution = solver.getBestSolution(task);

        assertTrue(solution.isPresent());
        assertThat(solution.get().size(), is(4));

        Set<InstancedPredicate> currentState = task.getStartingState();
        for(ActionApplication action: solution.get()) {
            ActionScheme actionScheme = action.getActionScheme();
            List<Symbol> symbols = action.getSymbols();

            currentState = actionScheme.apply(symbols, currentState);
        }

        assertTrue(StripsSolver.isContainingGoalState(currentState, task.getGoalFormula()));
    }
}