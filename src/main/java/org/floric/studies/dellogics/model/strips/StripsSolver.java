package org.floric.studies.dellogics.model.strips;

import org.floric.studies.dellogics.model.Solver;

import java.util.List;
import java.util.Optional;

public class StripsSolver implements Solver {

    public Optional<List<ActionWithSymbols>> getBestSolution(StripsPlanningTask task) {
        return Optional.empty();
    }
}
