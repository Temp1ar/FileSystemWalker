package ru.spbau.korovin.task2;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Pattern File filter, that accepts regular expression and can
 * be used to filter off files, that match(!) regular expression.
 */
class PatternFilter implements FileFilter {
    private final Pattern pattern;
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
        Matcher matcher = pattern.matcher(file.getName());
        return !matcher.find();
    }

    /**
     * Constructs PatternFilter to filter undesired files with regular
     * expression.
     * @param regex Regular expression
     * @throws PatternSyntaxException If bad regex passed to parameter.
     */
    public PatternFilter(String regex) throws PatternSyntaxException {
        pattern = Pattern.compile(regex);
    }
}
