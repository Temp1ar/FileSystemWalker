package ru.spbau.korovin.task2;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Recursive file system walker. Print's out whole file system tree in
 * following format:
 * <pre><blockquote>
 * dir1
 *     |_subdir1
 *              |_file_in_deep_place.txt
 *     |_file1.txt
 * file_in_root.doc
 * </blockquote></pre>
 */
public class FileSystemWalker {
    private static final String ACCESS_DENIED = " (access denied)";
    private File rootFile;

    /**
     * Constructs walker from the string with absolute or relative path
     * to directory.
     * @param rootPath Starting directory
     * @throws FileNotFoundException If directory is not exists.
     */
    public FileSystemWalker(String rootPath)
            throws FileNotFoundException {
        rootFile = new File(rootPath);
        if(!rootFile.exists()) {
            throw new FileNotFoundException(rootPath);
        }

        // This is conversion from relative path to absolute,
        // to show in first line name of directory in case when
        // rootPath = "."
        Path path = Paths.get(rootFile.getAbsolutePath()).normalize();
        rootFile = path.toFile();
    }

    /**
     * Starts to print fs tree to console.
     */
    public void startWalking() {
        if(isReadableFile(rootFile)) {
            System.out.println(rootFile.getName());
            walkThrough(rootFile, rootFile.getName().length());
        } else {
            System.out.println(rootFile.getName() + ACCESS_DENIED);
        }
    }

    private void walkThrough(File root, int offset) {
        File[] list = root.listFiles(new PatternFilter("^\\."));
        // For platform independent hidden files isolation, we should use:
        // File[] list = root.listFiles(new HiddenFileFilter());
        Arrays.sort(list, new LexicographicComparator());

        for(File file: list) {
            String accessDeniedSuffix = isReadableFile(file)
                    ? ""
                    : ACCESS_DENIED;

            System.out.println(constructPrefix(offset) +
                    file.getName() + accessDeniedSuffix);

            if(file.isDirectory() && isReadableFile(file)) {
                // Additional offset for |_ symbols.
                int currentOffset = file.getName().length()
                              + ( offset == 0 ? 0 : 2 );
                
                offset += currentOffset;
                walkThrough(file, offset);
                offset -= currentOffset;
            }
        }
    }

    private String constructPrefix(int offset) {
        if(offset <= 0) {
            return "";
        } else {
            // Repeat space " " symbol <offset> times
            String output = new String(new char[offset]).replace("\0", " ");

            output += "|_";
            return output;
        }
    }

    private boolean isReadableFile(File file) {
        boolean readable = file.canRead();
        if (file.isDirectory()) {
            // This check is for windows systems,
            // for unknown reasons file.canRead always return true,
            // even for closed for this user directories
            readable = readable && (file.listFiles() != null);
        }
        return readable;
    }
}