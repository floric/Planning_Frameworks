package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
public class ActionScheme {
    private Predicate predicate;
    private Set<Predicate> preConditions;
    private Set<Predicate> effects;

    public Set<InstancedPredicate> apply(Symbol... symbols) {
        return apply(Lists.newArrayList(symbols));
    }

    public Set<InstancedPredicate> apply(List<Symbol> symbols) {
        Map<String, Symbol> mappedSymbols = mapSymbolsToVariables(symbols);

        checkConditions(preConditions, predicate.getVariables());
        checkConditions(effects, predicate.getVariables());
        checkVariableCount();

        return effects.stream()
                .map(effect -> {
                    return new InstancedPredicate(
                            effect.getType(),
                            symbols,
                            effect.isNegated()
                    );
                })
                .collect(Collectors.toSet());
    }

    public boolean canApply(List<Symbol> symbols, Set<InstancedPredicate> state) {
        // check for matching argument count
        Map<String, Symbol> mappedSymbols = mapSymbolsToVariables(symbols);

        // go through all preconditions with these symbols assigned to variables and check if allowed
        return preConditions.stream()
                .map(cond -> isConditionValid(state, cond, mappedSymbols))
                .reduce((a, b) -> a && b)
                .orElse(true);
    }

    private Map<String, Symbol> mapSymbolsToVariables(List<Symbol> symbols, List<String> variableNames) {
        if (symbols.size() != variableNames.size()) {
            throw new RuntimeException("Number of symbols doesn't match signature");
        }

        return IntStream.range(0, symbols.size())
                .mapToObj(a -> new Integer(a))
                .collect(Collectors.toMap(index -> predicate.getVariables().get(index), index -> symbols.get(index)));
    }

    private Map<String, Symbol> mapSymbolsToVariables(List<Symbol> symbols) {
        return mapSymbolsToVariables(symbols, predicate.getVariables());
    }

    private List<Symbol> mapVariablesToSymbol(List<String> variables, Map<String, Symbol> mappedSymbols) {
        return variables.stream().map(var -> mappedSymbols.get(var)).collect(Collectors.toList());
    }

    private void checkVariableCount() {
        boolean isCorrectVariableCount = predicate.getVariables().size() == predicate.getType().getVariablesCount();
        if (!isCorrectVariableCount) {
            throw new RuntimeException(String.format("Signature of % doesn't match correct variables count!", predicate.getType().getName()));
        }
    }

    private boolean checkParameterNames(Predicate predicate, List<String> variableNames) {
        return predicate.getVariables().stream()
                .map(variableNames::contains)
                .reduce((a, b) -> a && b)
                .orElse(false);
    }

    private boolean isConditionValid(Set<InstancedPredicate> state, Predicate condition, Map<String, Symbol> mappedSymbols) {
        // check if state contains condition at all
        List<InstancedPredicate> matchedPredicatesWithSameType = state.stream()
                .filter(predicate -> predicate.getType().equals(this.predicate.getType()))
                .filter(predicate -> predicate.getSymbols().equals(mapVariablesToSymbol(condition.getVariables(), mappedSymbols)))
                .filter(predicate -> predicate.isNegated() == condition.isNegated())
                .collect(Collectors.toList());

        return matchedPredicatesWithSameType.size() == 1;
    }

    private void checkConditions(Set<Predicate> conditions, List<String> variableNames) {
        conditions.forEach(con -> {
            boolean isValid = checkParameterNames(con, variableNames);

            if (!isValid) {
                throw new RuntimeException(String.format("Condition %s in %s has invalid types!", con.getType().getName(), predicate.getType().getName()));
            }
        });
    }
}
