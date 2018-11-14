package com.chickeneater.tictactoe.core.ui;

/**
 * Created by romanlee on 11/10/18.
 * To the power of Love
 */
public class Event<T> {
    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    /**
     * Returns the content and prevents its use again.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return content;
    }
}