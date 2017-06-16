package org.floric.studies.dellogics.model.strips;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ActionWithSymbols {
    private ActionScheme actionScheme;
    private List<Symbol> symbols;

    public Set<InstancedPredicate> getNewState() {
        return actionScheme.apply(symbols);
    }
}
