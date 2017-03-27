package com.gmaur.legacycode.legacyutils.output;

/*
 *	LegacyUtils is a set of tools for dealing with legacy code
 *
 *	Copyright (C) 2017 G Maur (gmaur.com)
 *
 *	Subject to terms and condition provided in LICENSE
 *
 *  Acknowledgments: Rachel
 */

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StringIsEqualsAsPreviousInMatcher extends TypeSafeDiagnosingMatcher<String> {

    private static final String LINE_SEPARATOR_AND_WHITESPACES = System.getProperty("line.separator") + String.format("%8s", "");
    private Path path;

    public StringIsEqualsAsPreviousInMatcher(Path path) {
        this.path = path;
    }

    public static StringIsEqualsAsPreviousInMatcher isEqualsAsPreviousIn(Path path) {
        return new StringIsEqualsAsPreviousInMatcher(path);
    }

    @Override
    protected boolean matchesSafely(String consoleOutput, Description description) {
        try {
            File previousFile = path.toFile();
            File directoryOfPreviousFiles = new File(previousFile.getParent());
            if (!directoryOfPreviousFiles.exists()) {
                description.appendText("Create the directory: " + previousFile.getParent());
                return false;
            }
            if (previousFile.isFile()) {
                String previousFileContent = getStringFrom(previousFile);
                assertThat(consoleOutput, is(previousFileContent));
                return true;
            }
            writeConsoleOutputInFile(consoleOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string with the same content as " + path.toString());
        description.appendText(LINE_SEPARATOR_AND_WHITESPACES);
        description.appendText("Maybe, one first execution is required");
    }

    private String getStringFrom(File previousFile) throws IOException {
        return new String(Files.readAllBytes(previousFile.toPath()));
    }

    private void writeConsoleOutputInFile(String consoleOutput) throws IOException {
        try (FileWriter fw = new FileWriter(path.toString(), false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(consoleOutput);
        }
    }
}