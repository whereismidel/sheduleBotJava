package com.midel.schedulebott.google;

import com.midel.schedulebott.group.Group;
import com.midel.schedulebott.group.Schedule;
import com.midel.schedulebott.group.Subject;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetValidator {
    /* ToDo якщо некоректно заповнена таблиця - повідомляти старосту
     *    - перевірка унікальності предметів
     *    - перевірка тегів
     *    - перевірка двух пустих клітинок по підгрупам в таблиці з розкладом
     *    - перевірка пустої клітинки в першої підгрупи
     *    - перевірка відповідності розкладу до назв предметів
     */

    /*
    * ToDo Заповнювати спочатку розклад унікальнимим значеннями, потім в таблиці предмети генерувати поля для заповнення.
    * */

    public static Pair<Boolean, String> scheduleSheetValidator(Group group){

        Schedule schedule = group.getSchedule();
        Set<String> subjectsName = schedule.getSubjectList().keySet();
        /*
        Винести перевірку не як обо'вязкову, тільки при першій загрузці та при запиті з бота, якщо помилка при зчитуванні - вирубати нахуй групу з попередженням в лс.
         */
        StringBuilder warning = new StringBuilder("Виявлені помилки в таблиці з розкладом:\n");
        boolean isValid = true;

        for (ArrayList<Pair<String, String>> day : schedule.getFirstWeek()){
            if (schedule.getFirstWeek().size() > 5){
                return new Pair<>(false, "Фатальна помилка: Розклад вказаний на більше ніж 5 днів.");
            }
            if (day.size() > 6){
                return new Pair<>(false, "Фатальна помилка: Розклад вказаний на більше ніж 6 пар.");
            }
            for (Pair<String, String> lesson : day){
                if (lesson.getValue0().matches("[<>/].*") || lesson.getValue1().matches("[<>/].*")){
                    warning.append("I тижд. - Некоректні символи в назвах пар. Заборонені '<', '>', '/'\n");
                    isValid = false;
                }
                // Дві не пусті клітинки
                if (!lesson.getValue0().equals("") && !lesson.getValue1().equals("")){
                    // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
                    if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")){
                        warning.append("I тижд. - Невірно вказаний символ відсутності пари для I підгрупи. Повинен бути знак \"-\" (мінус)\n");
                        isValid = false;
                    }
                    // Якщо друга підгрупа заповнена одним символом і це не мінус - помилка
                    if (lesson.getValue1().length() == 1 && !lesson.getValue1().equals("-")){
                        warning.append("I тижд. - Невірно вказаний символ відсутності пари для II підгрупи. Повинен бути знак \"-\" (мінус)\n");
                        isValid = false;
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
                    if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
                        warning.append("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue0()).append("(I підгрупа)\n");
                        isValid = false;
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(2 підгрупа) - помилка
                    if (!lesson.getValue1().equals("-") && !subjectsName.contains(lesson.getValue1())) {
                        warning.append("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue1()).append("(II підгрупа)\n");
                        isValid = false;
                    }
                } else {
                    // Перша клітинка пуста
                    if (lesson.getValue0().equals("")){
                        warning.append("I тижд. - Не заповнена клітинка для першої підгрупи(відсутній \"-\" або назва предмету)\n");
                        isValid = false;
                    }

                    // Пуста тільки друга
                    if (!lesson.getValue0().equals("") && lesson.getValue1().equals("")){
                        // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
                        if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")){
                            warning.append("I тижд. - Невірно вказаний символ відсутності пари для I підгрупи. Повинен бути знак \"-\" (мінус)\n");
                            isValid = false;
                        }

                        // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
                        if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
                            warning.append("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue0()).append("(I підгрупа)\n");
                            isValid = false;
                        }
                    }
                }
            }
        }

        for (ArrayList<Pair<String, String>> day : schedule.getSecondWeek()){
            if (schedule.getSecondWeek().size() > 5){
                return new Pair<>(false, "Фатальна помилка: Розклад вказаний на більше ніж 5 днів.");
            }
            if (day.size() > 6){
                return new Pair<>(false, "Фатальна помилка: Розклад вказаний на більше ніж 6 пар.");
            }
            for (Pair<String, String> lesson : day){
                // Дві не пусті клітинки
                if (!lesson.getValue0().equals("") && !lesson.getValue1().equals("")){
                    // Якщо перша пара заповнена одни символом і це не мінус - помилка
                    if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")){
                        warning.append("II тижд. - Невірно вказаний символ відсутності першої пари. Повинен бути знак \"-\" (мінус)\n");
                        isValid = false;
                    }
                    // Якщо друга пара заповнена одним символом і це не мінус - помилка
                    if (lesson.getValue1().length() == 1 && !lesson.getValue1().equals("-")){
                        warning.append("II тижд. - Невірно вказаний символ відсутності другої пари. Повинен бути знак \"-\" (мінус)\n");
                        isValid = false;
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
                    if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
                        warning.append("II тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue0()).append("\n");
                        isValid = false;
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(2 підгрупа) - помилка
                    if (!lesson.getValue1().equals("-") && !subjectsName.contains(lesson.getValue1())) {
                        warning.append("II тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue1()).append("\n");
                        isValid = false;
                    }
                } else {
                    // Перша клітинка пуста
                    if (lesson.getValue0().equals("")){
                        warning.append("II тижд. - Не заповнена клітинка для першої підгрупи(відсутній \"-\" або назва предмету)\n");
                        isValid = false;
                    }

                    // Пуста тільки друга
                    if (!lesson.getValue0().equals("") && lesson.getValue1().equals("")){
                        // Якщо перша підгрупа заповнена одним символом і це не мінус - помилка
                        if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")){
                            warning.append("II тижд. - Невірно вказаний символ відсутності першої пари. Повинен бути знак \"-\" (мінус)\n");
                            isValid = false;
                        }

                        // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
                        if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
                            warning.append("I тижд. - Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - ").append(lesson.getValue0()).append("(I підгрупа)\n");
                            isValid = false;
                        }
                    }
                }
            }
        }
        if (!isValid) {
            return new Pair<>(false, warning.toString());
        }
        return new Pair<>(true, "OK");
    }

    /*

    */
    public static Pair<Boolean, String> scheduleInfoSheetValidator(Group group){

        ArrayList<String> htmlSeq = new ArrayList<>(
                                           Arrays.asList(
                                           "<u>", "</u>",
                                           "<b>", "</b>",
                                           "<i>", "</i>"));

        StringBuilder warning = new StringBuilder("Виявлені помилки в таблиці з предметами:\n");
        boolean isValid = true;

        for(HashMap.Entry<String, Subject> entry : group.getSchedule().getSubjectList().entrySet()) {
            Subject subject = entry.getValue();
            Matcher m = Pattern.compile("<(.+?)>").matcher(subject.getName());
            boolean error = false;

            if (subject.getName() != null) {
                while (m.find()) {
                    if (!htmlSeq.contains(subject.getName().substring(m.start(), m.end()))) {
                        error = true;
                        break;
                    }
                }
                if (error) {
                    warning.append("Помилково вказаний тег(читайте інструкцію) - ").append(subject.getName()).append("\n");
                    isValid = false;
                }
            } else {
                warning.append("Не вказана назва предмету \n");
                isValid = false;
            }

            if (subject.getNoteForFirstGroupAndGeneralLesson() != null) {
                m = Pattern.compile("<(.+?)>").matcher(subject.getNoteForFirstGroupAndGeneralLesson());
                error = false;
                while (m.find()) {
                    if (!htmlSeq.contains(subject.getNoteForFirstGroupAndGeneralLesson().substring(m.start(), m.end()))) {
                        error = true;
                        break;
                    }
                }
                if (error) {
                    warning.append("Помилково вказаний тег(читайте інструкцію) - ").append(subject.getNoteForFirstGroupAndGeneralLesson()).append("\n");
                    isValid = false;
                }
            }

            if (subject.getNoteForSecondGroup() != null) {
                m = Pattern.compile("<(.+?)>").matcher(subject.getNoteForSecondGroup());
                error = false;
                while (m.find()) {
                    if (!htmlSeq.contains(subject.getNoteForSecondGroup().substring(m.start(), m.end()))) {
                        error = true;
                        break;
                    }
                }
                if (error) {
                    warning.append("Помилково вказаний тег(читайте інструкцію) - ").append(subject.getNoteForSecondGroup()).append("\n");
                    isValid = false;
                }
            }
        }

        if (!isValid){
            return new Pair<>(false, warning.toString());
        }
        return new Pair<>(true, "OK");
    }
}

/*
Group{
groupName='ПІ-124Б(А)',
leaderId='787943933',
channelId='-1001852235048',
sheetId='1gfCrqRJFGgVK4WDT2GdnkOqiLp7V8giUD4WaYpsHuiA',
schedule=Schedule{
    subjectList={
        ЛР Основи програмування=Subject{name='<u>ЛАБАРАТОРНА</u> Основи програмування', linkForFirstGroupAndGeneralLesson='https://meet.google.com/vvx-tqfm-sgo', linkForSecondGroup='https://meet.google.com/vvx-tqfm-sgo', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛК Основи інженерії програмного забезпечення=Subject{name='<u>ЛЕКЦІЯ</u> Основи інженерії програмного забезпечення', linkForFirstGroupAndGeneralLesson='https://meet.google.com/vsq-nobn-xyj', linkForSecondGroup='https://meet.google.com/vsq-nobn-xyj', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛК Історія української державності та культури=Subject{name='<u>ЛЕКЦІЯ</u> Історія української державності та культури', linkForFirstGroupAndGeneralLesson='https://meet.google.com/hzt-kfwn-efi', linkForSecondGroup='https://meet.google.com/hzt-kfwn-efi', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ПР Основи програмування=Subject{name='<u>ПРАКТИКА</u> Основи програмування', linkForFirstGroupAndGeneralLesson='https://meet.google.com/vvx-tqfm-sgo', linkForSecondGroup='https://meet.google.com/vvx-tqfm-sgo', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ПР Математичний аналіз=Subject{name='<u>ПРАКТИКА</u> Математичний аналіз', linkForFirstGroupAndGeneralLesson='https://meet.google.com/gwj-vwmp-zko', linkForSecondGroup='https://meet.google.com/gwj-vwmp-zko', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛК Основи програмування=Subject{name='<u>ЛЕКЦІЯ</u> Основи програмування', linkForFirstGroupAndGeneralLesson='https://meet.google.com/vvx-tqfm-sgo', linkForSecondGroup='https://meet.google.com/vvx-tqfm-sgo', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛК Математичний аналіз=Subject{name='<u>ЛЕКЦІЯ</u> Математичний аналіз', linkForFirstGroupAndGeneralLesson='https://meet.google.com/gwj-vwmp-zko', linkForSecondGroup='https://meet.google.com/gwj-vwmp-zko', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ПР Фахова іноземна мова=Subject{name='<u>ПРАКТИКА</u> Фахова іноземна мова', linkForFirstGroupAndGeneralLesson='https://meet.google.com/nhq-ismw-bpj', linkForSecondGroup='https://meet.google.com/nhq-ismw-bpj', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ПР Історія української державності та культури=Subject{name='<u>ПРАКТИКА</u> Історія української державності та культури', linkForFirstGroupAndGeneralLesson='https://meet.google.com/hzt-kfwn-efi', linkForSecondGroup='https://meet.google.com/hzt-kfwn-efi', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛР Основи інженерії програмного забезпечення=Subject{name='<u>ЛАБАРАТОРНА</u> Основи інженерії програмного забезпечення', linkForFirstGroupAndGeneralLesson='https://meet.google.com/vsq-nobn-xyj', linkForSecondGroup='https://meet.google.com/vsq-nobn-xyj', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ЛК Комп'ютерна дискретна математика=Subject{name='<u>ЛЕКЦІЯ</u> Комп'ютерна дискретна математика', linkForFirstGroupAndGeneralLesson='https://meet.google.com/xwx-ffsb-iqp', linkForSecondGroup='https://meet.google.com/xwx-ffsb-iqp', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'},
        ПР Комп'ютерна дискретна математика=Subject{name='<u>ПРАКТИКА</u> Комп'ютерна дискретна математика', linkForFirstGroupAndGeneralLesson='https://meet.google.com/xwx-ffsb-iqp', linkForSecondGroup='https://meet.google.com/xwx-ffsb-iqp', noteForFirstGroupAndGeneralLesson='null', noteForSecondGroup='null'}
    },
    subjectListTable=[
        [ЛК Основи програмування, <u>ЛЕКЦІЯ</u> Основи програмування, https://meet.google.com/vvx-tqfm-sgo, , , , , ],
        [ЛК Математичний аналіз, <u>ЛЕКЦІЯ</u> Математичний аналіз, https://meet.google.com/gwj-vwmp-zko, , , , , ],
        [ЛК Комп'ютерна дискретна математика, <u>ЛЕКЦІЯ</u> Комп'ютерна дискретна математика, https://meet.google.com/xwx-ffsb-iqp, , , , , ],
        [ЛК Основи інженерії програмного забезпечення, <u>ЛЕКЦІЯ</u> Основи інженерії програмного забезпечення, https://meet.google.com/vsq-nobn-xyj, , , , , ],
        [ЛК Історія української державності та культури, <u>ЛЕКЦІЯ</u> Історія української державності та культури, https://meet.google.com/hzt-kfwn-efi, , , , , ],
        [ПР Математичний аналіз, <u>ПРАКТИКА</u> Математичний аналіз, https://meet.google.com/gwj-vwmp-zko, , , , , ],
        [ПР Фахова іноземна мова, <u>ПРАКТИКА</u> Фахова іноземна мова, https://meet.google.com/nhq-ismw-bpj, , , , , ],
        [ПР Основи програмування, <u>ПРАКТИКА</u> Основи програмування, https://meet.google.com/vvx-tqfm-sgo, , , , , ],
        [ПР Історія української державності та культури, <u>ПРАКТИКА</u> Історія української державності та культури, https://meet.google.com/hzt-kfwn-efi, , , , , ],
        [ПР Комп'ютерна дискретна математика        , <u>ПРАКТИКА</u> Комп'ютерна дискретна математика, https://meet.google.com/xwx-ffsb-iqp, , , , , ],
        [ЛР Основи програмування, <u>ЛАБАРАТОРНА</u> Основи програмування, https://meet.google.com/vvx-tqfm-sgo, , , , , ],
        [ЛР Основи інженерії програмного забезпечення, <u>ЛАБАРАТОРНА</u> Основи інженерії програмного забезпечення, https://meet.google.com/vsq-nobn-xyj, , , , , ]
    ],
    firstWeek=[
        [[ЛК Основи програмування, ], [ЛК Математичний аналіз, ], [ПР Математичний аналіз, ], [-, -], [-, -], [-, -]],
        [[-, -], [ПР Фахова іноземна мова, ], [ПР Основи програмування, ], [-, -], [-, -], [-, -]],
        [[-, -], [ЛР Основи програмування, ], [ЛК Комп'ютерна дискретна математика, ], [-, -], [-, -], [-, -]],
        [[ЛК Основи інженерії програмного забезпечення, ], [ПР Математичний аналіз, ], [ЛР Основи інженерії програмного забезпечення, ], [-, -], [-, -], [-, -]],
        [[ЛК Історія української державності та культури, ], [ПР Комп'ютерна дискретна математика, ], [ЛР Основи інженерії програмного забезпечення, ], [-, -], [-, -], [-, -]]
    ],
    secondWeek=[
        [[ЛК Математичний аналіз, ], [ЛК Основи програмування, ], [ПР Математичний аналіз, ], [-, -], [-, -], [-, -]],
        [[ПР Основи програмування, ], [ПР Фахова іноземна мова, ], [-, -], [-, -], [-, -], [-, -]],
        [[ЛР Основи програмування, ], [ПР Історія української державності та культури, ], [ЛК Комп'ютерна дискретна математика, ], [-, -], [-, -], [-, -]],
        [[ЛК Основи інженерії програмного забезпечення, ], [ПР Математичний аналіз, ], [ПР Комп'ютерна дискретна математика, ], [-, -], [-, -], [-, -]],
        [[ЛР Основи інженерії програмного забезпечення, ], [ПР Комп'ютерна дискретна математика, ], [-, -], [-, -], [-, -], [-, -]]
    ]
}}

 */
