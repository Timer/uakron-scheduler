package edu.uakron.cs;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClassOffering {
    public enum Days {
        MONDAY("Mo"), TUESDAY("Tu"), WEDNESDAY("We"), THURSDAY("Th"), FRIDAY("Fr"), UNKNOWN("TBA");
        public final String key;

        Days(final String key) {
            this.key = key;
        }
    }

    public static class DayPair {
        public final Days day;
        public LocalTime start, end;
        public Duration duration;

        public DayPair(final Days d) {
            this.day = d;
        }

        @Override
        public String toString() {
            return String.format("%s (%s-%s)", day, start, end);
        }
    }

    private static LocalTime getFrom(String str) {
        str = str.replaceAll("12[:]", "0:");
        final String[] parts = str.split("[:]");
        final int off = parts[1].contains("P") ? 12 : 0;
        try {
            return LocalTime.of(Integer.parseInt(parts[0].trim()) + off, Integer.parseInt(parts[1].substring(0, 2)));
        } catch (final Exception ignored) {
            return null;
        }
    }

    public static List<DayPair> parseDays(final String s) {
        final Days[] days = Days.values();
        final ArrayList<DayPair> l = new ArrayList<>(days.length);
        for (final Days day : days) {
            if (s.contains(day.key)) l.add(new DayPair(day));
        }
        final String[] parts = s.split("[-]");
        final String[] t = parts[0].split("\\s");
        parts[0] = t[t.length - 1];
        if (s.contains("TBA")) return l;
        for (final DayPair p : l) {
            p.start = getFrom(parts[0]);
            p.end = getFrom(parts[1]);
            p.duration = Duration.between(p.start, p.end);
        }
        return l;
    }

    public final String
            classNbr, section,
            desc,
            subject, course,
            room, professor, students;
    public final List<DayPair> days;
    public final boolean open;
    public final int units;

    public ClassOffering(
            final String classNbr, final String section,
            final String desc,
            final String subject, final String course,
            final String room, final String professor, final String students,
            final List<DayPair> days,
            final boolean open,
            final int units
    ) {
        this.classNbr = classNbr;
        this.section = section;

        this.desc = desc;

        this.subject = subject;
        this.course = course;

        this.room = room;
        this.professor = professor;
        this.students = students;

        this.days = days;

        this.open = open;

        this.units = units;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s) [%s:%s (%s), Days: %s, Room: %s / Instructor: %s, Open: %s [%s students], Units: %s]",
                desc, section, subject, course, classNbr, days.toString(), room, professor, Boolean.toString(open), students, Integer.toString(units)
        );
    }

    @Override
    public int hashCode() {
        return classNbr.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof ClassOffering && ((ClassOffering) o).classNbr.equals(classNbr);
    }
}
