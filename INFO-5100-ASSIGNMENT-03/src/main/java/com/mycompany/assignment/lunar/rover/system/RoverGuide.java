package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 6 of 7: RoverGuide.java
 *
 * The help text shown after every input: which moves work from the current state,
 * and a map of the whole diagram with ">>>" marking where the rover is. It only
 * builds text. It never changes the rover.
 */
public final class RoverGuide {

    private RoverGuide() {
        // only static methods, nothing to create
    }

    private static void move(StringBuilder sb, String typed, String what) {
        String pad = typed;
        while (pad.length() < 8) {
            pad = pad + " ";
        }
        sb.append("  ").append(pad).append(what).append("\n");
    }

    /** The typed moves that work in the current state, and what each one does. */
    public static String nextMoves(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("NEXT MOVES from ").append(state).append(":\n");
        switch (state) {
            case OFF:
                move(sb, "power", "turn the rover on, it starts STOPPED (M1)");
                return sb.toString();
            case STOPPED:
                move(sb, "L0", "tap the left pedal: accelerate forward (M3)");
                move(sb, "L4", "hold the left pedal over 3 s: accelerate backward (M4)");
                move(sb, "mode", "mode switch: go to Equipment mode (M12)");
                break;
            case ACCELERATING:
                move(sb, "R0", "tap the right pedal: slow down (M5)");
                move(sb, "R4", "hold the right pedal over 3 s: cruise at constant speed (M6)");
                move(sb, "wait", "speed keeps rising up to 12 km/h (M11)");
                break;
            case CRUISING:
                move(sb, "R0", "tap the right pedal: slow down (M7)");
                move(sb, "L0", "tap the left pedal: accelerate again (M8)");
                break;
            case DECELERATING:
                move(sb, "L0", "tap the left pedal: accelerate again (M8)");
                move(sb, "wait", "the rover stops by itself when speed reaches 0 (M9)");
                break;
            case REVERSING:
                move(sb, "R0", "tap the right pedal: stop (M10)");
                move(sb, "wait", "reverse speed keeps rising up to the limit (M13)");
                break;
            case IDLE:
                move(sb, "L5", "hold the left pedal 5 to 9.9 s: color camera (C2)");
                move(sb, "L10", "hold the left pedal 10 s or more: 16-mm camera (C3)");
                move(sb, "LL", "tap the left pedal twice: drill (C4)");
                move(sb, "mode", "mode switch: back to Drive, STOPPED (C1)");
                break;
            case COLOR_READY:
            case CAM16_READY:
                move(sb, "L0", "tap the left pedal: take a picture (C5)");
                move(sb, "L5", "hold the left pedal 5 s or more: start the selfie timer (C6)");
                move(sb, "R0", "tap the right pedal: back to IDLE (C8)");
                move(sb, "mode", "mode switch: back to Drive, STOPPED (C1)");
                break;
            case COLOR_TIMER:
            case CAM16_TIMER:
                move(sb, "wait", "after 10 s the picture is taken by itself (C7)");
                move(sb, "R0", "tap the right pedal: cancel the timer (C9)");
                move(sb, "mode", "mode switch: back to Drive, STOPPED (C1)");
                break;
            case DRILL_OFF:
                move(sb, "L0", "tap the left pedal: drill on (C10)");
                move(sb, "R0", "tap the right pedal: back to IDLE (C11)");
                move(sb, "mode", "mode switch: back to Drive, STOPPED (C1)");
                break;
            default:
                move(sb, "L0", "tap the left pedal: drill off (C10)");
                move(sb, "R0", "tap the right pedal: stop the drill, back to IDLE (C12)");
                move(sb, "mode", "mode switch: drill off, back to Drive, STOPPED (C1)");
                break;
        }
        move(sb, "power", "power off from any state (M2)");
        return sb.toString();
    }

    private static void mapRow(StringBuilder sb, State current, State here, String text) {
        sb.append(current == here ? ">>> " : "    ").append(text).append("\n");
    }

    /** The whole diagram as text. ">>>" marks the current state. */
    public static String map(State state) {
        StringBuilder sb = new StringBuilder();
    //     sb.append("MAP OF THE WHOLE DIAGRAM (>>> marks where you are)\n");
    //     sb.append("Pedals have no space: L0 = tap, L4 = hold 4 s. R is the right pedal.\n");
    //     mapRow(sb, state, State.OFF,          "OFF ................ power -> STOPPED (M1)");
    //     mapRow(sb, state, null,               "ON  (power -> OFF from every state below, M2)");
    //     mapRow(sb, state, null,               "  DRIVE");
    //     mapRow(sb, state, State.STOPPED,      "    STOPPED ........ L0 -> ACCELERATING | L4 -> REVERSING | mode -> IDLE");
    //     mapRow(sb, state, null,               "    FORWARD");
    //     mapRow(sb, state, State.ACCELERATING, "      ACCELERATING . R0 -> DECELERATING | R4 -> CRUISING");
    //     mapRow(sb, state, State.CRUISING,     "      CRUISING ..... R0 -> DECELERATING | L0 -> ACCELERATING");
    //     mapRow(sb, state, State.DECELERATING, "      DECELERATING . L0 -> ACCELERATING | speed 0 -> STOPPED (automatic)");
    //     mapRow(sb, state, State.REVERSING,    "    REVERSING ...... R0 -> STOPPED");
    //     mapRow(sb, state, null,               "  EQUIPMENT  (mode -> STOPPED from every state below, C1)");
    //     mapRow(sb, state, State.IDLE,         "    IDLE ........... L5 -> COLOR CAMERA | L10 -> CAMERA 16MM | LL -> DRILL");
    //     mapRow(sb, state, null,               "    COLOR CAMERA");
    //     mapRow(sb, state, State.COLOR_READY,  "      READY ........ L0 = picture | L5 -> TIMER | R0 -> IDLE");
    //     mapRow(sb, state, State.COLOR_TIMER,  "      TIMER ........ wait 10 s = picture, READY | R0 -> READY");
    //     mapRow(sb, state, null,               "    CAMERA 16MM");
    //     mapRow(sb, state, State.CAM16_READY,  "      READY ........ L0 = picture | L5 -> TIMER | R0 -> IDLE");
    //     mapRow(sb, state, State.CAM16_TIMER,  "      TIMER ........ wait 10 s = picture, READY | R0 -> READY");
    //     mapRow(sb, state, null,               "    DRILL");
    //     mapRow(sb, state, State.DRILL_OFF,    "      DRILL OFF .... L0 -> DRILL ON | R0 -> IDLE");
    //     mapRow(sb, state, State.DRILL_ON,     "      DRILL ON ..... L0 -> DRILL OFF | R0 -> IDLE (drill stops)");
        return sb.toString();
    }
}
