package org.floric.studies.dellogics.model.classicalplanning;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSolver {

    public List<Action> getSolution(ClassicalPlanningTask task) {
        Set<StateTransitionFunction> transitionFunctions = task.getDomain().getTransitionFunctions();
        Set<State> goalStates = task.getGoalStates();
        State initialState = task.getInitialState();

        List<List<Action>> solutions = tryFindSolution(initialState, goalStates, Lists.newArrayList(), Lists.newArrayList(), transitionFunctions);
        solutions.forEach(sol -> {
            System.out.println(String.format(
                    "Solution with length %s: %s",
                    sol.size(),
                    sol.stream().map(Action::getDescription).reduce((a, b) -> a + ", " + b).get()
            ));
        });

        return Lists.newArrayList();
    }

    private List<List<Action>> tryFindSolution(State currentState, Set<State> goalStates, List<State> usedStates, List<Action> usedActions, Set<StateTransitionFunction> transitionFunctions) {
        usedStates.add(currentState);

        List<StateTransitionFunction> possibleTransitions = getPossibleAndAllowedStateTransitions(currentState, usedStates, transitionFunctions);
        List<List<Action>> solutions = Lists.newArrayList();

        for (StateTransitionFunction transition: possibleTransitions) {
            List<Action> usedActionsForThisTransition = Lists.newArrayList(usedActions);
            List<State> usedStatesForThisTransition = Lists.newArrayList(usedStates);
            usedActionsForThisTransition.add(transition.getAction());
            usedStatesForThisTransition.add(transition.getNewState());

            if(goalStates.contains(transition.getNewState())) {
                solutions.add(usedActionsForThisTransition);
            } else {
                solutions.addAll(tryFindSolution(transition.getNewState(), goalStates, usedStatesForThisTransition, usedActionsForThisTransition, transitionFunctions));
            }
        }

        return solutions;
    }

    private List<StateTransitionFunction> getPossibleStateTransitions(State currentState, Set<StateTransitionFunction> transitionFunctions) {
        return transitionFunctions.stream()
                .filter(transition -> transition.getOldState().equals(currentState))
                .collect(Collectors.toList());
    }

    private List<StateTransitionFunction> getPossibleAndAllowedStateTransitions(State currentState, List<State> usedStates, Set<StateTransitionFunction> transitionFunctions) {
        return getPossibleStateTransitions(currentState, transitionFunctions).stream()
                .filter(transition -> !usedStates.contains(transition.getNewState()))
                .collect(Collectors.toList());
    }
}
