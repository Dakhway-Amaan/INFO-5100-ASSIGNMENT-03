package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 4 of 7: LunarRover.java  (the state machine itself)
 *
 * This is the heart of the program. It follows the UML diagram:
 *
 *  - The variable "state" always holds the innermost state (see State.java).
 *  - The composite states (On, Drive, Forward, Equipment, the cameras, Drill) are the
 *    helper methods isEquipment(), isCamera(), isDrill() and so on. A transition that
 *    belongs to a composite state is written once and applies to all of its substates,
 *    for example POWER OFF (M2) works from every state, and MODE (C1) works from every
 *    Equipment state.
 *  - Transition IDs (M1..M12, C1..C12) match the diagram and the specification.
 *  - Pedal gestures are reported when the pedal is released, with the seconds it was
 *    held: under 1 second is a "press", otherwise it is a "hold" and the guards decide
 *    what it means.
 *  - tick() is one second of time. It runs the do-activities (speed changing, the selfie
 *    countdown) and the time based transitions (M9 and C7).
 *
 * Other files: RoverSettings.java (numbers), RoverClock.java (calls tick by itself),
 * RoverGuide.java (the help text and map).
 */
public class LunarRover {

    // ------------------------------------------------------------- variables
    private State state = State.OFF;                // the rover starts in Off
    private double speed = 0;
    private double reverseSpeed = 0;
    private double timerRemaining = 0;
    private int pictures = 0;
    private int clock = 0;                          // seconds counted since the rover object was created
    private final boolean verbose;

    public LunarRover(boolean verbose) {
        this.verbose = verbose;
    }

    // ------------------------------------------------ composite state helpers
    private boolean isColorCamera() {
        return state == State.COLOR_READY || state == State.COLOR_TIMER;
    }

    private boolean isCamera() {
        return isColorCamera() || state == State.CAM16_READY || state == State.CAM16_TIMER;
    }

    private boolean isTimer() {
        return state == State.COLOR_TIMER || state == State.CAM16_TIMER;
    }

    private boolean isDrill() {
        return state == State.DRILL_OFF || state == State.DRILL_ON;
    }

    private boolean isEquipment() {
        return state == State.IDLE || isCamera() || isDrill();
    }

    // ----------------------------------------------------------------- inputs

    /** Button 1. */
    public synchronized void powerButton() {
        if (state == State.OFF) {
            go("M1", State.STOPPED);                // On > Drive > Stopped
            enterStopped();
        } else {
            leaveEquipment();                       // exit action of Equipment
            speed = 0;                              // M2 / speed = 0; drillOff(); cancelTimer()
            go("M2", State.OFF);
        }
    }

    /** Button 2 (mode switch). */
    public synchronized void modeButton() {
        if (state == State.STOPPED) {
            go("M12", State.IDLE);                  // enter Equipment mode
        } else if (isEquipment()) {
            leaveEquipment();                       // C1 / drillOff(); cancelTimer()
            go("C1", State.STOPPED);
            enterStopped();
        } else {
            say("mode switch ignored in " + state);  // M14: only works when stopped
        }
    }

    /** Left pedal released after the given number of seconds. */
    public synchronized void leftReleased(double seconds) {
        boolean pressed = seconds < RoverSettings.PRESS_LIMIT;

        switch (state) {
            case STOPPED:
                if (pressed) {
                    go("M3", State.ACCELERATING);
                } else if (seconds > RoverSettings.DRIVE_HOLD) {
                    go("M4", State.REVERSING);
                    reverseSpeed = 0;               // entry / reverseSpeed = 0
                } else {
                    ignored("left pedal");
                }
                break;

            case CRUISING:
            case DECELERATING:
                if (pressed) {
                    go("M8", State.ACCELERATING);
                } else {
                    ignored("left pedal");
                }
                break;

            case IDLE:
                if (seconds >= RoverSettings.CAMERA_16MM_HOLD) {
                    go("C3", State.CAM16_READY);
                } else if (seconds >= RoverSettings.COLOR_CAMERA_HOLD) {
                    go("C2", State.COLOR_READY);
                } else {
                    ignored("left pedal");
                }
                break;

            case COLOR_READY:
            case CAM16_READY:
                if (pressed) {
                    takePicture();                  // C5 (internal, stays in Ready)
                } else if (seconds >= RoverSettings.TIMER_HOLD) {
                    timerRemaining = RoverSettings.SELFIE_DELAY;  // C6 / startTimer()
                    say("startTimer()");
                    go("C6", isColorCamera() ? State.COLOR_TIMER : State.CAM16_TIMER);
                } else {
                    ignored("left pedal");
                }
                break;

            case DRILL_OFF:
                if (pressed) {
                    say("drillOn()");               // C10
                    go("C10", State.DRILL_ON);
                } else {
                    ignored("left pedal");
                }
                break;

            case DRILL_ON:
                if (pressed) {
                    say("drillOff()");              // C10
                    go("C10", State.DRILL_OFF);
                } else {
                    ignored("left pedal");
                }
                break;

            default:
                ignored("left pedal");              // OFF, ACCELERATING, REVERSING, TIMER states
        }
    }

    /** Right pedal released after the given number of seconds. */
    public synchronized void rightReleased(double seconds) {
        boolean pressed = seconds < RoverSettings.PRESS_LIMIT;

        switch (state) {
            case ACCELERATING:
                if (pressed) {
                    go("M5", State.DECELERATING);
                } else if (seconds > RoverSettings.DRIVE_HOLD) {
                    go("M6", State.CRUISING);
                } else {
                    ignored("right pedal");
                }
                break;

            case CRUISING:
                if (pressed) {
                    go("M7", State.DECELERATING);
                } else {
                    ignored("right pedal");
                }
                break;

            case REVERSING:
                if (pressed) {
                    go("M10", State.STOPPED);
                    enterStopped();
                } else {
                    ignored("right pedal");
                }
                break;

            // R inside a Timer state is handled by the Timer state first (C9),
            // so it does not reach the Camera state's own transition (C8).
            case COLOR_TIMER:
            case CAM16_TIMER:
                if (pressed) {
                    say("cancelTimer()");           // C9
                    go("C9", isColorCamera() ? State.COLOR_READY : State.CAM16_READY);
                } else {
                    ignored("right pedal");
                }
                break;

            case COLOR_READY:
            case CAM16_READY:
                if (pressed) {
                    go("C8", State.IDLE);           // C8: from the Camera state
                } else {
                    ignored("right pedal");
                }
                break;

            case DRILL_OFF:
                if (pressed) {
                    go("C11", State.IDLE);          // C11: from the Drill state
                } else {
                    ignored("right pedal");
                }
                break;

            case DRILL_ON:
                if (pressed) {
                    say("drillOff()");              // C12
                    go("C12", State.IDLE);
                } else {
                    ignored("right pedal");
                }
                break;

            default:
                ignored("right pedal");             // OFF, STOPPED, DECELERATING, IDLE (C13)
        }
    }

    /** Left pedal pressed twice within 1 second. */
    public synchronized void leftPressedTwice() {
        if (state == State.IDLE) {
            go("C4", State.DRILL_OFF);
        } else {
            ignored("double left press");
        }
    }

    /** One second passes: runs the do-activities and the time based transitions. */
    public synchronized void tick() {
        clock++;
        switch (state) {
            case ACCELERATING:                      // do / speed = min(speed + a, 12)   (M11)
                speed = Math.min(speed + RoverSettings.SPEED_STEP, RoverSettings.MAX_FORWARD_SPEED);
                break;

            case DECELERATING:                      // do / speed = max(speed - a, 0)
                speed = Math.max(speed - RoverSettings.SPEED_STEP, 0);
                if (speed == 0) {                   // when (speed == 0)   (M9)
                    go("M9", State.STOPPED);
                    enterStopped();
                }
                break;

            case REVERSING:                         // do / reverseSpeed = min(reverseSpeed + a, rMax)
                reverseSpeed = Math.min(reverseSpeed + RoverSettings.SPEED_STEP, RoverSettings.MAX_REVERSE_SPEED);
                break;

            case COLOR_TIMER:
            case CAM16_TIMER:
                timerRemaining = timerRemaining - 1;
                if (timerRemaining <= 0) {          // after (10 s) / takePicture()   (C7)
                    takePicture();
                    go("C7", isColorCamera() ? State.COLOR_READY : State.CAM16_READY);
                }
                break;

            default:
                break;
        }
    }

    public synchronized void tick(int seconds) {
        for (int i = 0; i < seconds; i++) {
            tick();
        }
    }

    // ------------------------------------------------------- automatic clock

    /** One automatic tick, called by RoverClock. Prints the status line when something changed. */
    public synchronized void autoTick() {
        State oldState = state;
        double oldSpeed = speed;
        double oldReverse = reverseSpeed;
        tick();
        boolean changed = state != oldState || speed != oldSpeed
                || reverseSpeed != oldReverse || isTimer();
        if (verbose && changed) {
            System.out.println();
            System.out.println("[" + clock + " s] " + status());
            System.out.print("> ");
        }
    }

    // ---------------------------------------------------------------- actions

    /** entry / speed = 0; reverseSpeed = 0 */
    private void enterStopped() {
        speed = 0;
        reverseSpeed = 0;
    }

    /** exit / drillOff(); cancelTimer()  (exit action of the Equipment state) */
    private void leaveEquipment() {
        if (state == State.DRILL_ON) {
            say("drillOff()");
        }
        if (isTimer()) {
            say("cancelTimer()");
        }
    }

    private void takePicture() {
        pictures++;
        say("takePicture() with the " + (isColorCamera() ? "color camera" : "16-mm camera"));
    }

    private void go(String id, State next) {
        say(id + ": " + state + " -> " + next);
        state = next;
    }

    private void ignored(String what) {
        say(what + " ignored in " + state);
    }

    private void say(String message) {
        if (verbose) {
            System.out.println("    " + message);
        }
    }

    // ----------------------------------------------------------- observation

    /** The nested path of the current state, for example ON > DRIVE > FORWARD > CRUISING. */
    public String path() {
        switch (state) {
            case OFF:          return "OFF";
            case STOPPED:      return "ON > DRIVE > STOPPED";
            case ACCELERATING: return "ON > DRIVE > FORWARD > ACCELERATING";
            case CRUISING:     return "ON > DRIVE > FORWARD > CRUISING";
            case DECELERATING: return "ON > DRIVE > FORWARD > DECELERATING";
            case REVERSING:    return "ON > DRIVE > REVERSING";
            case IDLE:         return "ON > EQUIPMENT > IDLE";
            case COLOR_READY:  return "ON > EQUIPMENT > COLOR CAMERA > READY";
            case COLOR_TIMER:  return "ON > EQUIPMENT > COLOR CAMERA > TIMER";
            case CAM16_READY:  return "ON > EQUIPMENT > CAMERA 16MM > READY";
            case CAM16_TIMER:  return "ON > EQUIPMENT > CAMERA 16MM > TIMER";
            case DRILL_OFF:    return "ON > EQUIPMENT > DRILL > DRILL OFF";
            default:           return "ON > EQUIPMENT > DRILL > DRILL ON";
        }
    }

    public synchronized String status() {
        String timer = isTimer() ? (int) Math.max(timerRemaining, 0) + " s left" : "off";
        return path() + " | speed=" + speed + " reverse=" + reverseSpeed
                + " | drill=" + (state == State.DRILL_ON ? "on" : "off")
                + " | timer=" + timer + " | pictures=" + pictures + " | clock=" + clock + " s";
    }


    // ---------------------------------------------------------------- getters
    // Read-only access for the tests and the guide.

    public synchronized State getState() { return state; }
    public synchronized double getSpeed() { return speed; }
    public synchronized double getReverseSpeed() { return reverseSpeed; }
    public synchronized int getPictures() { return pictures; }

    /** Status, next moves and the map, taken together so the clock cannot change them in between. */
    public synchronized String guide() {
        return "\n" + status() + "\n\n" + RoverGuide.nextMoves(state) + "\n" + RoverGuide.map(state);
    }
}
