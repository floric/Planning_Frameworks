package org.floric.studies.dellogics.model.strips;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredicateType {
    private String name;
    private int variablesCount;
}
