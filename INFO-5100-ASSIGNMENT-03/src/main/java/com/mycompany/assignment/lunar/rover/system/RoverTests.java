package com.mycompany.assignment.lunar.rover.system;

/**
 * FILE 7 of 7: RoverTests.java
 *
 * Built-in checks that replay the sample scenarios from the specification and compare
 * the result with the diagram. Run it two ways: type "test" in the console, or press
 * Shift+F6 on this file in NetBeans.
 */
public class RoverTests {

    public static void main(String[] args) {
        runTests();
    }

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    private static LunarRover powered() {
        LunarRover r = new LunarRover(false);
        r.powerButton();
        return r;
    }

    /** Replays the sample scenarios from the specification. */
    public static void runTests() {
        passed = 0;
        failed = 0;

        // Scenario 1: cruise and stop (M1, M3, M6, M7, M9)
        LunarRover r = new LunarRover(false);
        check("A9: starts in OFF", r.getState() == State.OFF);
        r.powerButton();
        check("M1: power on goes to STOPPED", r.getState() == State.STOPPED);
        r.leftReleased(0.3);
        check("M3: L pressed accelerates", r.getState() == State.ACCELERATING);
        r.tick(3);
        check("speed rises 1 per second", r.getSpeed() == 3.0);
        r.rightReleased(4);
        check("M6: R held 4 s cruises", r.getState() == State.CRUISING);
        r.tick(2);
        check("cruising holds the speed", r.getSpeed() == 3.0);
        r.rightReleased(0.3);
        check("M7: R pressed decelerates", r.getState() == State.DECELERATING);
        r.tick(5);
        check("M9: stops when speed reaches 0", r.getState() == State.STOPPED && r.getSpeed() == 0);

        // Scenario 2: reverse (M4, M13, M10)
        r = powered();
        r.leftReleased(4);
        check("M4: L held 4 s at rest reverses", r.getState() == State.REVERSING);
        r.tick(20);
        check("M13: reverse speed stops at rMax", r.getReverseSpeed() == RoverSettings.MAX_REVERSE_SPEED);
        r.rightReleased(0.3);
        check("M10: R pressed brakes to STOPPED", r.getState() == State.STOPPED && r.getReverseSpeed() == 0);

        // Scenario 3: mode switch ignored while moving (M14)
        r = powered();
        r.leftReleased(0.3);
        r.modeButton();
        check("M14: mode switch ignored while accelerating", r.getState() == State.ACCELERATING);
        r.tick(30);
        check("M11: forward speed stops at 12", r.getSpeed() == RoverSettings.MAX_FORWARD_SPEED);

        // Scenario 4: selfie (M12, C2, C5, C6, C7, C8, C1)
        r = powered();
        r.modeButton();
        check("M12: mode switch at rest enters IDLE", r.getState() == State.IDLE);
        r.leftReleased(6);
        check("C2: L held 6 s selects the color camera", r.getState() == State.COLOR_READY);
        r.leftReleased(0.4);
        check("C5: L pressed takes a picture", r.getPictures() == 1 && r.getState() == State.COLOR_READY);
        r.leftReleased(5);
        check("C6: L held 5 s starts the timer", r.getState() == State.COLOR_TIMER);
        r.tick(9);
        check("timer still running after 9 s", r.getState() == State.COLOR_TIMER && r.getPictures() == 1);
        r.tick(1);
        check("C7: after 10 s picture taken, back to READY", r.getState() == State.COLOR_READY && r.getPictures() == 2);
        r.rightReleased(0.3);
        check("C8: R pressed returns to IDLE", r.getState() == State.IDLE);
        r.modeButton();
        check("C1: mode switch returns to STOPPED", r.getState() == State.STOPPED);

        // Scenario 5: cancel the timer (C9, then C8)
        r = powered();
        r.modeButton();
        r.leftReleased(6);
        r.leftReleased(5);
        r.rightReleased(0.3);
        check("C9: R in TIMER cancels it and goes to READY, no picture",
                r.getState() == State.COLOR_READY && r.getPictures() == 0);
        r.rightReleased(0.3);
        check("C8: second R returns to IDLE", r.getState() == State.IDLE);

        // 16-mm camera (C3)
        r = powered();
        r.modeButton();
        r.leftReleased(10);
        check("C3: L held 10 s selects the 16-mm camera", r.getState() == State.CAM16_READY);
        r.leftReleased(5);
        r.tick(10);
        check("16-mm timer takes a picture after 10 s", r.getState() == State.CAM16_READY && r.getPictures() == 1);

        // Scenario 6: drill (C4, C10, C11, C12)
        r = powered();
        r.modeButton();
        r.leftPressedTwice();
        check("C4: two L presses select the drill", r.getState() == State.DRILL_OFF);
        r.leftReleased(0.3);
        check("C10: L turns the drill on", r.getState() == State.DRILL_ON);
        r.leftReleased(0.3);
        check("C10: L turns the drill off", r.getState() == State.DRILL_OFF);
        r.rightReleased(0.3);
        check("C11: R in Drill off returns to IDLE", r.getState() == State.IDLE);
        r.leftPressedTwice();
        r.leftReleased(0.3);
        r.rightReleased(0.3);
        check("C12: R in Drill on stops the drill and returns to IDLE", r.getState() == State.IDLE);

        // Scenario 7: leave Equipment with the drill on (exit action)
        r = powered();
        r.modeButton();
        r.leftPressedTwice();
        r.leftReleased(0.3);
        r.modeButton();
        check("C1: mode switch leaves Equipment and the drill is off",
                r.getState() == State.STOPPED);

        // Scenario 8: power off from inside Equipment (M2)
        r = powered();
        r.modeButton();
        r.leftReleased(6);
        r.leftReleased(5);
        r.powerButton();
        check("M2: power off from TIMER goes to OFF", r.getState() == State.OFF);
        r.tick(15);
        check("no picture appears after power off", r.getPictures() == 0);

        r = powered();
        r.leftReleased(0.3);
        r.tick(4);
        r.powerButton();
        check("M2: power off while moving sets speed to 0", r.getState() == State.OFF && r.getSpeed() == 0);

        // Guards and ignored inputs
        r = powered();
        r.leftReleased(2);
        check("a 2 s left release at rest does nothing", r.getState() == State.STOPPED);
        r.rightReleased(0.3);
        check("R pressed in STOPPED does nothing", r.getState() == State.STOPPED);
        r.modeButton();
        r.rightReleased(0.3);
        check("C13: R pressed in IDLE does nothing", r.getState() == State.IDLE);
        r.leftReleased(4.9);
        check("a 4.9 s left release in IDLE does nothing", r.getState() == State.IDLE);
        r.leftReleased(6);
        r.leftReleased(5);
        r.leftReleased(0.3);
        check("C14: L pressed in TIMER does nothing", r.getState() == State.COLOR_TIMER && r.getPictures() == 0);

        r = powered();
        r.leftReleased(0.3);
        r.rightReleased(0.3);
        r.rightReleased(4);
        check("R held while decelerating does not cruise", r.getState() == State.DECELERATING);
        r.leftReleased(0.3);
        check("M8: L pressed while decelerating accelerates again", r.getState() == State.ACCELERATING);

        System.out.println();
        System.out.println(passed + " checks passed, " + failed + " failed");
    }
}
