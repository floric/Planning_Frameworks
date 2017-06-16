package org.floric.studies.dellogics.model.classicalplanning;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SimpleSolverTest {
    @Test
    public void testGetBestSolution() {
        ClassicalPlanningExample example = new ClassicalPlanningExample();

        ClassicalPlanningSolver solver = new ClassicalPlanningSolver();
        ClassicalPlanningTask task = example.createTask();
        Optional<List<Action>> solution = solver.getBestSolution(task);

        assertTrue(solution.isPresent());
        assertThat(solution.get().size(), is(4));

        State currentState = task.getInitialState();
        for(Action action: solution.get()) {
            currentState = solver.applyTransitionFunction(action, currentState, task.getDomain().getTransitionFunctions());
        }

        assertTrue(task.getGoalStates().contains(currentState));
    }
}