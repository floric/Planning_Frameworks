package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Sets;
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

    public Set<InstancedPredicate> apply(List<Symbol> symbols, Set<InstancedPredicate> state) {
        // check preconditions
        if (!canApply(symbols, state)) {
            throw new RuntimeException("Preconditions are not met. Can't be applied!");
        }

        // do additional tests to avoid human errors during modelling
        checkConditions(preConditions, predicate.getVariables());
        checkConditions(effects, predicate.getVariables());
        checkVariableCountOfPredicate();
        checkSymbolsCountMatchingPredicate(symbols);

        // collect all untouched predicate instances for output
        //Set<InstancedPredicate> untouchedPredicateInstances = getUntouchedPredicates(state, effects, symbols);

        // modify matching predicate instances based on type and symbols
        Set<InstancedPredicate> newAndModifiedPredicateInstances = modifyOrCreateMatchingInstancedPredicates(symbols, state, effects);

        // combine combine to set and return
        return Sets.union(state, newAndModifiedPredicateInstances);
    }

    public boolean canApply(List<Symbol> symbols, Set<InstancedPredicate> state) {
        checkSymbolsCountMatchingPredicate(symbols);

        // go through all preconditions with these symbols assigned to variables and check if allowed
        // allow if no preconditions exist at all
        return preConditions.stream()
                .map(cond -> isConditionValid(state, cond, symbols))
                .reduce((a, b) -> a && b)
                .orElse(true);
    }

    private Set<InstancedPredicate> modifyOrCreateMatchingInstancedPredicates(List<Symbol> symbols, Set<InstancedPredicate> state, Set<Predicate> effects) {
        return effects.stream()
                .map(effect -> getInstancePredicateForEffect(symbols, state, effect))
                .collect(Collectors.toSet());
    }

    private InstancedPredicate getInstancePredicateForEffect(List<Symbol> symbols, Set<InstancedPredicate> state, Predicate effect) {
        Optional<InstancedPredicate> matchingStatePredicateFromCondition = getMatchingStatePredicateFromCondition(state, effect, symbols);
        if(matchingStatePredicateFromCondition.isPresent()) {
            InstancedPredicate matchingInstance = matchingStatePredicateFromCondition.get();
            matchingInstance.setState(effect.isState());
            return matchingInstance;
        } else {
            Map<String, Symbol> symbolMappings = mapSymbolsToVariables(symbols);
            return new InstancedPredicate(effect.getType(), effect.isState(), mapVariablesToSymbol(effect.getVariables(), symbolMappings));
        }
    }

    private Map<String, Symbol> mapSymbolsToVariables(List<Symbol> symbols, List<String> variableNames) {
        if (symbols.size() != variableNames.size()) {
            throw new RuntimeException("Number of symbols doesn't match signature");
        }

        return IntStream.range(0, symbols.size())
                .mapToObj(a -> (a))
                .collect(Collectors.toMap(index -> predicate.getVariables().get(index), symbols::get));
    }

    private Map<String, Symbol> mapSymbolsToVariables(List<Symbol> symbols) {
        return mapSymbolsToVariables(symbols, predicate.getVariables());
    }

    private List<Symbol> mapVariablesToSymbol(List<String> variables, Map<String, Symbol> mappedSymbols) {
        return variables.stream().map(mappedSymbols::get).collect(Collectors.toList());
    }

    private void checkSymbolsCountMatchingPredicate(List<Symbol> symbols) {
        boolean doArgumentsMatch = predicate.getVariables().size() == symbols.size();
        if (!doArgumentsMatch) {
            throw new RuntimeException("Arguments count doesn't match!");
        }
    }

    private void checkVariableCountOfPredicate() {
        boolean isCorrectVariableCount = predicate.getVariables().size() == predicate.getType().getVariablesCount();
        if (!isCorrectVariableCount) {
            throw new RuntimeException(String.format("Signature of %s doesn't match correct variables count!", predicate.getType().getName()));
        }
    }

    private boolean checkParameterNames(Predicate predicate, List<String> variableNames) {
        return predicate.getVariables().stream()
                .map(variableNames::contains)
                .reduce((a, b) -> a && b)
                .orElse(false);
    }

    private boolean isConditionValid(Set<InstancedPredicate> state, Predicate condition, List<Symbol> symbols) {
        return getMatchingStatePredicateFromCondition(state, condition, symbols).isPresent();
    }

    private Set<InstancedPredicate> getUntouchedPredicates(Set<InstancedPredicate> state, Set<Predicate> effects, List<Symbol> symbols) {
        return state.stream()
                .filter(instance -> !getMatchingEffectPredicateFromState(instance, effects, symbols).isPresent())
                .collect(Collectors.toSet());
    }

    private Optional<Predicate> getMatchingEffectPredicateFromState(InstancedPredicate predicateInstance, Set<Predicate> effects, List<Symbol> symbols) {
        Map<String, Symbol> mappedSymbols = mapSymbolsToVariables(symbols);

        // ignore negative state of effect as the state is modified!
        List<Predicate> matchedPredicates = effects.stream()
                .filter(effect -> effect.getType().equals(predicateInstance))
                .filter(effect -> mapVariablesToSymbol(effect.getVariables(), mappedSymbols).equals(predicateInstance.getSymbols()))
                .collect(Collectors.toList());

        // maximum one predicate can match!
        if (matchedPredicates.size() > 1) {
            throw new RuntimeException("Multiple predicates with same signature found!");
        } else if (matchedPredicates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(matchedPredicates.get(0));
    }

    private Optional<InstancedPredicate> getMatchingStatePredicateFromCondition(Set<InstancedPredicate> state, Predicate condition, List<Symbol> symbols) {
        Map<String, Symbol> mappedSymbols = mapSymbolsToVariables(symbols);

        // get all matching predicates with same type, symbols and conditions state
        List<InstancedPredicate> matchedPredicates = state.stream()
                .filter(predicate -> predicate.getType().equals(condition.getType()))
                .filter(predicate -> predicate.getSymbols().equals(mapVariablesToSymbol(condition.getVariables(), mappedSymbols)))
                .filter(predicate -> predicate.isState() == condition.isState())
                .collect(Collectors.toList());

        // maximum one predicate can match!
        if (matchedPredicates.size() > 1) {
            throw new RuntimeException("Multiple predicates with same signature found!");
        } else if (matchedPredicates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(matchedPredicates.get(0));
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
