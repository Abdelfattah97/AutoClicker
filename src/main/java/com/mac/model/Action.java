package com.mac.model;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.mac.input.AwtKeyMapper;
import lombok.Builder;
import lombok.Getter;

import java.awt.event.InputEvent;


@Builder
public record Action(@Getter int delay, @Getter InputEvent event , NativeInputEvent originalEvt) {

    public String toString() {
        // Use the AwtKeyMapper to generate the complete, friendly description of the event.
        // This leverages the logic that handles Key/Mouse/Wheel events and modifiers.
        String eventDetails = AwtKeyMapper.describe(event);
        var eventSource = AwtKeyMapper.getSource(event);
        // Combine the event details with the delay
        return String.format("[%s] %s | Delay: %dms", eventSource.name(), eventDetails, delay);
    }
}