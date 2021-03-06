package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Sets;
import org.floric.studies.dellogics.model.Example;

import java.lang.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StripsExample implements Example {

    @Override
    public void run() {
        StripsPlanningTask task = createTask();

        StripsSolver solver = new StripsSolver();
        Optional<List<ActionApplication>> solution = solver.getBestSolution(task);

        solution.ifPresent(StripsSolver::printSolution);
    }

    public StripsPlanningTask createTask() {
        Symbol father = new Symbol("Father");
        Symbol present = new Symbol("Present");
        Symbol postOffice = new Symbol("Postoffice");
        Symbol home = new Symbol("Home");

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
                Sets.newHashSet(new Predicate(at, "agt", "to"), new Predicate(at, false, "agt", "from"))
        );

        ActionScheme pickUpAction = new ActionScheme(
                new Predicate(pickUp, "agt", "obj", "from"),
                Sets.newHashSet(new Predicate(at, "agt", "from"), new Predicate(at, "obj", "from"), new Predicate(has, false, "agt", "obj"), new Predicate(isAgent, "agt"), new Predicate(isObject, "obj"), new Predicate(isLocation, "from")),
                Sets.newHashSet(new Predicate(has, "agt", "obj"), new Predicate(at, false, "obj", "from"))
        );

        ActionScheme wrapAction = new ActionScheme(
                new Predicate(wrap, "agt", "obj"),
                Sets.newHashSet(new Predicate(has, "agt", "obj"), new Predicate(wrapped, false,"obj"), new Predicate(isAgent, "agt"), new Predicate(isObject, "obj")),
                Sets.newHashSet(new Predicate(wrapped, "obj"))
        );

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

    @Override
    public String getDescription() {
        return "STRIPS";
    }
}
