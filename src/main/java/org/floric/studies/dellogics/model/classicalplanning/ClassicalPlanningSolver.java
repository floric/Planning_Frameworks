package org.floric.studies.dellogics.model.classicalplanning;

import com.google.common.collect.Lists;
import org.floric.studies.dellogics.model.Solver;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassicalPlanningSolver implements Solver {

    public Optional<List<Action>> getBestSolution(ClassicalPlanningTask task) {
        Set<StateTransitionFunction> transitionFunctions = task.getDomain().getTransitionFunctions();
        Set<State> goalStates = task.getGoalStates();
        State initialState = task.getInitialState();

        List<List<Action>> solutions = tryFindSolution(initialState, goalStates, Lists.newArrayList(), Lists.newArrayList(), transitionFunctions);

        solutions.sort(Comparator.comparing(List::size));

        return Optional.ofNullable(solutions.get(0));
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

    public List<StateTransitionFunction> getPossibleStateTransitions(State currentState, Set<StateTransitionFunction> transitionFunctions) {
        return transitionFunctions.stream()
                .filter(transition -> transition.getOldState().equals(currentState))
                .collect(Collectors.toList());
    }

    public List<StateTransitionFunction> getPossibleAndAllowedStateTransitions(State currentState, List<State> usedStates, Set<StateTransitionFunction> transitionFunctions) {
        return getPossibleStateTransitions(currentState, transitionFunctions).stream()
                .filter(transition -> !usedStates.contains(transition.getNewState()))
                .collect(Collectors.toList());
    }

    public State applyTransitionFunction(Action action, State state, Set<StateTransitionFunction> transitionFunctions) {
        List<StateTransitionFunction> matchingTransitionFunctions = transitionFunctions.stream()
                .filter(transition -> transition.getAction().equals(action) && transition.getOldState().equals(state))
                .collect(Collectors.toList());

        if(matchingTransitionFunctions.size() != 1) {
            throw new IllegalStateException(String.format(
                    "Not exactly one matching transition found, instead found %s!",
                    matchingTransitionFunctions.size()
            ));
        }

        return matchingTransitionFunctions.get(0).getNewState();
    }
}
