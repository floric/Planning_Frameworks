package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.Solver;

import java.util.*;
import java.util.stream.Collectors;

public class StripsSolver implements Solver {

    public Optional<List<ActionApplication>> getBestSolution(StripsPlanningTask task) {
        Set<ActionScheme> actions = task.getActions();
        Set<InstancedPredicate> startingState = task.getStartingState();
        Set<InstancedPredicate> goalState = task.getGoalFormula();

        Set<Symbol> symbols = getSymbolsFromState(startingState);

        List<List<ActionApplication>> solutions = Lists.newArrayList(tryFindSolutions(actions, startingState, goalState, symbols));

        if (solutions.isEmpty()) {
            return Optional.empty();
        }

        solutions.sort(Comparator.comparing(List::size));

        return Optional.ofNullable(solutions.get(0));
    }

    private Set<List<ActionApplication>> tryFindSolutions(Set<ActionScheme> actions, Set<InstancedPredicate> currentState, Set<InstancedPredicate> goalState, Set<Symbol> symbols) {
        return tryFindSolutionsRec(actions, currentState, goalState, symbols, Lists.newArrayList(), Lists.newArrayList());
    }

    private Set<List<ActionApplication>> tryFindSolutionsRec(Set<ActionScheme> actions, Set<InstancedPredicate> currentState, Set<InstancedPredicate> goalState, Set<Symbol> symbols, List<ActionApplication> currentSolution, List<Set<InstancedPredicate>> usedStates) {
        Set<List<ActionApplication>> solutions = Sets.newHashSet();

        // check for solution state
        if (isContainingGoalState(currentState, goalState)) {
            solutions.add(currentSolution);
            return solutions;
        }

        // avoid cycles where old states are repeated
        if (usedStates.contains(currentState)) {
            return solutions;
        }

        usedStates.add(currentState);

        // try all possible actions recursively
        for(ActionScheme scheme: actions) {
            int neededNumberOfSymbols = scheme.getPredicate().getVariables().size();

            // get all possible symbols for this action
            Set<List<Symbol>> possibleSymbolsForAction = getSymbolPermutations(neededNumberOfSymbols, symbols);

            // check if action with any of these symbols is applicable
            Set<List<Symbol>> possibleCalls = possibleSymbolsForAction.stream()
                    .filter(symbolList -> scheme.canApply(symbolList, currentState))
                    .collect(Collectors.toSet());

            // try all possible combinations
            for (List<Symbol> call: possibleCalls) {
                List<ActionApplication> newSolution = Lists.newArrayList(currentSolution);
                newSolution.add(new ActionApplication(scheme, call));

                // apply new action
                Set<InstancedPredicate> newState = scheme.apply(call, currentState);
                List<Set<InstancedPredicate>> newUsedStates = Lists.newArrayList(usedStates);

                // try again all new possible actions
                solutions.addAll(tryFindSolutionsRec(actions, newState, goalState, symbols, newSolution, newUsedStates));
            }
        }

        return solutions;
    }

    public static boolean isContainingGoalState(Set<InstancedPredicate> state, Set<InstancedPredicate> goalState) {
        return goalState.stream()
                .map(state::contains)
                .reduce((a, b) -> a && b)
                .orElse(true);
    }

    public static void printState(Set<InstancedPredicate> state) {
        System.out.println("State:");
        System.out.println(state.stream()
                .map(instancedPredicate -> String.format(
                        "%s(%s)",
                        instancedPredicate.getType().getName(),
                        instancedPredicate.getSymbols().stream()
                                .map(symbol -> symbol.getName())
                                .reduce((a, b) -> String.format("%s,%s", a, b))
                                .get()
                ))
                .reduce((a,b) -> String.format("%s|%s", a, b))
                .orElse(""));
    }

    public static void printSolution(List<ActionApplication> solution) {
        System.out.println("Solution:");
        solution.forEach(step -> System.out.println(String.format(
                "Step: %s(%s)",
                step.getActionScheme().getPredicate().getType().getName(),
                step.getSymbols().stream().map(symbol -> symbol.getName()).reduce((a, b) -> String.format("%s, %s", a, b)).get()
        )));
    }

    private Set<List<Symbol>> getSymbolPermutations(int permutationSize, Set<Symbol> symbols) {
        if (permutationSize <= 0) {
            throw new RuntimeException("Permutations needs to be higher then 0");
        }

        return getSymbolPermutationsRec(permutationSize, symbols, Lists.newArrayList());
    }

    private Set<List<Symbol>> getSymbolPermutationsRec(int permutationSize, Set<Symbol> symbols, List<Symbol> currentList) {
        Set<List<Symbol>> result = Sets.newHashSet();

        if (currentList.size() == permutationSize) {
            result.add(currentList);
            return result;
        }

        for (Symbol s: symbols) {
            List<Symbol> newList = Lists.newArrayList(currentList);
            newList.add(s);
            result.addAll(getSymbolPermutationsRec(permutationSize, symbols, newList));
        }

        return result;
    }

    private Set<Symbol> getSymbolsFromState(Set<InstancedPredicate> state) {
        return state.stream()
                .map(InstancedPredicate::getSymbols)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
