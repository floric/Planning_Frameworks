package org.floric.studies.dellogics.model.strips.exceptions;

public class NoNegPredicatesAllowedException extends RuntimeException {
    public NoNegPredicatesAllowedException() {
        super("No negative states are allowed in state." +
                "Negative conditions should only be used for preconditions and effects in action schemes.");
    }
}
