package org.tigus.core;

/**
 * @author Mircea Bardac
 * 
 */
public class Change {

    private Question before;
    private Question after;

    /**
     * Constructor for a Change object
     * 
     * @param before
     *        previous question
     * @param after
     *        current question
     */
    public Change(Question before, Question after) {
        this.before = new Question(before);
        this.after = new Question(after);
        this.before.getReviews().clear();
        this.before.setId(null);
        this.after.getReviews().clear();
        this.after.setId(null);
        if (this.before.getAnswers().equals(this.after.getAnswers())) {
            this.before.getAnswers().clear();
            this.after.getAnswers().clear();
        }
        if (this.before.getTags().equals(this.after.getTags())) {
            this.before.getTags().clear();
            this.after.getTags().clear();
        }
    }

    /**
     * Change copy constructor
     * 
     * @param change
     *        source to be copied
     */
    public Change(Change change) {
        this.before = new Question(change.before);
        this.after = new Question(change.after);
    }
    
    public Question getPreviousQuestion() {
        return this.before;
    }
    public Question getQuestion() {
        return this.after;
    }
}
