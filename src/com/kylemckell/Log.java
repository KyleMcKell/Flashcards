package com.kylemckell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

public class Log {
    private SortedSet<String> logs;

    public Log() {
        this.logs = new TreeSet<>();
    }

    public void outputMessage(String message) {
        System.out.println(message);
        storeMessage(message);
    }

    public void storeMessage(String message) {
        logs.add(message);
    }

    public void exportLog(String fileName) {
        File file = new File("./" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            for (String line: logs) {
                writer.write(line);
            }
            outputMessage("The log has been saved");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            storeMessage("An exception occured");
        }
    }
}
