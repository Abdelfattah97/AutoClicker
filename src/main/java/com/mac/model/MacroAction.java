package com.mac.model;

/**
 * @param y     screen coordinates
 * @param delay ms after this action
 */
public record MacroAction(int x, int y, int delay, ActionType actionType) {

    @Override
    public String toString() {
        return (x >= 0 ? String.format("%s(%d,%d) wait %dms",actionType, x, y, delay) : String.format("key(%d) wait %dms", y, delay));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDelay() {
        return delay;
    }
}