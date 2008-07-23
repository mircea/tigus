package org.tigus.core;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * @author Mircea Bardac
 * 
 */
public class Question {

    private String id;

    private String text;
    private Vector<Answer> answers;
    private TagSet tags;
    private Vector<Review> reviews;

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = new Vector<Answer>(answers);
    }

    public TagSet getTags() {
        return tags;
    }

    public void setTags(TagSet tags) {
        this.tags = tags;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Vector<Review> reviews) {
        this.reviews = reviews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public Question() {
        this.id = UUID.randomUUID().toString();
        this.text = new String();
        this.answers = new Vector<Answer>();
        this.tags = new TagSet();
        this.reviews = new Vector<Review>();
    }

    public Question(Question q) {
        this.id = UUID.randomUUID().toString();
        this.text = new String(q.text);
        this.answers = new Vector<Answer>();
        for (Answer a : q.answers) {
            this.answers.add(new Answer(a));
        }
        this.tags = new TagSet(q.tags);
        this.reviews = new Vector<Review>();
        for (Review r : q.reviews) {
            this.reviews.add(new Review(r));
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addAnswer(boolean correct, String text) {
        answers.add(new Answer(correct, text));
    }

    public void setTag(String name, String value) {
        List<String> values = new Vector<String>();
        values.add(value);
        tags.put(name, values);
    }

    public void setTag(String name, List<String> value) {
        tags.put(name, value);
    }

    public void setTagValueList(String name, String stringList) {
        List<String> value = new Vector<String>();
        String[] list = stringList.split(",");
        for (String element : list) {
            value.add(element.trim());
        }
        tags.put(name, value);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Question other = (Question) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
