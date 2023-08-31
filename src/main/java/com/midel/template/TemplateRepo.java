package com.midel.template;

import com.midel.config.DBConfig;
import com.midel.google.SheetAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TemplateRepo {
    public static final Map<String, Template> templates = new HashMap<>();
    static final Logger logger = LoggerFactory.getLogger(TemplateRepo.class);

    public static boolean importTemplateList() {
        try {
            List<List<Object>> valuesFromTemplates = SheetAPI.readSheetForRange(DBConfig.adminPanelInfoSheet, DBConfig.templatesListRange);

            if (valuesFromTemplates == null || valuesFromTemplates.isEmpty()) {
                logger.warn("No data found in \"Шаблони\"");
                templates.clear();
            } else {
                for (List<Object> rowTemplates : valuesFromTemplates) {
                    Template template = new Template(
                            rowTemplates.get(0).toString(),
                            rowTemplates.get(1).toString()
                    );

                    logger.trace("{} - {}", template.getGroupName(), template.getSheetId());

                    if (templates.containsKey(template.getGroupName())){
                        templates.get(template.getGroupName()).copy(template);
                    } else {
                        templates.put(template.getGroupName(), template);
                    }
                }
                logger.info("Successful importing of the table \"Шаблони\"");
                logger.trace("Template list: \n{}", templates);
            }


            return true;
        } catch (Exception e) {
            logger.error("Failed to import table \"Шаблони\"", e);
            return false;
        }
    }

    public static boolean exportStudentList(){

        List<List<Object>> templatesToExport = new ArrayList<>();
        for (Template template : templates.values()) {
            templatesToExport.add(template.toList());
        }
        templatesToExport.add(new ArrayList<>(Collections.nCopies((int) DBConfig.templatesEdge - 64, "")));

        if (SheetAPI.updateValues(DBConfig.adminPanelInfoSheet, DBConfig.templatesListRange, templatesToExport)){
            logger.info("Successful export of the table \"Шаблони\".");
            return true;
        } else {
            logger.error("Error when exporting table \"Шаблони\".");
            return false;
        }

    }

    public static boolean addTemplateToList(Template template) {
        try {
            if (templates.containsKey(template.getGroupName())){
                return false;
            } else {
                templates.put(template.getGroupName(), template);
                logger.info("Successfully added a template to the table \"Шаблони\".");

                if (!TemplateRepo.exportStudentList()){
                    throw new Exception();
                }

                return true;
            }
        } catch (Exception e){
            logger.error("Failed to add and update the table \"Шаблони\" = {}", template, e);
            return false;
        }
    }
}
