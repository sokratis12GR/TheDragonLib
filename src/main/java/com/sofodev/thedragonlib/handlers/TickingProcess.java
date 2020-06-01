package com.sofodev.thedragonlib.handlers;

public class TickingProcess implements IProcess {
    public int tick = 0;

    @Override
    public void updateProcess() {
        tick++;
    }

    @Override
    public boolean isDead() {
        return false;
    }
}