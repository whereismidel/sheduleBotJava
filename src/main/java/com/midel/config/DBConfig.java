package com.midel.config;

public class DBConfig {
    public static final String adminPanelInfoSheet = "1irsJGItfs5yU5iyPxmxJ8VpQg-V7DzML3nGE2OnyaUs"; //"1ggrj5icBrdrHhto4CeJCWFz931TOWjYBQW034_S4lCE";//
    public static final int groupsCount = 300+1;
    public static final char groupEdge = 'H';
    public static final String groupsListRange = "GroupInfo!A2:"+ groupEdge + groupsCount;

    public static final int studentsCount = 500+1;
    public static final char studentsEdge = 'C';
    public static final String studentsListRange = "StudentList!A2:"+ studentsEdge + studentsCount;

    public static final int templatesCount = 500+1;
    public static final char templatesEdge = 'B';
    public static final String templatesListRange = "GroupTemplateLink!A2:"+ studentsEdge + studentsCount;

    public static final int queuesCount = 500+1;
    public static final char queuesEdge = 'E';
    public static final String queuesListRange = "QueueInformation!A2:"+ queuesEdge + queuesCount;

    public static final String scheduleTableRange = "РОЗКЛАД!A5:S22";

    //-------------------------------------------
    public static final int subjectCount = 29+1;
    public static final char subjectEdge = 'N';
    public static final String subjectTableRange = "ПРЕДМЕТИ!A3:" + subjectEdge + subjectCount ;

    //-------------------------------------------

    public static final String templateSheet = "1FrVT7_Qk9J7UrJwOEaT3FRwD4Q9-QEd4_toh_ue1L1I";

    public static final String mainAccount = "schedule.nau.bot@gmail.com";




}
