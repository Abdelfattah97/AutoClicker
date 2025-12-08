package com.mac.input.nativeintput;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.util.HashMap;
import java.util.Map;

public class NativeTranslatorResolver {

    private final static Map<Class<? extends NativeInputEvent>, NativeInputTranslator> nativeInputTranslators;

    static {
        nativeInputTranslators = new HashMap<>();
        registerTranslator(new NativeKeyTranslator());
        registerTranslator(new NativeMouseTranslator());
    }


    /**
     * Add a translator for a specific NativeInputEvent type.
     */
    public static void registerTranslator(NativeInputTranslator translator) {
        nativeInputTranslators.put(translator.getApplicableClass(), translator);
    }

    /**
     * Resolve translator by matching event class or any superclass.
     */
    public static NativeInputTranslator resolve(NativeInputEvent event) {
        Class<?> eventClass = event.getClass();

        // Direct match or superclass match
        while (eventClass != null && NativeInputEvent.class.isAssignableFrom(eventClass)) {
            NativeInputTranslator translator = nativeInputTranslators.get(eventClass);
            if (translator != null) {
                return translator;
            }
            eventClass = eventClass.getSuperclass();
        }

        throw new RuntimeException("Unable to find NativeInputTranslator for " + eventClass);
    }
}
