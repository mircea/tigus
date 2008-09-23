package org.tigus.core;

import java.util.Date;

/**
 * @author Mircea Bardac
 * 
 */
public class Review {

    private String author;
    private Date date;
    private String comment;
    private Change change;

    public Review(Date date, String author, String comment, Question before,
            Question after) {
        this.date = date;
        this.author = author;
        this.comment = comment;
        this.change = new Change(before, after);
    }

    public Review(Review r) {
        this.date = (Date) r.date.clone();
        this.author = new String(r.author);
        this.comment = new String(r.comment);
        this.change = new Change(r.change);
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public Date getDate() {
        return this.date;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public Change getChange() {
        return this.change;
    }

}
