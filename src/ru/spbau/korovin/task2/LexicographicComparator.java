package ru.spbau.korovin.task2;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator used in {@see FileSystemWalker} to
 * properly sort file list. Subdirectories are "smaller" than files.
 * And in each group of files and directories elements shows up
 * in lexicographic order.
 */
public class LexicographicComparator implements Comparator<File> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     *
     * @param f1 the first file to be compared.
     * @param f2 the second file to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     * @throws SecurityException If one of the files can't be accessed.
     */
    @Override
    public int compare(File f1, File f2) throws SecurityException {
        if (f1.isDirectory() && !f2.isDirectory()) {
            return -1;

        } else if (!f1.isDirectory() && f2.isDirectory()) {
            return 1;

        } else {
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }
}
