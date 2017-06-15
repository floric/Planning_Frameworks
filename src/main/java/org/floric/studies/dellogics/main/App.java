package org.floric.studies.dellogics.main;

import com.google.common.collect.Lists;
import org.floric.studies.dellogics.model.Example;
import org.floric.studies.dellogics.model.classicalplanning.ClassicalPlanningExample;

import java.util.List;

public class App {

    public static void main(String[] args) {

        List<Example> examples = Lists.newArrayList(new ClassicalPlanningExample());

        examples.forEach(example -> {
            System.out.println(String.format("Run example: %s", example.getDescription()));

            example.run();
        });
    }
}
