package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 3 of 7: RoverSettings.java
 *
 * All the numbers used by the rover in one place. Change a value here and the
 * whole program follows.
 */
public final class RoverSettings {

    private RoverSettings() {
        // only constants, nothing to create
    }

    // ---- speed
    public static final double MAX_FORWARD_SPEED = 12.0;   // km/h, from the assignment (M11)
    public static final double MAX_REVERSE_SPEED = 6.0;    // km/h, rMax (our assumption)
    public static final double SPEED_STEP = 1.0;           // km/h gained or lost per second (a)

    // ---- pedal gestures, in seconds
    public static final double PRESS_LIMIT = 1.0;          // released under this = a tap
    public static final double DRIVE_HOLD = 3.0;           // M4, M6: hold more than 3 s
    public static final double COLOR_CAMERA_HOLD = 5.0;    // C2: hold 5 s to under 10 s
    public static final double CAMERA_16MM_HOLD = 10.0;    // C3: hold 10 s or more
    public static final double TIMER_HOLD = 5.0;           // C6: hold 5 s or more

    // ---- time
    public static final double SELFIE_DELAY = 10.0;        // seconds the selfie timer counts down
    public static final int TICK_MILLIS = 1000;            // real time between automatic ticks (smaller = faster)
}
