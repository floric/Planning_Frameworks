package org.floric.studies.dellogics.main;

import com.google.common.collect.Lists;
import org.floric.studies.dellogics.model.Example;
import org.floric.studies.dellogics.model.classicalplanning.ClassicalPlanningExample;
import org.floric.studies.dellogics.model.strips.StripsExample;

import java.util.List;

public class App {

    public static void main(String[] args) {

        List<Example> examples = Lists.newArrayList(new ClassicalPlanningExample(), new StripsExample());

        examples.forEach(example -> {
            System.out.println(String.format("Run example: %s", example.getDescription()));

            example.run();

            System.out.println("Successfull finished!\n");
        });
    }
}
