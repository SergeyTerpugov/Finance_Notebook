package com.company;

public class Note {
    private String text;

    public Note(String t) {
        this.text = t;
    }

    public String getText(){
        return this.text;
    }

    public int getPrice(){
        int indexDoubleDot = this.text.indexOf(":");

        if (indexDoubleDot != -1) {
            return Integer.parseInt(this.text.substring(0, indexDoubleDot));
        }
        return 0;
    }
}
