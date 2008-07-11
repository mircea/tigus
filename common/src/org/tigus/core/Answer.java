package org.tigus.core;

/**
 * @author Mircea Bardac
 * 
 */
public class Answer {

    private boolean correct;
    private String text;

    /**
     * @return the correct
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @param correct the correct to set
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Answer(boolean correct, String text) {
        this.correct = correct;
        this.text = text;
    }

    public Answer(Answer a) {
        this.correct = a.correct;
        this.text = new String(a.text);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        Answer test = (Answer) obj;
        return ((test.correct == this.correct) && (test.text.equals(this.text)));
    }
}
