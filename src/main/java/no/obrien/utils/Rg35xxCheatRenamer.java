package no.obrien.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Rg35xxCheatRenamer {
    private static final String ROMS_DIRECTORY = "/Users/ianrobrien/Workspace/rg35xx/SD_CARD/Roms/SFC/";
    private static final String CHEATS_DIRECTORY = "/Users/ianrobrien/Development/libretro-database/cht/Nintendo - Super Nintendo Entertainment System/";
    private static final String OUTPUT_DIRECTORY_CHILD = "output";
    private static final String CHEAT_FILE_EXTENSION = ".cht";

    /***
     * Main method
     */
    public static void renameFiles() {
        var romsDir = new File(ROMS_DIRECTORY);
        var cheatsDir = new File(CHEATS_DIRECTORY);
        var outputDir = new File(CHEATS_DIRECTORY, OUTPUT_DIRECTORY_CHILD);

        if (!romsDir.isDirectory() || !cheatsDir.isDirectory()) {
            System.err.println("Error: One or both input directories do not exist.");
            System.exit(1);
        }

        createOutputDirectory(outputDir);

        File[] romFiles = Arrays.stream(Objects.requireNonNull(romsDir.listFiles()))
            .sorted()
            .toArray(File[]::new);
        File[] cheatFiles = Arrays.stream(Objects.requireNonNull(cheatsDir.listFiles()))
            .filter(file -> file.getName().endsWith(CHEAT_FILE_EXTENSION))
            .sorted()
            .toArray(File[]::new);

        List<File> conflictingFiles = new ArrayList<>();

        Arrays.stream(romFiles).forEach(romFile -> {
            if (romFile.isFile()) {
                System.out.println("Finding cheat for: " + romFile.getName());
                String romFileName = getFileNameWithoutExtension(romFile.getName());
                File exactMatch = new File(cheatsDir, romFileName + CHEAT_FILE_EXTENSION);

                if (exactMatch.exists() && exactMatch.isFile()) {
                    handleExactMatch(romFile, exactMatch, outputDir);
                } else {
                    tryFindMatch(romFileName, cheatFiles, outputDir, conflictingFiles);
                }
            }
        });

        if (!conflictingFiles.isEmpty()) {
            System.out.println("Conflicting files:");
            conflictingFiles.stream().map(File::getName).forEach(System.out::println);
        }
    }

    /***
     * Helper method to create the output directory
     *
     * @param outputDirectory output directory
     */
    private static void createOutputDirectory(File outputDirectory) {
        if (outputDirectory.exists() && outputDirectory.isDirectory()) {
            try {
                FileUtils.deleteDirectory(outputDirectory);
                System.out.println("Deleted existing 'output' subdirectory.");
            } catch (IOException e) {
                System.err.println("Failed to delete 'output' subdirectory: " + e.getMessage());
                System.exit(1);
            }
        }

        if (outputDirectory.mkdir()) {
            System.out.println("Created 'output' subdirectory.");
        } else {
            System.err.println("Failed to create 'output' subdirectory.");
            System.exit(1);
        }
    }

    /***
     * Helper method to handle exact match
     *
     * @param romFile ROM file
     * @param cheatFile exact match cheat file
     * @param outputDirectory output directory
     */
    private static void handleExactMatch(
        File romFile,
        File cheatFile,
        File outputDirectory) {
        if (!cheatFile.equals(romFile)) {
            System.out.println("Exact match found: " + romFile.getName());
            try {
                var result = Files.copy(
                    cheatFile.toPath(),
                    new File(outputDirectory, cheatFile.getName()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /***
     * Helper method to try to find a match based on the first characters
     *
     * @param romFileName ROM file name
     * @param cheatFiles cheat files
     * @param outputDirectory output directory
     * @param conflictingFiles conflicting files
     */
    private static void tryFindMatch(
        String romFileName,
        File[] cheatFiles,
        File outputDirectory,
        List<File> conflictingFiles) {
        boolean matchFound = false;
        for (int i = 0; i < romFileName.length(); i++) {
            String romPrefix = romFileName.substring(0, i);
            var matches = Arrays.stream(cheatFiles)
                .filter(cheatFile -> cheatFile.getName().startsWith(romPrefix))
                .toArray(File[]::new);
            if (matches.length == 1) {
                System.out.println("Match found using smart algorithm: " + romFileName);
                renameMatch(romFileName, outputDirectory, conflictingFiles, matches[0]);
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            String prefix = romFileName.substring(0, Math.min(romFileName.length(), 10));
            Arrays.stream(cheatFiles).sorted()
                .filter(cheatFile -> prefix.equals(
                    cheatFile.getName().substring(0, Math.min(cheatFile.getName().length(), 10))))
                .findFirst()
                .ifPresentOrElse(cheatFile -> {
                        System.out.println("Match found using fallback algorithm: " + romFileName);
                        renameMatch(romFileName, outputDirectory, conflictingFiles, cheatFile);
                    },
                    () -> System.err.println("No match found for: " + romFileName));
        }
    }

    /***
     * Helper method to rename the cheat file
     *
     * @param romFileName ROM file name
     * @param outputDirectory output directory
     * @param conflictingFiles conflicting files
     * @param cheatFile cheat file
     */
    private static void renameMatch(
        String romFileName,
        File outputDirectory,
        List<File> conflictingFiles,
        File cheatFile) {
        File newCheatFile = new File(outputDirectory, romFileName + CHEAT_FILE_EXTENSION);

        System.err.println(
            MessageFormat.format("Matching rom ''{0}'' to ''{1}''",
                romFileName,
                getFileNameWithoutExtension(cheatFile.getName())));

        if (newCheatFile.exists()) {
            conflictingFiles.add(newCheatFile);
            System.err.println("File name collision: " + newCheatFile.getName());
        } else {
            try {
                Files.copy(cheatFile.toPath(), newCheatFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied to 'output': " + newCheatFile.getName());
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
            }
        }
    }

    /***
     * Helper method to get file name without extension
     *
     * @param fileName file name
     * @return file name without extension
     */
    private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1
            ? fileName
            : fileName.substring(0, lastDotIndex);
    }
}
