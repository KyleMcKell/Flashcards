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
        String exportFile = null;

        // checks to see if any arguments have been supplied on the command line
        // if an import argument exists, import the cards from the file right away
        // if an export argument exists, tell the program to store flashcards to the file before exiting
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")) {
                String importFile = args[i+1];
                fileSetup("import", importFile);
            }
            else if (args[i].equals("-export")) {
                exportFile = args[i+1];
                exportToFile = true;
            }
        }

        // gather user input, most of the program occurs here
        userInput();

        // if export argument existed, export cards before exiting
        if (exportToFile) {
            fileSetup("export", exportFile);
        }
        log.outputMessage("Bye bye!");
    }

    // asks the user to input an action, uses that input to then call a method to enact said action
    public static void userInput() {

        // loop breaks via return statement if action equals "exit"
        while (true) {
            log.outputMessage("Input the action (add, remove, import, export, ask, hardest card, reset stats, log, exit):");
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
                    fileSetup("export");
                    break;
                case "import":
                    fileSetup("import");
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

    // adds a card to flashcard HashSet, asking for both term and definition
    public static void addCard() {

        // asking for term of card
        log.outputMessage("The card:");
        String term = input.nextLine();
        log.storeMessage(term);

        // making sure the card doesn't already exist, if it does, don't add the card
        for (Card card: flashcards) {
            if (card.getTerm().equals(term)) {
                log.outputMessage("The card \"" + term + "\" already exists.");
                return;
            }
        }

        // asking for definition of card
        log.outputMessage("The definition of the card:");
        String definition = input.nextLine();
        log.storeMessage(definition);

        // making sure the definition doesn't already exist, if it does, don't add the card
        for (Card card: flashcards) {
            if (card.getDefinition().equals(definition)) {
                log.outputMessage("The definition of \"" + definition + "\" already exists.");
                return;
            }
        }

        // add the card to the flashcard HashSet
        log.outputMessage("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
        flashcards.add(new Card(term, definition, 0));
    }

    // removes a card via the term of the card
    public static void removeCard() {

        // asking for which card the user would like to remove
        log.outputMessage("The card:");
        String cardTerm = input.nextLine();
        log.storeMessage(cardTerm);

        //checking to see if the card is actually removed or not with a removeIf statement
        int initialSize = flashcards.size();
        flashcards.removeIf(card -> card.getTerm().equals(cardTerm));

        // if the size of our set is the same, the card wasn't removed, tell user the card doesn't exist
        if (initialSize == flashcards.size()) {
            log.outputMessage("Can't remove \"" + cardTerm + "\":m there is no such card.");
        }
        else {
            log.outputMessage("The card has been removed");
        }
    }


    // if a filename isn't supplied, get the filename to interact with
    public static void fileSetup(String type) {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        fileSetup(type, fileName);
    }

    // if importing call import method
    // if exporting call export method
    public static void fileSetup(String type, String fileName) {
        if (type.equals("import"))
            importCards(fileName);
        else if (type.equals("export")) {
            exportCards(fileName);
        }
    }

    // exports flashcards to a file, see example of exported file structure in README.md
    private static void exportCards(String fileName) {
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

    // imports flashcards from a file. reads each line, then passes the line to another method to parse
    private static void importCards(String fileName) {
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

    // parses what the line of the scanner reads
    // makes card objects that have a term, definition, and amount of mistakes
    public static void importParser(String importedText) {
        String[] cardArray = importedText.split(",");
        int mistakes = Integer.parseInt(cardArray[2]);
        Card card = new Card(cardArray[0], cardArray[1], mistakes);
        for (Card flashcard: flashcards) {
            // if a card already exists, in flashcards, set the definition to the imported definition
            // additionally, add any mistakes that current exist with the word to the current session's mistakes
            if (flashcard.getTerm().equals(card.getTerm())) {
                flashcard.setDefinition(cardArray[1]);
                flashcard.setMistakes(flashcard.getMistakes() + mistakes);
                return;
            }
            // if the definition already exists, don't add the card
            else if (flashcard.getDefinition().equals(card.getDefinition())) {
                return;
            }
        }
        flashcards.add(card);
    }

    // quizzes the user on random flashcards using a random number
    public static void askCard() {

        // makes sure the card pool isn't empty before asking to prevent errors
        if(flashcards.isEmpty()) {
            log.outputMessage("Add a card first!");
            return;
        }

        // checks to see how many times the user would like to be asked for a definition
        log.outputMessage("How many times to ask?");
        int count = input.nextInt();
        log.storeMessage(Integer.toString(count));
        input.nextLine();

        while (count > 0) {
            // Hashset doesn't use indexes, so we have to make our own version of a randomly chosen index
            // make a random number within our indexes of our cards
            int referenceRandom = random.nextInt(flashcards.size());
            // reference num to compare our random num to with each iteration
            int referenceNum = 0;
            for (Card flashcard: flashcards) {
                // if the reference num and random num match, ask about that card
                if (referenceRandom == referenceNum) {
                    cardGrading(flashcard);
                }
                // if the ref num and rand num don't match, increment ref num and loop again
                referenceNum++;
            }
            count--;
        }
    }

    // asks the user for the definition and evaluates if they were correct
    public static void cardGrading(Card card) {
        // ask for the definition
        log.outputMessage("Print the definition of \"" + card.getTerm() + "\":");
        String definition = input.nextLine();
        log.storeMessage(definition);

        // if they get it right, this method ends and the user is told they got the correct answer
        if (definition.equals(card.getDefinition())) {
            log.outputMessage("Correct Answer");
            return;
        }

        // if they are wrong, they get a mistake added to that card
        card.addMistake();

        // if the definition that they listed is the correct answer for a different card
        // we let them know by looping through our HashSet to see if any other cards hold that def
        for (Card flashcard: flashcards) {
            if (definition.equals(flashcard.getDefinition())) {
                log.outputMessage("Wrong answer. The right answer is \"" + card.getDefinition() +
                        "\", but your definition is correct for \"" + flashcard.getTerm() + "\"."
                );

                return;
            }
        }

        // if the definition was just wrong for everything, the right answer is told to them
        log.outputMessage("Wrong answer. The right answer is \"" + card.getDefinition() + "\".");
    }

    // logs the inputs and outputs of the current session into a separate file
    public static void logCards() {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        log.exportLog(fileName);
    }

    // the card which the user has made the most mistakes with is the hardest card
    // let the user know which card they mess up most with
    public static void hardestCard() {

        // find what the largest mistake count is for any of our cards
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

        // if the mistake count is 0, there are no cards with any errors, let the user know they are PERFECT!
        if (largestMistakeCount == 0) {
            log.outputMessage("There are no cards with errors. Yay!");
            return;
        }

        // there may be multiple cards with the most mistakes, find all of them and make an arrlist of them
        ArrayList<Card> hardestCardArr = new ArrayList<>();
        for (Card card: flashcards) {
            if (card.getMistakes() == largestMistakeCount) {
                hardestCardArr.add(card);
            }
        }

        // output a message letting the user know what their hardest card was if it was only 1 card
        if (numOfHardestCards == 1) {
            log.outputMessage(
                    "The hardest card is \"" + hardestCardArr.get(0).getTerm() +
                            "\". You have " + largestMistakeCount +
                            " errors answering it."
            );
        }
        // output a message if they had mutliple hard cards
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

    // resets all mistake counts to 0 for each flashcard
    public static void resetStats() {
        for (Card card: flashcards) {
            card.setMistakes(0);
        }
        log.outputMessage("Card statistics has been reset.");
    }
}

