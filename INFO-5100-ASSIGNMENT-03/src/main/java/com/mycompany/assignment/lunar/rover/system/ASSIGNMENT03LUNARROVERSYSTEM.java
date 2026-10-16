/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.assignment.lunar.rover.system;

import java.util.Scanner;

/**
 *
 * @author amaan
 */
public class ASSIGNMENT03LUNARROVERSYSTEM {
 
    public static void main(String[] args) {
        LunarRover rover = new LunarRover(true);
        RoverClock clock = new RoverClock(rover);
        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);
        
        printHelp();
        System.out.println(rover.guide());
        clock.start();                              // time now moves by itself
 
        while (true) {
            System.out.print("> ");
            if (!in.hasNext()) {
                break;
            }
            String command = in.next().toLowerCase();
 
            try {
                if (command.equals("power")) {
                    rover.powerButton();
                } else if (command.equals("mode")) {
                    rover.modeButton();
                } else if (command.equals("ll")) {
                    rover.leftPressedTwice();
                } else if (command.matches("l[0-9.]+")) {
                    rover.leftReleased(Double.parseDouble(command.substring(1)));
                } else if (command.matches("r[0-9.]+")) {
                    rover.rightReleased(Double.parseDouble(command.substring(1)));
                } else if (command.equals("l") || command.equals("r")) {
                    System.out.println("Type the seconds right after the letter, for example L0.5 or R4");
                    continue;
                } else if (command.equals("tick")) {
                    rover.tick(1);
                } else if (command.matches("tick[0-9]+")) {
                    rover.tick(Integer.parseInt(command.substring(4)));
                } else if (command.equals("pause")) {
                    clock.pause();
                } else if (command.equals("resume")) {
                    clock.start();
                } else if (command.equals("status")) {
                    // just show the guide below
                } else if (command.equals("test")) {
                    RoverTests.runTests();
                    continue;
                } else if (command.equals("help")) {
                    printHelp();
                    continue;
                } else if (command.equals("quit")) {
                    break;
                } else {
                    System.out.println("Unknown command. Type help.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Not a number. Examples: L0.5 (tap), L6 (hold 6 s), R4");
                continue;
            }
            System.out.println(rover.guide());
        }
    }
 
    private static void printHelp() {
        System.out.println("Lunar Rover state machine (type the pedal seconds with no space)");
        System.out.println("  power      Button 1");
        System.out.println("  mode       Button 2 (mode switch)");
        System.out.println("  L0       left pedal released after 0.3 s (under 1 s = tap)");
        System.out.println("  L6         left pedal held 6 s");
        System.out.println("  R0       right pedal tap;  R4 = right pedal held 4 s");
        System.out.println("  LL         press left pedal twice within 1 second");
        System.out.println("  tick5      skip ahead 5 seconds right now (tick = 1 second)");
        System.out.println("  pause      stop the automatic clock");
        System.out.println("  resume     start the automatic clock again");
        System.out.println("  status     show the guide again");
        System.out.println("  test       run the built-in checks");
        System.out.println("  quit");
    }
}