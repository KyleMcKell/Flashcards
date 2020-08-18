package com.kylemckell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static final Scanner input = new Scanner(System.in);
    static final Random random = new Random();
    static final Log log = new Log();
    public static void main(String[] args) {
        Set<Card> flashcards = new HashSet<>();
        boolean anotherAction = true;
        while (anotherAction) {
            log.outputMessage("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = input.nextLine();
            log.storeMessage(action);
            switch (action) {
                case "add":
                    addCard(flashcards);
                    break;
                case "remove":
                    removeCard(flashcards);
                    break;
                case "import":
                    importCards(flashcards);
                    break;
                case "export":
                    exportCards(flashcards);
                    break;
                case "ask":
                    if(flashcards.isEmpty()) {
                        log.outputMessage("Add a card first!");
                        break;
                    }
                    askCard(flashcards);
                    break;
                case "log":
                    log.outputMessage("File name:");
                    String fileName = input.nextLine();
                    log.storeMessage(fileName);
                    log.exportLog(fileName);
                    break;
                case "hardest card":
                    hardestCard(flashcards);
                    break;
                case "reset stats":
                    resetStats(flashcards);
                    break;
                case "exit":
                    anotherAction = false;
                    log.outputMessage("Bye bye!");
                    break;
                default:
                    break;
            }
        }
    }

    public static void addCard(Set<Card> flashcards) {
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

    public static void removeCard(Set<Card> flashcards) {
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

    public static void exportCards(Collection<Card> flashcards) {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
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

    public static void importCards(Set<Card> flashcards) {
        log.outputMessage("File name:");
        String fileName = input.nextLine();
        log.storeMessage(fileName);
        File file = new File(fileName);
        try (Scanner scanner = new Scanner(file)) {
            int count = 0;
            while (scanner.hasNext()) {
                importParser(scanner.nextLine(), flashcards);
                count++;
            }
            log.outputMessage(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            log.outputMessage("File not found");
        }

        for (Card card: flashcards) {
            System.out.println(card.getMistakes());
        }
    }

    public static void importParser(String importedText, Set<Card> flashcards) {
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

    public static void askCard(Set<Card> flashcards) {
        log.outputMessage("How many times to ask?");
        int count = input.nextInt();
        log.storeMessage(Integer.toString(count));
        input.nextLine();
        while (count > 0) {
            int referenceRandom = random.nextInt(flashcards.size());
            int referenceNum = 0;
            for (Card flashcard: flashcards) {
                if (referenceRandom == referenceNum) {
                    cardGrading(flashcard, flashcards);
                }
                referenceNum++;
            }
            count--;
        }
    }

    public static void cardGrading(Card card, Set<Card> flashcards) {
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

    public static void hardestCard(Set<Card> flashcards) {
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

    public static void resetStats(Set<Card> flashcards) {
        for (Card card: flashcards) {
            card.setMistakes(0);
        }
        log.outputMessage("Card statistics has been reset.");
    }
}

