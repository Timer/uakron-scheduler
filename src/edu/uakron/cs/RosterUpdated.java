package edu.uakron.cs;

import java.util.EventListener;
import java.util.Set;

public interface RosterUpdated extends EventListener {
    void updated(final Set<ClassOffering> offerings);
}
