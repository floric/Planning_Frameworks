/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017. florian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
