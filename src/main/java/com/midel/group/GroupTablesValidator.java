package com.midel.group;

import com.midel.type.Common;
import com.midel.type.Tuple;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class GroupTablesValidator {

    /*
    * ToDo Оновити опис в шаблоні і зразку
    *  ToDo зробити валідацію або прибрати обробку таблиць в принципі і винести в нормальну бд і надати юзерінтерфейс(не гівна)
    */

    public static final Logger logger = LoggerFactory.getLogger(GroupTablesValidator.class);

    public static boolean isEmptyScheduleSheet(Group group){
        // If the schedule does not contain any items, the value will be true

        for (Week week : group.getSchedule().getWeeks()){
            for(Day day : week.getDays()){
                for(Tuple<Subject> lesson : day.getLessons()){
                    if (lesson instanceof Common && ((Common<Subject>) lesson).get() != null && ((Common<Subject>) lesson).get().getTitleForMessage().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$")
                        || lesson instanceof com.midel.type.Pair &&  (((com.midel.type.Pair<Subject>) lesson).get(0) != null && ((com.midel.type.Pair<Subject>) lesson).get(0).getTitleForMessage().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$")
                            || ((com.midel.type.Pair<Subject>) lesson).get(1) != null && ((com.midel.type.Pair<Subject>) lesson).get(1).getTitleForMessage().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$"))){
                        return false;
                    }
                }
            }
        }

        return true;
//        for (ArrayList<Pair<String, String>> day : group.getSchedule().getFirstWeek()) {
//            for (Pair<String, String> lesson : day) {
//                if (lesson.getValue0().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$")
//                        || lesson.getValue1().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$"))
//                {
//                    isEmptySchedule = false;
//                    break;
//                }
//            }
//        }
//        for (ArrayList<Pair<String, String>> day : group.getSchedule().getSecondWeek()) {
//            for (Pair<String, String> lesson : day) {
//                if (lesson.getValue0().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$")
//                        || lesson.getValue1().matches("^[A-Za-zA-Яа-яіїґІЇҐ]+(['\\-.\\s]?[A-Za-zA-Яа-яіїґІЇҐ][.]?)*$"))
//                {
//                    isEmptySchedule = false;
//                    break;
//                }
//            }
//        }

    }
    /**
     * <p>Checking the sheet based on the completed subject table</p>
     *
     * @param group group for validation
     * @return Boolean - validity status of tables(true - valid, false - is not), ArrayList - warning message if group sheet is invalid otherwise null
     **/
    public static Pair<Boolean, ArrayList<String>> scheduleSheetValidator(Group group){

//        Schedule schedule = group.getSchedule();
//        Set<String> subjectsName = schedule.getSubjectInfo().keySet();
//
//        ArrayList<String> warning = new ArrayList<>();
//        warning.add("Виявлені помилки в таблиці з розкладом:");
//
//        // if the schedule filled in correctly, the value will be true
//        boolean isValid = true;
//
//        if (isEmptyScheduleSheet(group)){
//            warning.add("Таблиця \"РОЗКЛАД\" порожня, заповни її на основі предметів з вкладки \"ПРЕДМЕТИ\".");
//            isValid = false;
//        } else {
//            for (Week week : schedule.getWeeks()){
//                for(Day day : week.getDays()){
//                    if (week.isInvalid()) {
//                        logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 5 днів.(Перший тиждень). Group = {}", group.getGroupName());
//                        warning.add("Помилка: Розклад вказаний на більше ніж 5 днів.(Перший тиждень)");
//                        return new Pair<>(false, warning);
//                    }
//                    if (day.getNumberOfLessons() > 6) {
//                        logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 6 пар.(Перший тиждень). Group = {}", group.getGroupName());
//                        warning.add("Фатальна помилка: Розклад вказаний на більше ніж 6 пар.(Перший тиждень)");
//                        return new Pair<>(false, warning);
//                    }
//
//                    for(Tuple<Subject> lesson : day.getLessons()){
//                        if (!lesson.getValue0().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                            warning.add("I тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue0() + ")");
//                            isValid = false;
//                        }
//                        if (!lesson.getValue1().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                            warning.add("I тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue1() + ")");
//                            isValid = false;
//                        }
//                    }
//                }
//            }







//            for (ArrayList<Pair<String, String>> day : schedule.getFirstWeek()) {
//                if (schedule.getFirstWeek().size() > 5) {
//                    logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 5 днів.(Другий тиждень). Group = {}", group.getGroupName());
//                    warning.add("Фатальна помилка: Розклад вказаний на більше ніж 5 днів.(Перший тиждень)");
//                    return new Pair<>(false, warning);
//                }
//                if (day.size() > 6) {
//                    logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 6 пар.(Перший тиждень). Group = {}", group.getGroupName());
//                    warning.add("Фатальна помилка: Розклад вказаний на більше ніж 6 пар.(Перший тиждень)");
//                    return new Pair<>(false, warning);
//                }
//
//                for (Pair<String, String> lesson : day) {
//                    // Перевірка на відсутність заборонених символів
//                    if (!lesson.getValue0().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                        warning.add("I тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue0() + ")");
//                        isValid = false;
//                    }
//                    if (!lesson.getValue1().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                        warning.add("I тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue1() + ")");
//                        isValid = false;
//                    }
//
//
//                    // Дві не пусті клітинки
//                    if (!lesson.getValue0().equals("") && !lesson.getValue1().equals("")) {
//                        // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
//                        if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")) {
//                            warning.add("I тижд. - Невірно вказаний символ відсутності пари для I підгрупи. Повинен бути знак \"-\" (мінус).(" + lesson.getValue0() + ")");
//                            isValid = false;
//                        }
//                        // Якщо друга підгрупа заповнена одним символом і це не мінус - помилка
//                        if (lesson.getValue1().length() == 1 && !lesson.getValue1().equals("-")) {
//                            warning.add("I тижд. - Невірно вказаний символ відсутності пари для II підгрупи. Повинен бути знак \"-\" (мінус).(" + lesson.getValue1() + ")");
//                            isValid = false;
//                        }
//                        // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
//                        if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
//                            warning.add("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue0() + "(I підгрупа)");
//                            isValid = false;
//                        }
//                        // Якщо предмети в розкладі і в списку не співпадають(2 підгрупа) - помилка
//                        if (!lesson.getValue1().equals("-") && !subjectsName.contains(lesson.getValue1())) {
//                            warning.add("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue1() + "(II підгрупа)");
//                            isValid = false;
//                        }
//                    } else {
//                        // Перша клітинка пуста
//                        if (lesson.getValue0().equals("")) {
//                            warning.add("I тижд. - Не заповнена клітинка для першої підгрупи(відсутній \"-\" або назва предмету)");
//                            isValid = false;
//                        }
//
//                        // Пуста тільки друга
//                        if (!lesson.getValue0().equals("")) {
//                            // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
//                            if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")) {
//                                warning.add("I тижд. - Невірно вказаний символ відсутності для спільної пари. Повинен бути знак \"-\" (мінус)");
//                                isValid = false;
//                            }
//
//                            // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
//                            if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
//                                warning.add("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue0() + "(Спільна пара)");
//                                isValid = false;
//                            }
//                        }
//                    }
//                }
//            }
//
//            for (ArrayList<Pair<String, String>> day : schedule.getSecondWeek()) {
//                if (schedule.getSecondWeek().size() > 5) {
//                    logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 5 днів.(Другий тиждень). Group = {}", group.getGroupName());
//                    warning.add("Фатальна помилка: Розклад вказаний на більше ніж 5 днів.(Другий тиждень)");
//                    return new Pair<>(false, warning);
//                }
//                if (day.size() > 6) {
//                    logger.info("Перевірка розкладу: (помилка)Розклад вказаний на більше ніж 6 пар.(Другий тиждень). Group = {}", group.getGroupName());
//                    warning.add("Фатальна помилка: Розклад вказаний на більше ніж 6 пар.(Другий тиждень)");
//                    return new Pair<>(false, warning);
//                }
//
//                for (Pair<String, String> lesson : day) {
//                    // Перевірка на відсутність заборонених символів
//                    if (!lesson.getValue0().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                        warning.add("II тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue0() + ")");
//                        isValid = false;
//                    }
//                    if (!lesson.getValue1().matches("^[A-Za-zA-Яа-яіїґ\\-.'`\\s]*$")) {
//                        warning.add("II тижд. - Заборонені символи в назвах пар. Дозволені знаки: тире, крапка, апостроф (" + lesson.getValue1() + ")");
//                        isValid = false;
//                    }
//
//                    // Дві не пусті клітинки
//                    if (!lesson.getValue0().equals("") && !lesson.getValue1().equals("")) {
//                        // Якщо перша пара заповнена одни символом і це не мінус - помилка
//                        if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")) {
//                            warning.add("II тижд. - Невірно вказаний символ відсутності першої пари. Повинен бути знак \"-\" (мінус)");
//                            isValid = false;
//                        }
//                        // Якщо друга пара заповнена одним символом і це не мінус - помилка
//                        if (lesson.getValue1().length() == 1 && !lesson.getValue1().equals("-")) {
//                            warning.add("II тижд. - Невірно вказаний символ відсутності другої пари. Повинен бути знак \"-\" (мінус)");
//                            isValid = false;
//                        }
//                        // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
//                        if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
//                            warning.add("II тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue0());
//                            isValid = false;
//                        }
//                        // Якщо предмети в розкладі і в списку не співпадають(2 підгрупа) - помилка
//                        if (!lesson.getValue1().equals("-") && !subjectsName.contains(lesson.getValue1())) {
//                            warning.add("II тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue1());
//                            isValid = false;
//                        }
//                    } else {
//                        // Перша клітинка пуста
//                        if (lesson.getValue0().equals("")) {
//                            warning.add("II тижд. - Не заповнена клітинка для першої підгрупи(відсутній \"-\" або назва предмету)");
//                            isValid = false;
//                        }
//
//                        // Пуста тільки друга
//                        if (!lesson.getValue0().equals("")) {
//                            // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
//                            if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")) {
//                                warning.add("II тижд. - Невірно вказаний символ відсутності першої пари. Повинен бути знак \"-\" (мінус)");
//                                isValid = false;
//                            }
//
//                            // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
//                            if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
//                                warning.add("II тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue0() + "(I підгрупа)");
//                                isValid = false;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (isValid) {
//            logger.info("Validation of table \"РОЗКЛАД\" is successful. Group = {}", group.getGroupName());
//            return new Pair<>(true, null);
//        } else {
//            logger.info("Validation of table \"РОЗКЛАД\" failed. Group = {}", group.getGroupName());
//            System.out.println(warning);
//            return new Pair<>(false, warning);
//        }
        return new Pair<>(true, null);
    }

    /**
     * <p>Fills the table "ПРЕДМЕТИ" with unique items according to the schedule.</p>
     *
     * @param group group for generate subject list
     * @return Boolean - filling validity status(true - successful, false - fail), ArrayList - warning message if not generated otherwise null
     **/
    public static Pair<Boolean, String> generateSubjectInfoSheet(Group group){
//        try {
//
//            if (isEmptyScheduleSheet(group)){
//                return new Pair<>(false, "Таблиця \"РОЗКЛАД\" не містить жодного предмету, спочатку заповни її.");
//            }
//
//            Set<String> subjectsName = new HashSet<>();
//
//            group.getSchedule().getFirstWeek().stream().flatMap(Collection::stream).forEachOrdered(e -> {
//                subjectsName.add(e.getValue0());
//                subjectsName.add(e.getValue1());
//            });
//
//
//            group.getSchedule().getSecondWeek().stream().flatMap(Collection::stream).forEachOrdered(e -> {
//                subjectsName.add(e.getValue0());
//                subjectsName.add(e.getValue1());
//            });
//
//            subjectsName.remove("-");
//            subjectsName.remove("");
//
//            if (subjectsName.size()>0) {
//
//                List<List<Object>> subjectExport = new ArrayList<>();
//                for(String subject : subjectsName.stream().sorted().collect(Collectors.toList()))
//                    subjectExport.add(Collections.singletonList(subject));
//                for(int i = 0; i < DBConfig.subjectCount-subjectExport.size(); i++)
//                    subjectExport.add(new ArrayList<>(Collections.nCopies(8, "")));
//
//                if (!GroupRepo.exportGroupSubjects(group, subjectExport)){
//                    return new Pair<>(false, "Ймовірна помилка при генерації предметів. Перевір чи заповнились поля в вкладці \"ПРЕДМЕТИ\". \n" +
//                            "Якщо частково то зітри все до заголовка і повтори спробу.\n" +
//                            "Якщо не створилось нічого натисни ще раз кнопку перевірки.");
//                }
//            } else {
//                return new Pair<>(false, "Загальна кількість предметів менша 1.");
//            }
//
//            logger.info("Successfully generated list of subject in \"ПРЕДМЕТИ\". SheetId = {}", group.getSheetId());
//            return new Pair<>(true, "Предмети в вкладці \"ПРЕДМЕТИ\" були успішно згенеровані, заповніть цю таблицю.");
//        } catch (Exception e){
//            logger.error("Failed to generate subject list in \"ПРЕДМЕТИ\". SheetId = {}", group.getSheetId(), e);
//            return new Pair<>(false, "Помилка при генерації предметів");
//        }
        return new Pair<>(true, null);
    }

    /**
     * <p>Checking the sheet based on the completed subject info table</p>
     *
     * @param group group for validation
     * @return Boolean - validity status of tables(true - valid, false - is not), ArrayList - warning message if group sheet is invalid otherwise null
     **/
    public static Pair<Boolean, ArrayList<String>> scheduleInfoSheetValidator(Group group) {
//        ArrayList<String> warning = new ArrayList<>();
//        warning.add("Виявлені помилки в таблиці з предметами:");
//        boolean isValid = true;
//
//        try {
//            if (group.getSchedule() == null || group.getSchedule().getSubjectListWithLink().size() == 0){
//                warning.add("Таблиця \"ПРЕДМЕТИ\" не містить жодного предмету.");
//                isValid = false;
//            } else {
//                for (HashMap.Entry<String, Tuple<String>> entry : group.getSchedule().getSubjectListWithLink().entrySet()) {
//
//                    if (entry.getKey() == null){
//                        warning.add("Пропущене поле \"НАЗВА (РОЗКЛАД)\", але заповнене одне з наступних полів.");
//                        isValid = false;
//                        break;
//                    }
//
//                    Subject subject = entry.getValue();
//                    if (subject.getName() != null) {
//                        String temp = Pattern
//                                .compile("(<[ubi]>[^<>/]+</[ubi]>)+")
//                                .matcher(subject.getName())
//                                .replaceAll("")
//                                .trim();
//
//                        if (temp.contains("<") || temp.contains(">")){
//                            warning.add("Помилково вказаний тег(читайте інструкцію) - " + subject.getName());
//                            isValid = false;
//                        }
//                    } else {
//                        warning.add("Не вказана назва предмету для " + entry.getKey());
//                        isValid = false;
//                    }
//
//                    List<String> checkMarkup = Arrays.asList(
//                            subject.getNoteForFirstGroupAndGeneralLesson(),
//                            subject.getLecturerForFirstGroupAndGeneralLesson(),
//                            subject.getAuditoryForFirstGroupAndGeneralLesson(),
//                            subject.getNoteForSecondGroup(),
//                            subject.getLecturerForSecondGroup(),
//                            subject.getAuditoryForSecondGroup()
//                    );
//
//                    for (String check : checkMarkup){
//                        if (check != null) {
//                            String temp = Pattern
//                                    .compile("(<[ubi]>[^<>/]+</[ubi]>)+")
//                                    .matcher(check)
//                                    .replaceAll("")
//                                    .trim();
//
//                            if (temp.contains("<") || temp.contains(">")){
//                                warning.add("Помилково вказаний тег(читайте інструкцію) - " + check);
//                                isValid = false;
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (isValid) {
//                logger.info("Validation of table \"ПРЕДМЕТИ\" is successful. Group = {}", group.getGroupName());
//                return new Pair<>(true, null);
//            } else {
//                logger.info("Validation of table \"ПРЕДМЕТИ\" failed. Group = {}", group.getGroupName());
//                return new Pair<>(false, warning);
//            }
//        } catch (Exception e){
//            warning.add("Невідома помилка, звернись до " + ChatConfig.creatorUsername);
//            logger.error("Failed to check table \"ПРЕДМЕТИ\". SheetId = {}", group.getSheetId(), e);
//            return new Pair<>(false, warning);
//        }
        return new Pair<>(true, null);
    }
}
