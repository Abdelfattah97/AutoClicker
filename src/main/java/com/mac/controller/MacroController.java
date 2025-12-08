package com.mac.controller;

import com.mac.service.AutoClickExecutorObserver;
import com.mac.recorder.MacroRecorderObserver;
import com.mac.model.Macro;
import com.mac.service.MacroService;

import java.util.List;
import java.util.UUID;

public class MacroController {
    private final MacroService service;

    public MacroController(MacroService service) {
        this.service = service;
    }

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

    public Macro getMacroById(String uuid) {
        return service.getMacroByID(uuid);
    }

    public void deleteMacro(UUID macroId) {
        service.deleteMacro(macroId);
    }

    public void playMacroCycles(UUID macroId, int cycles, int delayMs) {
        service.executeMacroCycles(macroId, cycles, delayMs);
    }

    public void playMacroForTime(UUID macroId, int seconds, int delayMs) {
        service.executeMacroForTime(macroId, seconds, delayMs);
    }

    public void subscribeForNewMacros(MacroRecorderObserver observer) {
        service.subscribeForNewMacros(observer);
    }
    public void subscribeForMacroExecution(AutoClickExecutorObserver observer) {
        service.subscribeForMacroExecution(observer);
    }

    public void stopPlay() {
        service.stopRunningMacro();
    }

    public boolean isPlaying() {
        return service.isPlaying();
    }
}

