package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.Example;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ActionSchemeTest {

    private ActionScheme scheme;
    private List<Symbol> symbols = Lists.newArrayList((Symbol) () -> "TestSymbol");
    private PredicateType predicateTypeA = new PredicateType("ActionToTest", 1);
    private PredicateType predicateTypeB = new PredicateType("ActionToTest2", 1);
    private Predicate predicateA = new Predicate(predicateTypeA, "varA");

    @Before
    public void setUp() {
        scheme = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet());
    }

    @Test
    public void apply() throws Exception {

    }

    @Test
    public void canApply() throws Exception {

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
        assertFalse(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, () -> "Something else"), new InstancedPredicate(predicateTypeB))));
    }

    @Test(expected = RuntimeException.class)
    public void testCanApplyWithNonMatchingArgumentsCount() throws Exception {
        symbols.add(() -> "Added another symbol");
        assertFalse(scheme.canApply(symbols, Sets.newHashSet(new InstancedPredicate(predicateTypeA, true, symbols))));
    }

    /*@Test
    public void testIsValidWithUselessActionScheme() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");
        ActionScheme actionWithoutAnyChanges = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet());

        boolean isValid = actionWithoutAnyChanges.isValid(Sets.newHashSet());

        assertFalse(isValid);
    }

    @Test
    public void testIsValidWithDifferentActionScheme() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        PredicateType predicateTypeB = new PredicateType("B", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");
        ActionScheme actionWithoutAnyChanges = new ActionScheme(predicateA, Sets.newHashSet(), Sets.newHashSet());

        Symbol symbolForB = () -> "For B";

        boolean isValid = actionWithoutAnyChanges.isValid(Sets.newHashSet(new InstancedPredicate(predicateTypeB, symbolForB)));

        assertFalse(isValid);
    }

    @Test
    public void testIsValidWithCorrectActionSchemeButNegated() {
        PredicateType predicateTypeA = new PredicateType("A", 1);
        Predicate predicateA = new Predicate(predicateTypeA, "varA");
        ActionScheme actionWithoutAnyChanges = new ActionScheme(predicateA, Sets.newHashSet(predicateA), Sets.newHashSet());

        Symbol symbolForA = () -> "For A";

        boolean isValid = actionWithoutAnyChanges.isValid(Sets.newHashSet(new InstancedPredicate(predicateTypeA, false, symbolForA)));

        assertFalse(isValid);
    }

    @Test
    public void testIsValidWithCorrectActionScheme() {
        PredicateType predicateTypeA = new PredicateType("CoolAction", 2);
        Predicate predicateA = new Predicate(predicateTypeA, "varA", "varB");

        PredicateType predicateTypeB = new PredicateType("PredicateToCheck", 1);
        Predicate predicateB = new Predicate(predicateTypeB, "var");
        ActionScheme actionWithoutAnyChanges = new ActionScheme(predicateA, Sets.newHashSet(predicateB), Sets.newHashSet());

        Symbol symbolForA = () -> "For A";

        boolean isValid = actionWithoutAnyChanges.isValid(Sets.newHashSet(new InstancedPredicate(predicateTypeB, symbolForA)));

        assertTrue(isValid);
    }*/
}