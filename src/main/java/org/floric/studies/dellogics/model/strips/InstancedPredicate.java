package org.floric.studies.dellogics.model.strips;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InstancedPredicate {

    public InstancedPredicate(PredicateType type, Symbol... symbols) {
        this.type = type;
        this.symbols = Lists.newArrayList(symbols);
    }

    public InstancedPredicate(PredicateType type, List<Symbol> symbols) {
        this.type = type;
        this.symbols = symbols;
    }

    public InstancedPredicate(PredicateType type, boolean isNegated, Symbol... symbols) {
        this.type = type;
        this.symbols = Lists.newArrayList(symbols);
        this.isNegated = isNegated;
    }

    public InstancedPredicate(PredicateType type, boolean isNegated, List<Symbol> symbols) {
        this.type = type;
        this.symbols = symbols;
        this.isNegated = isNegated;
    }

    private PredicateType type;
    private List<Symbol> symbols;
    private boolean isNegated = false;
}
