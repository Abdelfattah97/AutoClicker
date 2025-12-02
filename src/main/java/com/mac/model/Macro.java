package com.mac.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Macro {

    private final UUID id;
    private final String name;
    private final List<MacroAction> actions;

    public Macro(String name, List<MacroAction> actions) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.actions = Collections.unmodifiableList(new ArrayList<>(actions));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MacroAction> getActions() {
        return actions;
    }

    public String getSummary() {
        return actions.size() + " actions";
    }

    public String detailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Macro: ").append(name).append("\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Actions:\n");
        for (MacroAction a : actions) sb.append("  - ").append(a.toString()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}