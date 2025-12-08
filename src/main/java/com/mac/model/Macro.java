package com.mac.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Macro {

    private final UUID id;
    private final String name;
    private final List<Action> actions;

    public Macro(String name, List<Action> actions) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.actions = List.copyOf(actions);
    }

    public String getSummary() {
        return actions.size() + " events";
    }

    public String detailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Macro: ").append(name).append("\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Events:\n");
        for (Action e : actions) sb.append("  - ").append(e.toString()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}