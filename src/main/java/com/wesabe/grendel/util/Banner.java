package com.wesabe.grendel.util;

import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;

import java.io.PrintStream;

/**
 * 4/5/14 Created by Jonathan Garay
 */
public class Banner {
    public static String[] BANNER = {
            "╔═╗┬─┐┌─┐┌┐┌┌┬┐┌─┐┬  ",
            "║ ╦├┬┘├┤ │││ ││├┤ │  ",
            "╚═╝┴└─└─┘┘└┘─┴┘└─┘┴─┘",
            "                     ",
            "Spring Boot, Spring DI container"
    };

    public static void writeBanner(PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }

        printStream.println(AnsiOutput.toString(AnsiElement.GREEN, AnsiElement.FAINT, "0.4.1-netmask-SNAPSHOT"));
        printStream.println();
    }
}
