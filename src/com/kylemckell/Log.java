package com.kylemckell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// stores all inputs and outputs during a current session into a Collection, which can then be written to a file
class Log {
    private final Collection<String> logs;

    public Log() {
        this.logs = new ArrayList<>(); // Arraylist is used to keep the log in order chronologically;
    }

    // replaces all System.out.println, doubles as an output and also calls the storage of the message
    public void outputMessage(String message) {
        System.out.println(message);
        storeMessage(message);
    }

    // utilized for user inputs, stores the input of the user into the Log
    public void storeMessage(String message) {
        logs.add(message);
    }

    // exports the log to a file, each input/output to a line
    public void exportLog(String fileName) {
        File file = new File("./" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            for (String line: logs) {
                writer.write(line + "\n");
            }
            outputMessage("The log has been saved");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            storeMessage("An exception occurred");
        }
    }
}
