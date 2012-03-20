package ru.spbau.korovin.task2;

import java.io.FileFilter;
import java.io.FileNotFoundException;

/**
 * File system walker starter.
 */
public class Main {

    /**
     * Starts the walker from the desired path.
     * @param args args[0] - starting directory for tree walker
     */
    public static void main(String[] args) {

        if(args.length != 1) {
            usage();
        }

        try {
            FileFilter dotStarting = new PatternFilter("^\\.(.*)");
            FileSystemWalker walker = new FileSystemWalker(dotStarting);

            walker.startWalking(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("Directory not found: " + e.getMessage());
        }
    }

    private static void usage() {
        System.err.println("usage: java FileSystemWalker dir");
        System.exit(-1);
    }
}
