package com.midel.schedulebott.SheetAPI;

import com.midel.schedulebott.Group.Group;
import com.midel.schedulebott.Group.Schedule;
import com.midel.schedulebott.Group.Subject;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SheetValidator {
    /* ToDo якщо некоректно заповнена таблиця - повідомляти старосту
     *    - перевірка унікальності предметів
     *    - перевірка тегів
     *    - перевірка двух пустих клітинок по підгрупам в таблиці з розкладом
     *    - перевірка пустої клітинки в першої підгрупи
     *    - перевірка відповідності розкладу до назв предметів
     */
    /*
    * 1) Перевірка пустих клітинок в відповідних полях підгрупи
    *   - дві заповнених
    *       - 1 символ має дорівнювати мінусу
    *       - дві однакових попросити збити в
    *         одну для першої підгрупи і
    *         об'єднати або залишити поле для
    *         другої підгрупи пустим
    *       -
    *   - перша заповнена друга пуста
    * 2)
    * */

    public static Pair<Boolean, String> scheduleSheetValidator(Group group){
        /*
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
         */

        Schedule schedule = group.getSchedule();
        Set<String> subjectsName = schedule.getSubjectList().keySet();
        /*
        Винести перевірку не як обо'вязкову, тільки при першій загрузці та при запиті з бота, якщо помилка при зчитуванні - вирубати нахуй групу з попередженням в лс.
         */
        StringBuilder warning = new StringBuilder("Попередження: ");

        for (ArrayList<Pair<String, String>> day : schedule.getFirstWeek()){
            if (day.size() > 5){
                return new Pair<>(false, "Розклад вказаний на більше ніж 5 днів.");
            }
            for (Pair<String, String> lesson : day){
                // Дві не пусті клітинки
                if (!lesson.getValue0().equals("") && !lesson.getValue1().equals("")){
                    // Якщо перша пара заповнена одни символом і це не мінус - помилка
                    if (lesson.getValue0().length() == 1 && !lesson.getValue0().equals("-")){
                        return new Pair<>(false, "Невірно вказаний символ відсутності першої пари. Повинен бути знак \"-\" (мінус)");
                    }
                    // Якщо друга пара заповнена одним символом і це не мінус - помилка
                    if (lesson.getValue1().length() == 1 && !lesson.getValue1().equals("-")){
                        return new Pair<>(false, "Невірно вказаний символ відсутності другої пари. Повинен бути знак \"-\" (мінус)");
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(1 підгрупа) - помилка
                    if (!lesson.getValue0().equals("-") && !subjectsName.contains(lesson.getValue0())) {
                        return new Pair<>(false, "Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue0());
                    }
                    // Якщо предмети в розкладі і в списку не співпадають(2 підгрупа) - помилка
                    if (!lesson.getValue1().equals("-") && !subjectsName.contains(lesson.getValue1())) {
                        return new Pair<>(false, "Предмет в розкладі не співпадає з предметом в списку \"ПРЕДМЕТИ\". Предмет - " + lesson.getValue1());
                    }
                    if (lesson.getValue0().equals(lesson.getValue1())){
                        warning.append("Співпадіння назв для підгруп, якщо це спільна пара - об'єднайте клітинки");
                    }
                } else {
                    // Пуста тільки перша
                    if (lesson.getValue0().equals("") && !lesson.getValue1().equals("")){

                    }
                }
            }
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
