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

        String path;
        path = new String("/home/chupy/dev/tigus.git/challenge_week_10_1.txt");

        PlainTextQuestionConverter plain;
        plain = new PlainTextQuestionConverter(path);

        plain.parseFile();

        System.out.println("\n\ndone");

    }

}
