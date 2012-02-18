package ru.spbau.korovin.task2;

import java.io.File;
import java.io.FileFilter;

/**
 * Class filters out hidden files.
 */
public class HiddenFileFilter implements FileFilter {
    /**
     * Tests whether or not the specified abstract file should be
     * included in a file list.
     *
     * @param file The abstract file to be tested
     * @return <code>true</code> if and only if <code>file</code>
     *         should be included
     */
    @Override
    public boolean accept(File file) {
        return !file.isHidden();
    }
}
