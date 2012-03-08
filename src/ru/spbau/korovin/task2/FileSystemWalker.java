package ru.spbau.korovin.task2;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Recursive file system walker. Prints out whole file system tree in
 * following format:
 * <pre><blockquote>
 * dir1
 *     |_subdir1
 *     |        |_file_in_deep_place.txt
 *     |_file1.txt
 * file_in_root.doc
 * </blockquote></pre>
 */
class FileSystemWalker {
    private static final String ACCESS_DENIED = " (access denied)";
    private final FileFilter fileFilter;
    private final Comparator<File> comparator;

    /**
     * Constructs walker from file filter and desired comparator.
     *
     * @param fileFilter File filter
     * @param comparator Comparator for ordering files while walking
     */
    public FileSystemWalker(FileFilter fileFilter, Comparator<File> comparator)
    {
        this.fileFilter = fileFilter;
        this.comparator = comparator;
    }

    /**
     * Constructs walker with the desired comparator. Filter doesn't reject
     * anything.
     *
     * @param comparator Comparator for ordering files while walking
     */
    public FileSystemWalker(Comparator<File> comparator)
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = comparator;
    }

    /**
     * Constructs walker with default comparator and desired file filter.
     *
     * @param fileFilter Desired file filter
     */
    public FileSystemWalker(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
        this.comparator = new LexicographicComparator();
    }

    /**
     * Constructs walker with default comparator. Filter doesn't reject
     * anything.
     *
     */
    public FileSystemWalker()
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = new LexicographicComparator();
    }

    /**
     *  Walks whole tree from the rootPath directory.
     * @param rootPath Starting directory.
     * @throws java.io.FileNotFoundException If rootPath is not found.
     */
    public void startWalking(String rootPath) throws FileNotFoundException {
        File rootFile = convertPathToFile(rootPath);

        if(isReadableFile(rootFile)) {
            System.out.println(rootFile.getName());
            walkThrough(rootFile, "", rootFile.getName().length(), fileFilter);
        } else {
            if(rootFile != null) {
                System.out.println(rootFile.getName() + ACCESS_DENIED);
            } else {
                System.out.println(rootPath + ACCESS_DENIED);
            }
        }
    }

    private File convertPathToFile(String rootPath) throws FileNotFoundException {
        File rootFile = new File(rootPath);
        try {
            if(!rootFile.exists()) {
                throw new FileNotFoundException(rootPath);
            }
        } catch(SecurityException e) {
            return null;
        }

        // This is conversion from relative path to absolute,
        // to show in first line name of directory in case when
        // rootPath = "."
        Path path = Paths.get(rootFile.getAbsolutePath()).normalize();
        rootFile = path.toFile();
        return rootFile;
    }

    private void walkThrough(File root, String prefix, int offset,
                             FileFilter fileFilter) {
        File[] list = root.listFiles(fileFilter);
        Arrays.sort(list, comparator);


        for(File file: list) {
            String accessDeniedSuffix = isReadableFile(file)
                    ? ""
                    : ACCESS_DENIED;

            String oldPrefix = prefix;
            prefix = constructPrefix(prefix, offset);
            System.out.println(prefix + "_" + file.getName()
                    + accessDeniedSuffix);

            if(file.isDirectory() && isReadableFile(file)) {
                walkThrough(file, prefix, file.getName().length() + 1,
                        fileFilter);
            }
            
            prefix = oldPrefix;
        }
    }

    private String constructPrefix(String prefix, int offset) {
        if(offset <= 0) {
            return prefix;
        } else {
            // Repeat space " " symbol <offset> times
            char[] tmp = new char[offset];
            Arrays.fill(tmp, ' ');

            prefix += new String(tmp) + "|";
            return prefix;
        }
    }

    private boolean isReadableFile(File file) {
        boolean readable;
        if(file == null) {
            return false;
        }
        try {
            readable = file.canRead();
            if (file.isDirectory()) {
                // This check is for windows systems,
                // for unknown reasons file.canRead always return true,
                // even for closed for this user directories
                readable = readable && (file.listFiles() != null);
            }
        } catch(SecurityException e) {
            return false;
        }
        return readable;
    }
}