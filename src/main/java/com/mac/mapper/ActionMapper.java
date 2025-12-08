package com.mac.mapper;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.mac.input.nativeintput.NativeTranslatorResolver;
import com.mac.model.Action;

/**
 * Creates an Action
 */
public class ActionMapper {

    public static Action toAction(NativeInputEvent event, int delay) {
        return Action.builder()
                .event(NativeTranslatorResolver.resolve(event).toAwtEvent(event))
                .delay(delay)
                .originalEvt(event)
                .build();
    }

}
