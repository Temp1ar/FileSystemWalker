package ru.spbau.korovin.task2;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
    private PrintStream out;

    /**
     * Constructs walker from file filter and desired comparator.
     * Print stream is by default: System.out
     *
     * @param fileFilter File filter
     * @param comparator Comparator for ordering files while walking
     */
    public FileSystemWalker(FileFilter fileFilter, Comparator<File> comparator)
    {
        this.fileFilter = fileFilter;
        this.comparator = comparator;
        this.out = System.out;
    }

    /**
     * Constructs walker with the desired comparator. Filter doesn't reject
     * anything. Print stream is by default: System.out
     *
     * @param comparator Comparator for ordering files while walking
     */
    public FileSystemWalker(Comparator<File> comparator)
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = comparator;
        this.out = System.out;
    }

    /**
     * Constructs walker with default comparator and desired file filter.
     * Print stream is by default: System.out
     *
     * @param fileFilter Desired file filter
     */
    public FileSystemWalker(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
        this.comparator = new LexicographicComparator();
        this.out = System.out;
    }

    /**
     * Constructs walker with default comparator. Filter doesn't reject
     * anything. Print stream is by default: System.out
     *
     */
    public FileSystemWalker()
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = new LexicographicComparator();
        this.out = System.out;
    }

    /**
     * Constructs walker from file filter, desired comparator and print stream.
     * 
     *
     * @param fileFilter File filter
     * @param comparator Comparator for ordering files while walking
     * @param out Output print stream
     */
    public FileSystemWalker(FileFilter fileFilter, Comparator<File> comparator, PrintStream out)
    {
        this.fileFilter = fileFilter;
        this.comparator = comparator;
        this.out = out;
    }

    /**
     * Constructs walker with the desired comparator and print stream.
     * Filter doesn't reject anything.
     *
     * @param comparator Comparator for ordering files while walking
     * @param out Output print stream
     */
    public FileSystemWalker(Comparator<File> comparator, PrintStream out)
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = comparator;
        this.out = out;
    }

    /**
     * Constructs walker with default comparator, desired file filter
     * and print stream.
     *
     * @param fileFilter Desired file filter
     * @param out Output print stream
     */
    public FileSystemWalker(FileFilter fileFilter, PrintStream out)
    {
        this.fileFilter = fileFilter;
        this.comparator = new LexicographicComparator();
        this.out = out;
    }

    /**
     * Constructs walker with default comparator. Filter doesn't reject
     * anything. Print stream can be passed in argument.
     *
     * @param out Output print stream
     */
    public FileSystemWalker(PrintStream out)
    {
        this.fileFilter = new PatternFilter("^$");
        this.comparator = new LexicographicComparator();
        this.out = out;
    }

    /**
     *  Walks whole tree from the rootPath directory.
     * @param rootPath Starting directory.
     * @throws java.io.FileNotFoundException If rootPath is not found.
     */
    public void startWalking(String rootPath) throws FileNotFoundException {
        File rootFile = convertPathToFile(rootPath);

        if(isReadableFile(rootFile)) {
            out.println(rootFile.getName());
            walkThrough(rootFile, "", rootFile.getName().length(), fileFilter);
        } else {
            if(rootFile != null) {
                out.println(rootFile.getName() + ACCESS_DENIED);
            } else {
                out.println(rootPath + ACCESS_DENIED);
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
        try {
            Path path = Paths.get(rootFile.getAbsolutePath()).normalize();
            rootFile = path.toFile();
        } catch(SecurityException e) {
            System.err.println("Can't resolve absolute path, " +
                    "will use relative.");
        }
        
        return rootFile;
    }

    private void walkThrough(File root, String prefix, int offset,
                             FileFilter fileFilter) {
        File[] list = root.listFiles(fileFilter);
        Arrays.sort(list, comparator);


        for(int i = 0; i < list.length; i++) {
            File file = list[i];
            String accessDeniedSuffix = isReadableFile(file)
                    ? ""
                    : ACCESS_DENIED;

            String oldPrefix = prefix;
            boolean lastOnLevel = (i == list.length - 1);
            prefix = constructPrefix(prefix, offset, lastOnLevel);
            out.println(
                    prefix
                    + (lastOnLevel ? "|" : "")
                    + "_"
                    + file.getName()
                    + accessDeniedSuffix);

            if(file.isDirectory() && isReadableFile(file)) {
                walkThrough(file, prefix,
                        file.getName().length() + 1 + (lastOnLevel ? 1 : 0),
                        fileFilter);
            }
            
            prefix = oldPrefix;
        }
    }

    private String constructPrefix(String prefix, int offset, boolean last) {
        if(offset <= 0) {
            return prefix;
        } else {
            // Repeat space " " symbol <offset> times
            char[] tmp = new char[offset];
            Arrays.fill(tmp, ' ');

            prefix += new String(tmp) + (last ? "" : "|");
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