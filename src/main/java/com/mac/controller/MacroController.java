package com.mac.controller;

import com.mac.model.Macro;
import com.mac.service.MacroService;

import java.util.List;
import java.util.UUID;

/* ==========================
 * Controller Layer (thin)
 * - Methods mirror what a REST controller might expose
 * - Delegates to MacroService
 * ==========================
 */
public class MacroController {
    private final MacroService service;

    public MacroController(MacroService service) {
        this.service = service;
    }

    public boolean isSessionActive() {
        return service.isSessionActive();
    }

    public void startManualSession() {
        service.startManualSession();
    }
//
//    public void stopManualSessionAndSave(String macroName) {
//        service.stopManualSessionAndSave(macroName);
//    }

    public boolean isRecording() {
        return service.isRecording();
    }

    public void startAutoRecording() {
        service.startAutoRecording();
    }

    public void stopAutoRecording() {
        service.stopAutoRecording();
    }

    public List<Macro> listMacros() {
        return service.listMacros();
    }

    public void deleteMacro(UUID macroId) {
        service.deleteMacro(macroId);
    }

    public void playMacroCycles(UUID macroId, int cycles, int delayMs) {
        service.playMacroCycles(macroId, cycles, delayMs);
    }

    public void playMacroForTime(UUID macroId, int seconds, int delayMs) {
        service.playMacroForTime(macroId, seconds, delayMs);
    }
}

