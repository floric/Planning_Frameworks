package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.floric.studies.dellogics.model.Example;

import java.lang.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StripsExample implements Example {

    @Override
    public void run() {
        StripsPlanningTask task = createTask();

        StripsSolver solver = new StripsSolver();
        Optional<List<ActionWithSymbols>> solution = solver.getBestSolution(task);

        if (solution.isPresent()) {
            List<String> actions = solution.get().stream().map(actionScheme -> actionScheme.getActionScheme().getPredicate().getType().getName()).collect(Collectors.toList());
            System.out.println("Solution: " + StringUtils.join(actions, ", "));
        }
    }

    public StripsPlanningTask createTask() {
        Symbol father = () -> "Father";
        Symbol present = () -> "Present";
        Symbol postOffice = () -> "Postoffice";
        Symbol home = () -> "Home";

        PredicateType at = new PredicateType("At", 2);
        PredicateType has = new PredicateType("Has", 2);
        PredicateType wrapped = new PredicateType("Wrapped", 1);
        PredicateType isAgent = new PredicateType("IsAgent", 1);
        PredicateType isLocation = new PredicateType("IsLocation", 1);
        PredicateType isObject = new PredicateType("IsObject", 1);

        PredicateType go = new PredicateType("Go", 3);
        PredicateType pickUp = new PredicateType("PickUp", 3);
        PredicateType wrap = new PredicateType("Wrap", 2);

        ActionScheme goAction = new ActionScheme(
                new Predicate(go, "agt", "from", "to"),
                Sets.newHashSet(new Predicate(at, "agt", "from"), new Predicate(isAgent, "agt"), new Predicate(isLocation, "from"), new Predicate(isLocation, "to")),
                Sets.newHashSet(new Predicate(at, "agt", "to"), new Predicate(at, true, "agt", "from"))
        );

        ActionScheme pickUpAction = new ActionScheme(
                new Predicate(pickUp, "agt", "obj", "from"),
                Sets.newHashSet(new Predicate(at, "agt", "from"), new Predicate(at, "obj", "from"), new Predicate(has, false, "agt", "obj"), new Predicate(isAgent, "agt"), new Predicate(isObject, "obj"), new Predicate(isLocation, "from")),
                Sets.newHashSet(new Predicate(has, "agt", "obj"), new Predicate(has, true, "obj", "from"))
        );

        ActionScheme wrapAction = new ActionScheme(
                new Predicate(wrap, "agt", "obj"),
                Sets.newHashSet(new Predicate(has, "agt", "obj"), new Predicate(wrapped, false,"obj"), new Predicate(isAgent, "agt"), new Predicate(isObject, "obj")),
                Sets.newHashSet(new Predicate(wrapped, "obj"))
        );

        Set<InstancedPredicate> newState = goAction.apply(father, home, postOffice);

        // task
        Set<InstancedPredicate> startState = Sets.newHashSet(
                new InstancedPredicate(at, father, home),
                new InstancedPredicate(at, present, postOffice),
                new InstancedPredicate(isAgent, father),
                new InstancedPredicate(isLocation, home),
                new InstancedPredicate(isLocation, postOffice),
                new InstancedPredicate(isObject, present)
        );
        Set<InstancedPredicate> goalState = Sets.newHashSet(
                new InstancedPredicate(at, father, home),
                new InstancedPredicate(wrapped, present),
                new InstancedPredicate(has, father, present)
        );
        Set<ActionScheme> actions = Sets.newHashSet(goAction, pickUpAction, wrapAction);

        return new StripsPlanningTask(actions, startState, goalState);
    }

    private void printState(Set<InstancedPredicate> state) {
        state.forEach(s ->
                System.out.println(String.format("%s: %s", s.getType().getName(), StringUtils.join(s.getSymbols().stream().map(i -> i.getName()).collect(Collectors.toList()), ",")))
        );
    }

    @Override
    public String getDescription() {
        return "STRIPS";
    }
}
