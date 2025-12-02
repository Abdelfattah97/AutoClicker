package com.mac.service;

public interface Observable<Event> {

    void addObserver(Observer<Event> observer);
    void removeObserver(Observer<Event> observer);
    void notifyObservers(Event evt);

}
