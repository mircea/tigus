package org.tigus.app.editor;

import java.util.Vector;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.GridLayout;
import javax.swing.*;

import org.tigus.core.*;

/**
 * This class represents a tab with components that permit adding a new Question
 * to a Question Set
 * 
 * @author Adriana Draghici
 *
 */
class QuestionTabAdd {
    
    JTabbedPane tabbedPane;
    JTextArea questionTextArea = new JTextArea();
    JTextArea answerTextArea = new JTextArea(); 
    JTextField tagTextField = new JTextField();
    JTextField tagValueTextField = new JTextField();
    
    JButton answerButton = new JButton("Add answer");
    JButton tagButton = new JButton("  Add tag    ");

    JButton okButton = new JButton("Ok");       
    JButton cancelButton = new JButton("Cancel");
    
    JCheckBox correctCheckBox = new JCheckBox("Correct");
    JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
    JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
    JPanel mainPanel;
    
    int tabIndex;
    Boolean isCorrect = false;
    int correctCount = 0;
    Vector <Answer> answersList  = new Vector<Answer>(); 
    QuestionSet questionSet;
    Question question;
    String qsName;
    
    /**
     * Constructor
     * @param pane - the JTabbedPane object in which to add the new tab
     * @param qs - the QuestionSet object in which to add the created Question
     * @param qsname - the name of the QuestionSet
     */
    public QuestionTabAdd(JTabbedPane pane, QuestionSet qs, String qsName) {
           
        this.tabbedPane = pane;
        this.questionSet = qs;
        question = new Question();
        this.qsName = qsName;
        //initComponents();
    }
    
    /**
     * Initializes the main panel's components and containers by setting their size, their layout and their listeners.
     * 
     * @param none
     */
    public void initComponents() {      
        
        /*
         * set components size
         */ 
        
        answerButton.setPreferredSize(new Dimension(100,25));
        tagButton.setPreferredSize(new Dimension(100,25));
        tagButton.setMinimumSize(new Dimension(100,25));
        okButton.setPreferredSize(new Dimension(100,25));
        cancelButton.setPreferredSize(new Dimension(100,25));
        questionTextArea.setPreferredSize(new Dimension(300,100));
        answerTextArea.setPreferredSize(new Dimension(300,100));
        
              
        /*  set components layout */
        
        
        mainPanel = setLayout();
        
        /* add new tab */
        
        tabbedPane.addTab("Question", mainPanel);
        tabIndex = tabbedPane.getTabCount() -1;
        
        tabbedPane.setSelectedIndex(tabIndex); //set focus
        
        
        /* add listeners */
         addListeners();
        
    }
    
    /**
     * Places the class's GUI components into panels
     * @params none
     * @return JPanel object
     */
    
    private JPanel setLayout() {
        
        JPanel questionPanel = new JPanel();
        JPanel answerPanel = new JPanel();
        JPanel tagsPanel = new JPanel();
        JPanel confirmPanel = new JPanel();
    
        JPanel verticalPanel = new JPanel();      

        questionScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        answerButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        answerScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        correctCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    
        questionPanel.add(questionScrollPane);
        questionPanel.setBorder(BorderFactory.createTitledBorder("Text"));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        
        answerPanel.add(correctCheckBox);
        answerPanel.add(answerScrollPane);
        answerPanel.add(answerButton);    
        answerPanel.setBorder(BorderFactory.createTitledBorder("Answer"));
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        
        JPanel hPanel = new JPanel();
        hPanel.setLayout(new GridLayout(2,2));
        hPanel.add(tagTextField,1,0);
        hPanel.add(tagValueTextField,1,1);
        hPanel.add(new JLabel("Tag:"),0,0);
        hPanel.add(new JLabel("Values: "),0,1);
        hPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        tagsPanel.add(hPanel);
        
        tagsPanel.add(tagButton);
        tagsPanel.setBorder(BorderFactory.createTitledBorder("Tags"));
        tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.Y_AXIS));
        tagsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        confirmPanel.add(okButton);
        confirmPanel.add(cancelButton);     
        confirmPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        
        verticalPanel.add(questionPanel);       
        verticalPanel.add(answerPanel);
        verticalPanel.add(tagsPanel);
        verticalPanel.add(confirmPanel);
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        
        
        return verticalPanel;
    }
    
    /**
     * Add listeners to the class's GUI components (buttons, checkbox)
     * @params none
     */
    
    private void addListeners() {
        correctCheckBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   isCorrect = true;
                }
                else isCorrect = false;
            }
            
        }) ;
        
        answerButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                String answertext = answerTextArea.getText();
                if (answertext.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please insert the answer's text!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }  
                if (isCorrect == true)
                    correctCount ++;
                
                if (isCorrect == true && correctCount > 1){
                    JOptionPane.showMessageDialog(mainPanel,
                            "There must be only ONE correct answer!", 
                                "Error", JOptionPane.ERROR_MESSAGE);                    
                    correctCount--;
                    return;
                }
              
                
                System.out.println("answer : " + answertext);
                
                Answer answer = new Answer(isCorrect, answertext);
                answersList.add(answer);
                question.addAnswer(isCorrect, answertext);
                
                // clear components
                answerTextArea.setText("");
                correctCheckBox.setSelected(false);
                isCorrect = false;
                
            }
        });
        
        tagButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
            
                String tagName = tagTextField.getText();
                String tagValues = tagValueTextField.getText();
                
                if (tagName.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel,
                         "Please insert the tag's name!", 
                             "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tagValues.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please insert the tag's values!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                       return;
                }
               
               //String []values = tagValues.split(", ;");                   
              
                question.setTagValueList(tagName, tagValues);  
                
                // clear components
                tagTextField.setText("");
                tagValueTextField.setText("");
            
            }
        });
        
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               
               String questiontext = questionTextArea.getText();
               
               if (questiontext.length() == 0) {
                   JOptionPane.showMessageDialog(mainPanel,
                        "Please insert the question's text!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                   return;
               }
               
               if (answersList.size() == 0) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "Please insert the question's answers!", 
                               "Error", JOptionPane.ERROR_MESSAGE);
                      return;
               }
               if (answersList.size() == 1) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "Note that your question has only an answer!", 
                               "Warning", JOptionPane.WARNING_MESSAGE);
               }
               
               // create and add the the question to a Question Set
               System.out.println("question : " + questiontext);
               question.setText(questiontext);
              
               questionSet.add(question);
               
               try {
                   
                   questionSet.saveToFile(qsName);
                   
               }catch(Exception ex){
                   
               }
               
               int value = JOptionPane.showConfirmDialog(mainPanel,
                       "Do you want to create another question?",
                       "", JOptionPane.YES_NO_OPTION);
              
               if (value == JOptionPane.NO_OPTION) {
                   tabbedPane.removeTabAt(tabIndex);                   
               }
               
               else {
                   correctCount = 0;
                   questionTextArea.setText("");
                   answerTextArea.setText("");
                   question = new Question();              
                   answersList  = new Vector<Answer>(); 
                   
               } 
               
           }
           
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int value = JOptionPane.showConfirmDialog(mainPanel,
                        "Are you sure you want to cancel?",
                        "", JOptionPane.YES_NO_OPTION);
               
                if (value == JOptionPane.YES_OPTION) {
                    tabbedPane.removeTabAt(tabIndex);
                    
                }                   
            }
        });
    }
}