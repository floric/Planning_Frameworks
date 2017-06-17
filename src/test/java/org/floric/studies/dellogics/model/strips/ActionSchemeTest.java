package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.strips.exceptions.InvalidPredicateException;
import org.floric.studies.dellogics.model.strips.exceptions.NoNegPredicatesAllowedException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ActionSchemeTest {

    private ActionScheme scheme;
    private List<Symbol> symbols = Lists.newArrayList(new Symbol("TestSymbol"));
    private PredicateType predicateTypeA = new PredicateType("ActionToTest", 1);
    private PredicateType predicateTypeB = new PredicateType("ActionToTest2", 1);
    private Predicate predicateA = new Predicate(predicateTypeA, "varA");

    private PredicateType isWrapped = new PredicateType("IsWrapped", 1);
    private PredicateType isPresent = new PredicateType("IsPresent", 1);

    @Before
    public void setUp() {
        scheme = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet());
    }

    @Test(expected = RuntimeException.class)
    public void testApplyWithEmptyState() throws Exception {
        Set<InstancedPredicate> state = Sets.newHashSet();
        scheme.setPreConditions(Sets.newHashSet(new Predicate(isWrapped, "varA")));
        Set<InstancedPredicate> newState = scheme.apply(symbols, state);
        assertTrue(newState.isEmpty());
    }

    @Test
    public void testApplyWithNoEffects() throws Exception {
        List<Symbol> customSymbols = Lists.newArrayList(new Symbol("MainSymbol"));
        InstancedPredicate matchingConditionInstance = new InstancedPredicate(predicateTypeA, customSymbols);
        InstancedPredicate unchangedInstance = new InstancedPredicate(predicateTypeB, new Symbol("OtherSymbol"));
        Set<InstancedPredicate> state = Sets.newHashSet(matchingConditionInstance, unchangedInstance);
        scheme.setPreConditions(Sets.newHashSet(new Predicate(predicateTypeA,"varA")));

        Set<InstancedPredicate> newState = scheme.apply(customSymbols, state);

        assertTrue(newState.size() == state.size());
        assertTrue(newState.contains(unchangedInstance));
        assertTrue(newState.contains(matchingConditionInstance));
    }

    @Test
    public void testApplyWithEffectsWithNewInstancedPredicate() throws Exception {
        Set<InstancedPredicate> state = Sets.newHashSet();
        scheme.setEffects(Sets.newHashSet(new Predicate(isWrapped, "varA")));

        Set<InstancedPredicate> newState = scheme.apply(symbols, state);

        assertFalse(newState.isEmpty());
        List<InstancedPredicate> newIsWrappedInstance = newState.stream()
                .filter(p -> p.getType().equals(isWrapped))
                .collect(Collectors.toList());
        assertFalse(newIsWrappedInstance.isEmpty());
        assertTrue(newIsWrappedInstance.get(0).getType().equals(isWrapped));
        assertTrue(newIsWrappedInstance.get(0).getSymbols().equals(symbols));
    }

    @Test
    public void testApplyWithEffectsWithChangedInstancedPredicate() throws Exception {
        Predicate isWrappedPredicateCondition = new Predicate(isWrapped, false, "varA");
        Predicate isWrappedPredicateEffect = new Predicate(isWrapped, "varA");
        Predicate isPresentPredicate = new Predicate(isPresent, "varA");
        Symbol presentA = new Symbol("PresentA");
        Symbol presentB = new Symbol("PresentB");
        List<Symbol> usedSymbols = Lists.newArrayList(presentA);
        Set<InstancedPredicate> state = Sets.newHashSet(
                new InstancedPredicate(isPresent, presentA),
                new InstancedPredicate(isPresent, presentB)
        );

        scheme.setPredicate(new Predicate(new PredicateType("WrapPresent", 1), "varA"));
        scheme.setPreConditions(Sets.newHashSet(isPresentPredicate, isWrappedPredicateCondition));
        scheme.setEffects(Sets.newHashSet(isWrappedPredicateEffect));

        Set<InstancedPredicate> newState = scheme.apply(usedSymbols, state);

        assertThat(newState.size(), is(state.size() + 1));

        List<InstancedPredicate> addedIsWrappedInstance = newState.stream()
                .filter(p -> p.getType().equals(isWrapped))
                .filter(p -> p.getSymbols().contains(presentA))
                .collect(Collectors.toList());
        assertFalse(addedIsWrappedInstance.isEmpty());
        assertTrue(addedIsWrappedInstance.get(0).getType().equals(isWrapped));
        assertTrue(addedIsWrappedInstance.get(0).isState());
        assertTrue(addedIsWrappedInstance.get(0).getSymbols().equals(usedSymbols));
    }

    @Test
    public void testCanApplyWithEmptyPreConditions() throws Exception {
        assertTrue(scheme.canApply(symbols, Sets.newHashSet()));
        assertTrue(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeB))));
    }

    @Test
    public void testCanApplyWithValidPreConditions() throws Exception {
        scheme.setPreConditions(Sets.newHashSet(predicateA));
        assertTrue(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, symbols))));
        assertTrue(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, symbols), new InstancedPredicate(predicateTypeB))));

        scheme.setPreConditions(Sets.newHashSet(new Predicate(predicateTypeA, true,"varA")));
        assertTrue(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, true, symbols))));
    }

    @Test
    public void testCanApplyWithInvalidPreConditions() throws Exception {
        scheme.setPreConditions(Sets.newHashSet(predicateA));
        assertFalse(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA))));
        assertFalse(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, new Symbol("Something else")), new InstancedPredicate(predicateTypeB))));
    }

    @Test(expected = RuntimeException.class)
    public void testCanApplyWithNonMatchingArgumentsCount() throws Exception {
        symbols.add(new Symbol("AddedSymbol"));
        scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, true, symbols)));
    }

    @Test(expected = InvalidPredicateException.class)
    public void testApplyWithInvalidPredicate() {
        PredicateType predicateTypeA = new PredicateType("A", 2);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet());

        scheme.apply(Lists.newArrayList(), Sets.newHashSet());
    }

    @Test(expected = InvalidPredicateException.class)
    public void testApplyWithInvalidPreConditionsPredicateParameterCount() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");

        Predicate preConditionPredicateA = new Predicate(predicateTypeA, "varA", "varB");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(preConditionPredicateA), Sets.newHashSet());

        scheme.apply(Lists.newArrayList(), Sets.newHashSet());
    }

    @Test(expected = InvalidPredicateException.class)
    public void testApplyWithInvalidPreConditionsPredicateParameterName() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");

        Predicate preConditionPredicateA = new Predicate(predicateTypeA, "varX");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(preConditionPredicateA), Sets.newHashSet());

        scheme.apply(Lists.newArrayList(), Sets.newHashSet());
    }

    @Test
    public void testApplyWithInvalidPreConditionsWithNegation() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA,"varA");

        Predicate preConditionPredicateA = new Predicate(predicateTypeA, false,"varA");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(preConditionPredicateA), Sets.newHashSet());

        scheme.apply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, symbols)));
    }

    @Test(expected = InvalidPredicateException.class)
    public void testApplyWithInvalidEffectsPredicateParameterName() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");

        Predicate effectsPredicateA = new Predicate(predicateTypeA, "varX");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet(effectsPredicateA));

        scheme.apply(Lists.newArrayList(), Sets.newHashSet());
    }

    @Test(expected = InvalidPredicateException.class)
    public void testApplyWithInvalidEffectsPredicateParameterCount() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");

        Predicate effectsPredicateA = new Predicate(predicateTypeA, "varA", "varB");
        scheme = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet(effectsPredicateA));

        scheme.apply(Lists.newArrayList(), Sets.newHashSet());
    }

    @Test
    public void testApplyWithoutChange() {
        PredicateType isKnownType = new PredicateType("IsKnown", 1);
        Predicate isKnown = new Predicate(isKnownType, "varA");

        Symbol elemInstance = new Symbol("ElemInstance");
        Set<InstancedPredicate> oldState = Sets.newHashSet(new InstancedPredicate(isKnownType, elemInstance));

        scheme.setEffects(Sets.newHashSet(isKnown));
        Set<InstancedPredicate> newState = scheme.apply(Lists.newArrayList(elemInstance), oldState);

        assertThat(newState.size(), is(1));
    }

    @Test(expected = NoNegPredicatesAllowedException.class)
    public void testApplyWithNegativeStatePredicatesNotAllowed() {
        PredicateType isKnownType = new PredicateType("IsKnown", 1);

        Symbol elemInstance = new Symbol("ElemInstance");
        Set<InstancedPredicate> oldState = Sets.newHashSet(new InstancedPredicate(isKnownType, false, elemInstance));

        scheme.apply(Lists.newArrayList(elemInstance), oldState);
    }
}