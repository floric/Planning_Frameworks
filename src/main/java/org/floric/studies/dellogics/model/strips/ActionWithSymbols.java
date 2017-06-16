package org.floric.studies.dellogics.model.strips;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActionWithSymbols {
    private ActionScheme actionScheme;
    private List<Symbol> symbols;
}
