
package com.example.newsapp.Event;

public class ChannelItemMoveEvent {
    private int fromPosition;
    private int toPosition;

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public ChannelItemMoveEvent(int fromPosition, int toPosition) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;

    }
}
