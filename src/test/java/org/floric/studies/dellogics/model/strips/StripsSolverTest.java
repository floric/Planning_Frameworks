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
        Optional<List<ActionWithSymbols>> solution = solver.getBestSolution(task);

        /*assertTrue(solution.isPresent());
        assertThat(solution.get().size(), is(4));

        Set<InstancedPredicate> currentState = task.getStartingState();
        for(ActionWithSymbols action: solution.get()) {

        }

        assertTrue(task.getGoalFormula().equals(currentState));*/
    }
}