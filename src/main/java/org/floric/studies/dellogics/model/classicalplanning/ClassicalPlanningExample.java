package org.floric.studies.dellogics.model.classicalplanning;

import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.Example;

import java.util.List;
import java.util.Set;

public class ClassicalPlanningExample implements Example {

    @Override
    public void run() {
        ClassicalPlanningTask task = createTask();

        SimpleSolver solver = new SimpleSolver();
        List<Action> solution = solver.getSolution(task);

        solution.forEach(step -> System.out.println(step.getDescription()));
    }

    @Override
    public String getDescription() {
        return "Classical planning with a state transition system.";
    }

    private static ClassicalPlanningTask createTask() {
        // actions
        Action goToPOAction = () -> "Go to PO";
        Action goToHomeAction = () -> "Go home";
        Action pickUpPresentAction = () -> "Pick up present";
        Action wrapPresentAction = () -> "Wrap present";

        // states
        State s1 = () -> "At home, present at PO, present unwrapped";
        State s2 = () -> "At PO, present at PO, present unwrapped";
        State s3 = () -> "At PO, got present, present unwrapped";
        State s4 = () -> "At home, got present, present unwrapped";
        State s5 = () -> "At home, got present, present wrapped";
        State s6 = () -> "At PO, got present, present wrapped";

        // state transition functions
        StateTransitionFunction transitionS1S2 = new StateTransitionFunction(s1, s2, goToPOAction);
        StateTransitionFunction transitionS2S3 = new StateTransitionFunction(s2, s3, pickUpPresentAction);
        StateTransitionFunction transitionS3S4 = new StateTransitionFunction(s3, s4, goToHomeAction);
        StateTransitionFunction transitionS3S6 = new StateTransitionFunction(s3, s6, wrapPresentAction);
        StateTransitionFunction transitionS4S5 = new StateTransitionFunction(s4, s5, wrapPresentAction);
        StateTransitionFunction transitionS5S6 = new StateTransitionFunction(s5, s6, goToPOAction);
        StateTransitionFunction transitionS2S1 = new StateTransitionFunction(s2, s1, goToHomeAction);
        StateTransitionFunction transitionS4S3 = new StateTransitionFunction(s4, s3, goToPOAction);
        StateTransitionFunction transitionS6S5 = new StateTransitionFunction(s6, s5, goToHomeAction);
        StateTransitionFunction transitionS1 = new StateTransitionFunction(s1, s1, goToHomeAction);
        StateTransitionFunction transitionS2 = new StateTransitionFunction(s2, s2, goToPOAction);
        StateTransitionFunction transitionS3 = new StateTransitionFunction(s3, s3, goToPOAction);
        StateTransitionFunction transitionS4 = new StateTransitionFunction(s4, s4, goToHomeAction);
        StateTransitionFunction transitionS5 = new StateTransitionFunction(s5, s5, goToHomeAction);
        StateTransitionFunction transitionS6 = new StateTransitionFunction(s6, s6, goToPOAction);

        // domain
        Set<Action> actions = Sets.newHashSet(goToPOAction, goToHomeAction, pickUpPresentAction, wrapPresentAction);
        Set<State> states = Sets.newHashSet(s1, s2, s3, s4, s5, s6);
        Set<StateTransitionFunction> transitionFunctions = Sets.newHashSet(
                transitionS1, transitionS1S2,
                transitionS2, transitionS2S1, transitionS2S3,
                transitionS3, transitionS3S4, transitionS3S6,
                transitionS4, transitionS4S3, transitionS4S5,
                transitionS5, transitionS5S6,
                transitionS6, transitionS6S5
        );

        // planning task
        Domain domain = new Domain(actions, states, transitionFunctions);
        Set<State> goalStates = Sets.newHashSet(s5);
        State startState = s1;

        return new ClassicalPlanningTask(domain, startState, goalStates);
    }
}
