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

        List<List<ActionWithSymbols>> solutions = tryFindSolutions(actions, startingState, goalState, symbols);

        solutions.sort(Comparator.comparing(List::size));

        if (solutions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(solutions.get(0));
    }

    private List<List<ActionWithSymbols>> tryFindSolutions(Set<ActionScheme> actions, Set<InstancedPredicate> currentState, Set<InstancedPredicate> goalState, Set<Symbol> symbols) {
        List<List<ActionWithSymbols>> allSolutions = Lists.newArrayList();



        return allSolutions;
    }

    private Set<Symbol> getSymbolsFromState(Set<InstancedPredicate> state) {
        return state.stream()
                .map(InstancedPredicate::getSymbols)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
