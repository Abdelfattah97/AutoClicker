package com.mac.service;

import com.mac.model.AutoClickExecution;
import com.mac.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AutoClickExecuterService implements AutoClickExecuterObservable {

    private Future runningTask;

    private List<Observer<AutoClickExecution>> observerList;
    private boolean isWorking = false;

    public AutoClickExecuterService() {
        observerList = new ArrayList<>();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "background-worker");
        t.setDaemon(true);
        return t;
    });

    protected void startBackgroundTask(Runnable task) {
        isWorking = true;
        runningTask = executor.submit(() -> {
            try {
                task.run();
            } catch (CancellationException e) {
                // cancelled by user
            } catch (Exception ex) {
            }
            isWorking = false;
            notifyObservers(new AutoClickExecution(TaskStatus.FINISHED));
        });
    }

    protected void stopBackgroundTask() {
        if (runningTask != null && !runningTask.isDone()) {
            runningTask.cancel(true);
        }
    }

    @Override
    public void addObserver(Observer<AutoClickExecution> observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer<AutoClickExecution> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(AutoClickExecution evt) {
        for (Observer<AutoClickExecution> observer : observerList) {
            observer.update(evt);
        }
    }

    public boolean isWorking() {
        return runningTask != null && isWorking;
    }
}
