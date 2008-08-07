package org.tigus.conversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PlainTextQuestionConverter {
    
    /*
     * Class that transforms plain text questions into usable objects
     */
    
    private int state;
    private final static int NOQUESTIONSTATE = 0;
    private final static int TAGSTATE = 1;
    private final static int QUESTIONTEXTSTATE = 2;
    private final static int ANSWERSTATE = 3;
    private File inputFile;
    //no-question -> tags -> question-text -> answers -> begin
    
    public PlainTextQuestionConverter(File input)
    {
        inputFile = new File( input.getPath() );
    }
    
    public PlainTextQuestionConverter(String path)
    {
        inputFile = new File(path);
    }
    
    private void parseFile()
    {
        FileReader inputReader;
        
        try {
            inputReader = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(inputReader);
            
            String line;
            
            
            state = NOQUESTIONSTATE;
            
            while((line = inputBuffer.readLine()) != null)
            {
                /*
                 * if the line is empty and it is not part of the question text
                 * or we are not in the answer state (where it separates the questions)
                 * we ignore it  
                 */
                
                if( state != QUESTIONTEXTSTATE && state != ANSWERSTATE 
                        && line.trim().length()==0 )
                    continue;
                
                /*
                 * if we are in tag state or in no qustion state 
                 * and the line starts with '@' we have tags to parse
                 */
                if( (state == TAGSTATE || state == NOQUESTIONSTATE) 
                        && line.startsWith("@") )
                {
                    state = TAGSTATE;
                    parseTags(line);
                }
                
                /*
                 * if we are in tag state or in question text state 
                 * and the line doesn't start with '@' nor '+' or'-'
                 * we have question text to parse
                 */
                
                if( (state == TAGSTATE || state == QUESTIONTEXTSTATE) 
                        && (!line.startsWith("@"))&& (!line.startsWith("+")) 
                        && (!line.startsWith("-")) )
                {
                    state = QUESTIONTEXTSTATE;
                    parseQuestionText(line);
                }
                
                /*
                 * if we are in question text state or in answer state 
                 * and the line starts with '+' or '-' we have answers to parse
                 */
                
                if( (state == QUESTIONTEXTSTATE || state == ANSWERSTATE) 
                        && (line.startsWith("+") || line.startsWith("-")) )
                {
                    state = ANSWERSTATE;
                    parseAnswer(line);
                }
                
                /*
                 * if we are in answer state and we have an empty line
                 * we jump to the next question (no question state)
                 */
                
                if( state == ANSWERSTATE && line.trim().length()==0 )
                    state = NOQUESTIONSTATE;
                
            } 
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }

    private void parseAnswer(String line) 
    {
        // TODO Auto-generated method stub    
    }

    private void parseQuestionText(String line) 
    {
        // TODO Auto-generated method stub
    }

    private void parseTags(String line) 
    {
        // TODO Auto-generated method stub
    }
}


