package com.mac.service;

public interface Observer<Event> {

    void update(Event e);

}
