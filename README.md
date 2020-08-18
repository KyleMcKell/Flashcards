# Flashcards

A project from JetBrains Academy's HyperSkill 

## Introduction

Flashcards is a project that makes use of the command line to have the user create and quiz themselves
on flashcards which they can either import or add themselves. 

## How to Use

### Command Line Arguments

* -import fileName.txt
    * Imports flashcards from an already existing file
* -export fileName.txt
    * Exports flashcards to a file at the end of the program
    
### User Input

* add
    * asks user to add a card with a term and a definition
* remove
    * asks user for a card to remove via term
* import
    * imports flashcards from file
* export
    * exports flashcards to file
* ask
    * quizzes the user on a select number of cards
* hardest card
    * tells the user which card they struggle on most
* reset stats
    * resets the amount of mistakes the user has made
* log
    * puts a log of all inputs and outputs to a file
* exit
    * exits the program
    
### Example formatting of import/export file

String CardTerm,String CardDefinition,int AmountOfMistakes

#### Like So

CardTerm0,CardDefinition0,AmountOfMistakes0
CardTerm1,CardDefinition1,AmountOfMistakes1

### What I learned during this project

This project taught me how to better work with files and collections.

Collections were an entirely new concept to me at the start of this project, so forcing myself to
use a hashmap as my method of storing my flashcards proved to be challenging at first, but eventually
was actually pretty easy.

I do think that there are some parts in this program which probably work clunkier than they could
due to the limitations of sets (namely the random asking of questions portion of the project), however,
that being said, I found it more appealing to challenge myself with the restriction of using a Set as to 
learn how they work, for I already know how an ArrayList works.

Overall, this project was probably the hardest one I've worked on thus far, and I'm pretty proud how
I accomplished it. I love my personal approach of storing each Flashcard with a custom class, and then 
making a set of that class, it just works very elegantly. I think I'll probably take a break from 
JetBrains Academy for the time being, for school is starting up, and I want to start making some personal
projects that mean more to me and aren't based on a website's arbitrary restrictions that they
put on you to put into your project.

There are a few things that I think I could improve with this project, such as adding a GUI interface,
improving how the program handles quizzing the user, and being able to handle a file that is passed in
that doesn't follow the requirements via the import command. That being said I think I'll move on 
for now, mainly because I have some other plans of things I want to work on, and this isn't my 
personal project, but rather was given to me via an online course.

## Enjoy!






