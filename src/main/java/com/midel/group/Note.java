
package com.midel.group;

/**
 The Note class represents a note that contains text and information about the date and time when this note should be used
 */
public class Note {

    /**
     * The text of the note.
     */
    private final String text;
    private final boolean isPermanent;

    /**
     * Constructs a new Note object with the specified text and empty date/time information.
     *
     * @param text the text of the note.
     */
    public Note(String text, boolean isPermanent){
        this.text = text;
        this.isPermanent = isPermanent;
    }

    public Note(String text){
        this(text.replace("PERMANENT","").trim(), text.contains("PERMANENT"));
    }

    public String getText() {
        return text;
    }

    public boolean isPermanent() {
        return isPermanent;
    }
}