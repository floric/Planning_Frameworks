package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.Solver;

import java.util.*;
import java.util.stream.Collectors;

public class StripsSolver implements Solver {

    public Optional<List<ActionWithSymbols>> getBestSolution(StripsPlanningTask task) {
        Set<ActionScheme> actions = task.getActions();
        Set<InstancedPredicate> startingState = task.getStartingState();
        Set<InstancedPredicate> goalState = task.getGoalFormula();

        Set<Symbol> symbols = getSymbolsFromState(startingState);

        Set<List<ActionWithSymbols>> solutions = tryFindSolutions(actions, startingState, goalState, symbols);

        System.out.println("Solutions: " + solutions.size());

        if (solutions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private Set<List<ActionWithSymbols>> tryFindSolutions(Set<ActionScheme> actions, Set<InstancedPredicate> currentState, Set<InstancedPredicate> goalState, Set<Symbol> symbols) {
        return tryFindSolutionsRec(actions, currentState, goalState, symbols, Lists.newArrayList(), Lists.newArrayList());
    }

    private Set<List<ActionWithSymbols>> tryFindSolutionsRec(Set<ActionScheme> actions, Set<InstancedPredicate> currentState, Set<InstancedPredicate> goalState, Set<Symbol> symbols, List<ActionWithSymbols> currentSolution, List<Set<InstancedPredicate>> usedStates) {
        Set<List<ActionWithSymbols>> solutions = Sets.newHashSet();

        if (currentState.equals(goalState)) {
            solutions.add(currentSolution);
            return solutions;
        }

        if (usedStates.contains(currentState)) {
            return solutions;
        }

        usedStates.add(currentState);

        for(ActionScheme scheme: actions) {
            int neededNumberOfSymbols = scheme.getPredicate().getVariables().size();

            // get all possible symbols for this action
            Set<List<Symbol>> possibleSymbolsForAction = getSymbolPermutations(neededNumberOfSymbols, symbols);

            // check if action with any of these symbols is applicable
            Set<List<Symbol>> possibleCalls = possibleSymbolsForAction.stream()
                    .filter(symbolList -> scheme.canApply(symbolList, currentState))
                    .collect(Collectors.toSet());

            System.out.println(String.format("%d different calls for %s possible", possibleCalls.size(), scheme.getPredicate().getType().getName()));

            // get scores for next steps and execute in this order
            Map<List<Symbol>, Integer> scoreMap = possibleCalls.stream()
                    .collect(Collectors.toMap(
                            call -> call,
                            call -> getSimiliarScoreForSolutions(goalState, scheme.apply(call, currentState))
                    ));

            // try all possible combinations
            for (Map.Entry<List<Symbol>, Integer> callEntry : scoreMap.entrySet()) {
                List<ActionWithSymbols> newSolution = Lists.newArrayList(currentSolution);
                newSolution.add(new ActionWithSymbols(scheme, callEntry.getKey()));
                System.out.println(newSolution.stream()
                        .map(s -> s.getActionScheme().getPredicate().getType().getName() + " with " + s.getSymbols()
                                .stream().map(a -> a.getName()).reduce((a, b) -> a + ";" + b))
                        .reduce((a, b) -> a + "\n" + b).get()
                );

                Set<InstancedPredicate> newState = scheme.apply(callEntry.getKey(), currentState);
                List<Set<InstancedPredicate>> newUsedStates = Lists.newArrayList(usedStates);

                solutions.addAll(tryFindSolutionsRec(actions, newState, goalState, symbols, newSolution, newUsedStates));
            }
        }

        return solutions;
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

    private int getSimiliarScoreForSolutions(Set<InstancedPredicate> stateA, Set<InstancedPredicate> stateB) {
        int score = 0;

        for (InstancedPredicate predicateA : stateA) {
            for (InstancedPredicate predicateB :stateB) {
                if (predicateA.getType().equals(predicateB.getType())) {
                    score += 10;

                    if (predicateA.isState() && predicateB.isState()) {
                        score += 5;
                    }

                    for (int i = 0; i < predicateA.getSymbols().size(); i++) {
                        Symbol symbolFromA = predicateA.getSymbols().get(i);
                        Symbol symbolFromB = predicateB.getSymbols().get(i);

                        if(symbolFromA.equals(symbolFromB)) {
                            score += 5;
                        }
                    }
                }
            }
        }

        return score;
    }
}
