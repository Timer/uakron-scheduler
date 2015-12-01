package edu.uakron.cs;

import java.util.ArrayList;
import java.util.List;

public class ClassOffering {
    public enum Days {
        MONDAY("Mo"), TUESDAY("Tu"), WEDNEWDAY("We"), THURSDAY("Th"), FRIDAY("Fr"), UNKNOWN("TBA");
        public final String key;

        Days(final String key) {
            this.key = key;
        }
    }

    public static List<Days> parseDays(final String s) {
        final Days[] days = Days.values();
        final ArrayList<Days> l = new ArrayList<>(days.length);
        for (final Days day : days) {
            if (s.contains(day.key)) l.add(day);
        }
        return l;
    }

    public final String
            desc,
            subject, course,
            room, professor, students;

    public final List<Days> days;

    public ClassOffering(
            final String desc,
            final String subject, final String course,
            final String room, final String professor, final String students,
            final List<Days> days
    ) {
        this.desc = desc;

        this.subject = subject;
        this.course = course;

        this.room = room;
        this.professor = professor;
        this.students = students;

        this.days = days;
    }

    @Override
    public String toString() {
        return String.format(
                "%s [%s:%s, Days: %s, Room: %s / Instructor: %s]",
                desc, subject, course, days.toString(), room, professor
        );
    }
}
