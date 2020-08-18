package com.kylemckell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    // Objects made static for fluid use throughout the program, all final
    static final Scanner input = new Scanner(System.in); // Scanner for user input
    static final Random random = new Random(); // Random for asking random flashcard questions, may want to refactor
    static final Log log = new Log(); // Log for all inputs and outputs during current session
    static final Set<Card> flashcards = new HashSet<>(); // Our HashSet of Flashcards

    public static void main(String[] args) {
        boolean exportToFile = false; // checks to see if cards should be exported at the end of the program running
        String exportArg = null;

        // checks to see if any arguments have been supplied on the command line
        // if an import argument exists, import the cards from the file right away
        // if an export argument exists, tell the program to store flashcards to the file before exiting
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")) {
                String importArg = args[i+1];
                importCards(importArg);
            }
            else if (args[i].equals("-export")) {
                exportArg = args[i+1];
                exportToFile = true;
            }
        }

        // gather user input, most of the program occurs here
        userInput();

        // if export argument existed, export cards before exiting
        if (exportToFile) {
            exportCards(exportArg);
        }
        log.outputMessage("Bye bye!");
    }

    // asks the user to input an action, uses that input to then call a method to enact said action
    public static void userInput() {
        while (true) { // loop breaks via return statement if action equals "exit"
            log.outputMessage("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = input.nextLine();
            log.storeMessage(action);
            switch (action) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "export":
                    exportCards();
                    break;
                case "import":
                    importCards();
                    break;
                case "ask":
                    askCard();
                    break;
                case "log":
                    logCards();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "exit":
                    return;
                default:
                    break;
            }
        }
    }

    public static void addCard() {
        log.outputMessage("The card:");
        String term = input.nextLine();
        log.storeMessage(term);
        for (Card card: flashcards) {
            if (card.getTerm().equals(term)) {
                log.outputMessage("The card \"" + term + "\" already exists.");
                return;
            }
        }
        log.outputMessage("The definition of the card:");
        String definition = input.nextLine();
        log.storeMessage(definition);
        for (Card card: flashcards) {
            if (card.getDefinition().equals(definition)) {
                log.outputMessage("The definition of \"" + definition + "\" already exists.");
                return;
            }
        }
        log.outputMessage("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
        flashcards.add(new Card(term, definition, 0));
    }

    public static void removeCard() {
        log.outputMessage("The card:");
        String cardTerm = input.nextLine();
        log.storeMessage(cardTerm);
        int initialSize = flashcards.size();
        flashcards.removeIf(card -> card.getTerm().equals(cardTerm));
        if (initialSize == flashcards.size()) {
            log.outputMessage("Can't remove \"" + cardTerm + "\":m there is no such card.");
        }
        else {
            log.outputMessage("The card has been removed");
        }
    }

    public static void exportCards() {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        exportSetup(fileName);
    }

    public static void exportCards(String fileName) {
        exportSetup(fileName);
    }

    private static void exportSetup(String fileName) {
        File file = new File("./" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            for (Card card: flashcards) {
                writer.write(card.getTerm() + "," + card.getDefinition() +  "," + card.getMistakes() + "\n");
            }
            log.outputMessage(flashcards.size() + " cards have been saved.");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            log.storeMessage("An exception occurred");
        }
    }

    public static void importCards() {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        importSetup(fileName);
    }

    public static void importCards(String fileName) {
        importSetup(fileName);
    }

    private static void importSetup(String fileName) {
        File file = new File(fileName);
        try (Scanner scanner = new Scanner(file)) {
            int count = 0;
            while (scanner.hasNext()) {
                importParser(scanner.nextLine());
                count++;
            }
            log.outputMessage(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            log.outputMessage("File not found");
        }
    }

    public static void importParser(String importedText) {
        String[] cardArray = importedText.split(",");
        int mistakes = Integer.parseInt(cardArray[2]);
        Card card = new Card(cardArray[0], cardArray[1], mistakes);
        for (Card flashcard: flashcards) {
            if (flashcard.getTerm().equals(card.getTerm())) {
                flashcard.setDefinition(cardArray[1]);
                flashcard.setMistakes(flashcard.getMistakes() + mistakes);
                return;
            }
            else if (flashcard.getDefinition().equals(card.getDefinition())) {
                return;
            }
        }
        flashcards.add(card);
    }

    public static void askCard() {
        if(flashcards.isEmpty()) {
            log.outputMessage("Add a card first!");
            return;
        }
        log.outputMessage("How many times to ask?");
        int count = input.nextInt();
        log.storeMessage(Integer.toString(count));
        input.nextLine();
        while (count > 0) {
            int referenceRandom = random.nextInt(flashcards.size());
            int referenceNum = 0;
            for (Card flashcard: flashcards) {
                if (referenceRandom == referenceNum) {
                    cardGrading(flashcard);
                }
                referenceNum++;
            }
            count--;
        }
    }

    public static void cardGrading(Card card) {
        log.outputMessage("Print the definition of \"" + card.getTerm() + "\":");
        String definition = input.nextLine();
        log.storeMessage(definition);
        if (definition.equals(card.getDefinition())) {
            log.outputMessage("Correct Answer");
            return;
        }
        card.addMistake();
        for (Card flashcard: flashcards) {
            if (definition.equals(flashcard.getDefinition())) {
                log.outputMessage("Wrong answer. The right answer is \"" + card.getDefinition() +
                        "\", but your definition is correct for \"" + flashcard.getTerm() + "\"."
                );

                return;
            }
        }
        log.outputMessage("Wrong answer. The right answer is \"" + card.getDefinition() + "\".");
    }

    public static void logCards() {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        log.exportLog(fileName);
    }

    public static void hardestCard() {
        int largestMistakeCount = 0;
        int numOfHardestCards = 0;
        for (Card card: flashcards) {
            if (card.getMistakes() > largestMistakeCount) {
                largestMistakeCount = card.getMistakes();
                numOfHardestCards = 1;
            }
            else if (card.getMistakes() == largestMistakeCount) {
                numOfHardestCards++;
            }
        }

        if (largestMistakeCount == 0) {
            log.outputMessage("There are no cards with errors.");
            return;
        }

        ArrayList<Card> hardestCardArr = new ArrayList<>();
        for (Card card: flashcards) {
            if (card.getMistakes() == largestMistakeCount) {
                hardestCardArr.add(card);
            }
        }

        if (numOfHardestCards == 1) {
            log.outputMessage(
                    "The hardest card is \"" + hardestCardArr.get(0).getTerm() +
                            "\". You have " + largestMistakeCount +
                            " errors answering it."
            );
        }
        else {
            StringBuilder hardestCardString = new StringBuilder();
            for (Card card : hardestCardArr) {
                hardestCardString.append("\"").append(card.getTerm()).append("\"");
                if (hardestCardArr.indexOf(card) != hardestCardArr.toArray().length - 1) {
                    hardestCardString.append(", ");
                }
            }
            log.outputMessage(
                    "The hardest cards are " +
                            hardestCardString +
                            ". You have " + largestMistakeCount +
                            " errors answering them.");
        }
    }

    public static void resetStats() {
        for (Card card: flashcards) {
            card.setMistakes(0);
        }
        log.outputMessage("Card statistics has been reset.");
    }
}

