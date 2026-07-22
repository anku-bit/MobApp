package com.example.mobapp;
public class Question {
    public String question;
    public String[] options;
    public int correctIndex;
    public Question(String question, String[] options, int correctIndex){
        this.question=question; this.options=options; this.correctIndex=correctIndex;
    }
}
