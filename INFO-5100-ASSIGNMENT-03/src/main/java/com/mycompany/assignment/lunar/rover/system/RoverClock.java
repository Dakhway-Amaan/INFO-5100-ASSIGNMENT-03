package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 5 of 7: RoverClock.java
 *
 * The automatic clock. It runs in the background and, once every
 * RoverSettings.TICK_MILLIS milliseconds, tells the rover that one second has passed.
 * That is what makes the speed climb, the rover brake and the selfie timer count down
 * without you typing anything.
 */
public class RoverClock {

    private final LunarRover rover;
    private volatile boolean running = false;   // true = ticking, false = paused
    private Thread thread;

    public RoverClock(LunarRover rover) {
        this.rover = rover;
    }

    /** Starts the clock, or resumes it after pause(). */
    public void start() {
        running = true;
        if (thread != null) {
            return;                              // already created, it just resumes
        }
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(RoverSettings.TICK_MILLIS);
                    } catch (InterruptedException ex) {
                        return;
                    }
                    if (running) {
                        rover.autoTick();
                    }
                }
            }
        });
        thread.setDaemon(true);                  // does not keep the program alive
        thread.start();
    }

    /** Stops the ticking. The rover keeps its state. */
    public void pause() {
        running = false;
    }
}
