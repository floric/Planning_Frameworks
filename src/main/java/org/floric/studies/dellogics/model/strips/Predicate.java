package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class Predicate {

    public Predicate(PredicateType type, String... variables) {
        this.type = type;
        this.variables = Lists.newArrayList(variables);
    }

    public Predicate(PredicateType type, boolean isNegated, String... variables) {
        this.type = type;
        this.isNegated = isNegated;
        this.variables = Lists.newArrayList(variables);
    }

    private PredicateType type;
    private List<String> variables;
    private boolean isNegated = false;
}
