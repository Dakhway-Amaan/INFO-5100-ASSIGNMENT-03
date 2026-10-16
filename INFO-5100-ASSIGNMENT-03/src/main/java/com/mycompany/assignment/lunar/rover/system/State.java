package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 2 of 7: State.java
 *
 * Every state of the rover, as it is named in the diagram. Only the innermost
 * (leaf) states are listed here, because the rover is always in exactly one of them.
 * The nested (composite) states are worked out from these in LunarRover.java.
 *
 *   OFF
 *   ON
 *     DRIVE
 *       STOPPED
 *       FORWARD   { ACCELERATING, CRUISING, DECELERATING }
 *       REVERSING
 *     EQUIPMENT
 *       IDLE
 *       COLOR CAMERA  { READY, TIMER }     -> COLOR_READY, COLOR_TIMER
 *       CAMERA 16MM   { READY, TIMER }     -> CAM16_READY, CAM16_TIMER
 *       DRILL         { DRILL OFF, DRILL ON }
 */
public enum State {
    OFF,
    STOPPED, ACCELERATING, CRUISING, DECELERATING, REVERSING,
    IDLE,
    COLOR_READY, COLOR_TIMER,
    CAM16_READY, CAM16_TIMER,
    DRILL_OFF, DRILL_ON
}
