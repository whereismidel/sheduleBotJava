package com.midel.template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateController {

    public static HashMap<String, Set<Template>> getGroupDistributionsByFaculties() {
        HashMap<String, Set<Template>> groupWithFaculty = new HashMap<>();

        for (Map.Entry<String, Template> entry : TemplateRepo.templates.entrySet()) {
            try {
                String faculty = entry.getKey().split(" ")[0];

                if (groupWithFaculty.containsKey(faculty)) {
                    groupWithFaculty.get(faculty).add(entry.getValue());
                } else {
                    HashSet<Template> templateSet = new HashSet<>();
                    templateSet.add(entry.getValue());
                    groupWithFaculty.put(faculty, templateSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return groupWithFaculty;
    }
}
