package com.kylemckell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static final Scanner input = new Scanner(System.in);
    static final Random random = new Random();
    public static void main(String[] args) {
        Set<Card> flashcards = new HashSet<>();
        boolean anotherAction = true;
        while (anotherAction) {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            switch (input.nextLine()) {
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
                    askCard(flashcards);
                    break;
                case "exit":
                    anotherAction = false;
                    System.out.println("Bye bye!");
                    break;
                case "log":
                    break;
                default:
                    break;
            }
            System.out.println();
        }
    }

    public static void addCard(Set<Card> flashcards) {
        System.out.println("The card:");
        String term = input.nextLine();
        for (Card card: flashcards) {
            if (card.getTerm().equals(term)) {
                System.out.println("The card \"" + term + "\" already exists.");
                return;
            }
        }
        System.out.println("The definition of the card:");
        String definition = input.nextLine();
        for (Card card: flashcards) {
            if (card.getDefinition().equals(definition)) {
                System.out.println("The definition of \"" + definition + "\" already exists.");
                return;
            }
        }
        System.out.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
        flashcards.add(new Card(term, definition));
    }

    public static void removeCard(Set<Card> flashcards) {
        System.out.println("The card:");
        String cardTerm = input.nextLine();
        int initialSize = flashcards.size();
        flashcards.removeIf(card -> card.getTerm().equals(cardTerm));
        if (initialSize == flashcards.size()) {
            System.out.println("Can't remove \"" + cardTerm + "\":m there is no such card.");
        }
        else {
            System.out.println("The card has been removed");
        }
    }

    public static void exportCards(Set<Card> flashcards) {
        System.out.println("File name:");
        String fileName = input.nextLine();
        File file = new File("./" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            for (Card card: flashcards) {
                writer.write(card.getTerm() + "," + card.getDefinition() + "\n");
            }
            System.out.println(flashcards.size() + " cards have been saved.");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    public static void importCards(Set<Card> flashcards) {
        System.out.println("File name:");
        String fileName = input.nextLine();
        File file = new File(fileName);
        try (Scanner scanner = new Scanner(file)) {
            int count = 0;
            while (scanner.hasNext()) {
                importParser(scanner.nextLine(), flashcards);
                count++;
            }
            System.out.println(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }

    public static void importParser(String importedText, Set<Card> flashcards) {
        String[] cardArray = importedText.split(",");
        Card card = new Card(cardArray[0], cardArray[1]);
        for (Card flashcard: flashcards) {
            if (flashcard.getTerm().equals(card.getTerm())) {
                flashcard.setDefinition(cardArray[1]);
                return;
            }
            else if (flashcard.getDefinition().equals(card.getDefinition())) {
                return;
            }
        }
        flashcards.add(card);
    }

    public static void askCard(Set<Card> flashcards) {
        System.out.println("How many times to ask?");
        int count = input.nextInt();
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
        System.out.println("Print the definition of \"" + card.getTerm() + "\":");
        String definition = input.nextLine();
        if (definition.equals(card.getDefinition())) {
            System.out.println("Correct Answer");
            return;
        }
        for (Card flashcard: flashcards) {
            if (definition.equals(flashcard.getDefinition())) {
                System.out.println("Wrong answer. The right answer is \"" + card.getDefinition() +
                        "\", but your definition is correct for \"" + flashcard.getTerm() + "\"."
                );
                return;
            }
        }
        System.out.println("Wrong answer. The right answer is \"" + card.getDefinition() + "\".");
    }
}

class Card {
    private final String term;
    private String definition;

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}