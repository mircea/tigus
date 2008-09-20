package org.tigus.app.converter;

import org.tigus.conversion.PlainTextQuestionConverter;

public class QuestionConverter {

    /**
     * application that uses the PlainTextQuestionConverter to create
     * QuestionSet
     * 
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        PlainTextQuestionConverter plain;

        plain = new PlainTextQuestionConverter(args[0]);

        plain.parseFile();

        System.out.println("\n\ndone");

    }

}
