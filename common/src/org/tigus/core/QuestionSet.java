package org.tigus.core;

import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Mircea Bardac
 * 
 */
class AnswerConverter implements Converter {

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return type.equals(Answer.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        Answer a = (Answer) value;
        writer.addAttribute("correct", a.isCorrect() ? "true" : "false");
        context.convertAnother(a.getText());
    }

    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        Answer answer = new Answer(new Boolean(reader.getAttribute("correct"))
                .booleanValue(), "");
        String text = (String) context.convertAnother(answer, String.class);
        answer.setText(text);
        return answer;
    }

}

class TagsConverter implements Converter {

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return type.equals(TagSet.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        TagSet tags = (TagSet) value;
        for (String tag : tags.keySet()) {
            writer.startNode("tag");
            writer.addAttribute("name", tag);
            context.convertAnother(tags.get(tag));
            writer.endNode();
        }
    }

    @SuppressWarnings("unchecked")
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        TagSet tags = new TagSet();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String tag = reader.getAttribute("name");
            Vector<String> v = (Vector<String>)
                    context.convertAnother(tags, Vector.class);
            tags.put(tag, v);
            reader.moveUp();
        }
        return tags;
    }

}

class QuestionSetConverter implements Converter {

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return type.equals(QuestionSet.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        QuestionSet qset = (QuestionSet) value;
        writer.addAttribute("version", QuestionSet.getVersion());
        for (Question q: qset) {
            writer.startNode("question");
            context.convertAnother(q);
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        QuestionSet qset = new QuestionSet();
        String ver = reader.getAttribute("version");
        if ( ! QuestionSet.getVersion().equals(ver) ) {
            return null;
        }
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Question q = (Question)
                    context.convertAnother(qset, Question.class);
            qset.add(q);
            reader.moveUp();
        }
        return qset;
    }

}

public class QuestionSet extends TreeSet<Question> {

    private static final long serialVersionUID = -8764732912877541072L;

    /**
     * version string
     * 
     * version string should be in the format "major.minor[-text]"
     * where:
     *  - "major" version number changes only when breaking compatibility
     *  - "minor" version number changes only when new features are added
     *  - "-text" is optional and might be an intermediate state
     */
    private static final String version = "0.1";

    /**
     * @return the version
     */
    public static String getVersion() {
        return version;
    }

    private static XStream xstream = null;

    private static void init() {
        if (xstream != null) {
            return;
        }

        xstream = new XStream(new DomDriver());

        xstream.alias("question", Question.class);
        xstream.alias("questionset", QuestionSet.class);
        xstream.alias("review", Review.class);
        xstream.alias("answer", Answer.class);

        xstream.useAttributeFor(Question.class, "id");
        xstream.useAttributeFor(Review.class, "date");

        xstream.registerConverter(new AnswerConverter());
        xstream.registerConverter(new TagsConverter());
        xstream.registerConverter(new QuestionSetConverter());
    }

    public QuestionSet(List<Question> list) {
        super(list);
        init();
    }

    public QuestionSet() {
        super();
        init();
    }

    public QuestionSet(String xml) {
        super();
        init();
        QuestionSet qset = QuestionSet.fromXML(xml);
        this.addAll(qset);
    }

    public String toXML() {
        return xstream.toXML(this);
    }

    private static QuestionSet fromXML(String xml) {
        init();
        return (QuestionSet) xstream.fromXML(xml);
    }
}
