package org.tigus.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

public class QuestionSet extends HashSet<Question> {

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

    public QuestionSet() {
        super();
        init();
    }
    
    public QuestionSet(String fileName) throws IOException {
        super();
        init();
        loadFromFile(fileName);
    }

    public QuestionSet(Collection<Question> c) {
        super(c);
        init();
    }
    
    /**
     * Function for describing the QuestionSet in an XML String
     * @return XML string
     */
    public String toXML() {
        return xstream.toXML(this);
    }

    /**
     * Static function for creating a QuestionSet from an XML string
     * @param xml input string
     * @return the generated QuestionSet
     */
    public static QuestionSet createFromXML(String xml) {
        init();
        return (QuestionSet) xstream.fromXML(xml);
    }
    
    /**
     * Function for loading a QuestionSet from a file
     * @param fileName where the QuestionSet is located
     * @throws IOException
     */
    public void loadFromFile(String fileName) throws IOException {
        BufferedInputStream is = null;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile(fileName);
        Enumeration< ? extends ZipEntry> e = zipfile.entries();
        while (e.hasMoreElements()) {
            // iterate over the files in the archive
            entry = (ZipEntry) e.nextElement();
            if (entry.getName().equals("questions.xml")) {
                // create an input stream with the file
                is = new BufferedInputStream(zipfile.getInputStream(entry));
                // extract the question set from the XML file
                QuestionSet qset = (QuestionSet) xstream.fromXML(is);
                this.addAll(qset);
                is.close();
            }
        }
    }
    
    /**
     * Function for saving a QuestionSet to a file
     * @param fileName where the QuestionSet will be saved
     * @throws IOException
     */
    public void saveToFile(String fileName) throws IOException {
        FileOutputStream dest = new FileOutputStream(fileName);
        CheckedOutputStream checksum = new CheckedOutputStream(dest, new CRC32());
        ZipOutputStream out =
            new ZipOutputStream(new BufferedOutputStream(checksum));
        ZipEntry entry = new ZipEntry("questions.xml");
        out.putNextEntry(entry);
        out.write(xstream.toXML(this).getBytes());
        out.close();
    }

}
