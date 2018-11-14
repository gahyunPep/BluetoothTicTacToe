package com.chickeneater.tictactoe.core.ui;

import androidx.lifecycle.Observer;

/**
 * Created by romanlee on 11/10/18.
 * To the power of Love
 */
public abstract class EventObserver<T> implements Observer<Event<T>> {

    @Override
    public void onChanged(Event<T> tEvent) {
        if (tEvent == null) {
            return;
        }

        T content = tEvent.getContentIfNotHandled();
        if (content != null) {
            onEventHappened(content);
        }
    }

    public abstract void onEventHappened(T value);
}