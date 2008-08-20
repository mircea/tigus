package org.tigus.app.editor;

import java.util.Vector;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.tigus.core.*;


class QuestionTabAdd {
    
    JTabbedPane tabbedPane;
    JTextArea questionTextArea = new JTextArea();
    JTextArea answerTextArea = new JTextArea();     
    JButton answerButton = new JButton("Add answer");
    JButton okButton = new JButton("Ok");       
    JButton cancelButton = new JButton("Cancel");
    JCheckBox correctCheckBox = new JCheckBox("Correct");
    JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
    JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
    int tabIndex;
    Boolean isCorrect = false;
    int correctCount = 0;
    Vector <Answer> answersList  = new Vector<Answer>(); 
    QuestionSet questionSet;
    
    public QuestionTabAdd(JTabbedPane pane, QuestionSet qs) {
           
        this.tabbedPane = pane;
        this.questionSet = qs;
        //initComponents();
    }
    
    void initComponents() {      
        
        /*
         * set components size
         */ 
        
        answerButton.setPreferredSize(new Dimension(100,25));
        okButton.setPreferredSize(new Dimension(100,25));
        cancelButton.setPreferredSize(new Dimension(100,25));
        questionTextArea.setPreferredSize(new Dimension(300,100));
        answerTextArea.setPreferredSize(new Dimension(300,100));
        
        
        
        /*  
         * set components layout
         */
        
        JPanel questionPanel = new JPanel();
        JPanel answerPanel = new JPanel();
        JPanel confirmPanel = new JPanel();
    
        final JPanel verticalPanel = new JPanel();      

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
        
        confirmPanel.add(okButton);
        confirmPanel.add(cancelButton);     
        confirmPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        
        verticalPanel.add(questionPanel);       
        verticalPanel.add(answerPanel);
        verticalPanel.add(confirmPanel);
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        
        
        /* add new tab */
        tabbedPane.addTab("Question", verticalPanel);
        tabIndex = tabbedPane.getTabCount() -1;
        
        tabbedPane.setSelectedIndex(tabIndex); //set focus
        
        
        /*
         * add listeners 
         */
        
        
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
                    JOptionPane.showMessageDialog(verticalPanel,
                            "Please insert the answer's text!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }  
                if (isCorrect == true)
                    correctCount ++;
                
                if (isCorrect == true && correctCount > 1){
                    JOptionPane.showMessageDialog(verticalPanel,
                            "There must be only ONE correct answer!", 
                                "Error", JOptionPane.ERROR_MESSAGE);                    
                    correctCount--;
                    return;
                }
              
                
                System.out.println("answer : " + answertext);
                
                Answer answer = new Answer(isCorrect, answertext);
                answersList.add(answer);
                answerTextArea.setText("");
                
            }
        });
        
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String questiontext = questionTextArea.getText();
               if (questiontext.length() == 0) {
                   JOptionPane.showMessageDialog(verticalPanel,
                        "Please insert the question's text!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                   return;
               }
               System.out.println("question : " + questiontext);
               if (answersList.size() == 0) {
                   JOptionPane.showMessageDialog(verticalPanel,
                           "Please insert the question's answers!", 
                               "Error", JOptionPane.ERROR_MESSAGE);
                      return;
               }
               if (answersList.size() == 1) {
                   JOptionPane.showMessageDialog(verticalPanel,
                           "Note that your question has only an answer!", 
                               "Warning", JOptionPane.WARNING_MESSAGE);
               }
               // create and add the the question to a Question Set
               Question question = new Question();
               question.setText(questiontext);
               for (int i = 0; i < answersList.size(); i++)
                   question.addAnswer(answersList.elementAt(i).isCorrect(), answersList.elementAt(i).getText());
               questionSet.add(question);
               
               try {
                   questionSet.saveToFile("qs1");
               }catch(Exception ex){
                   
               }
               
               int value = JOptionPane.showConfirmDialog(null,
                       "Do you want to create another question?",
                       "", JOptionPane.YES_NO_OPTION);
              
               if (value == JOptionPane.NO_OPTION) {
                   tabbedPane.removeTabAt(tabIndex);
                   QuestionTabEdit qt2 = new QuestionTabEdit(tabbedPane, question);
                   qt2.initComponents();
               }
               else {
                   correctCount = 0;
                   questionTextArea.setText("");
                   answerTextArea.setText("");
               } 
               
           }
           
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int value = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to cancel?",
                        "", JOptionPane.YES_NO_OPTION);
               
                if (value == JOptionPane.YES_OPTION) {
                    tabbedPane.removeTabAt(tabIndex);
                    
                }                   
            }
        });
        
    }
}