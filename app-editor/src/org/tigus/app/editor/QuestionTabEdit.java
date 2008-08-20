package org.tigus.app.editor;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;

import java.util.Vector;
import javax.swing.DefaultListModel;
import org.tigus.core.Answer;
import org.tigus.core.Question;
import org.tigus.core.QuestionSet;

class QuestionTabEdit {
        
    int listIndex;
    int tabIndex;
    Boolean isCorrect;;
    
    Vector <Answer> answers;
    QuestionSet questionSet;
    Question question;
    
    /* Tab components */
    JTabbedPane tabbedPane;
    JTextArea questionTextArea = new JTextArea();
    JTextArea answerTextArea = new JTextArea();     
    JButton addButton = new JButton("New answer");
    JButton editButton = new JButton("Edit answer");
    JButton deleteButton = new JButton("Delete answer");
    JButton saveButton = new JButton("Save answer");
    JButton okButton = new JButton("Ok");       
    JButton cancelButton = new JButton("Cancel");
    JCheckBox correctCheckBox = new JCheckBox("Correct");
    JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
    JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
    JScrollPane listScrollPane;
    DefaultListModel listModel = new DefaultListModel();
    JList answersList;
    int correctCount = 0;
    String state = "";
    Vector <Answer> newAnswers  = new Vector<Answer>();
    
    public QuestionTabEdit(JTabbedPane pane, Question question)//QuestionSet qs) {
    {       
        this.tabbedPane = pane;   
        this.question = question;
        answers = new Vector<Answer>(question.getAnswers());
        
        // build a list model containing the question's answer
        String s;
        for(int i = 0; i < answers.size(); i++){
            if (answers.elementAt(i).isCorrect() == true){
                s =  "correct    :";
                correctCount++;
            }
            else s = "incorrect :";
            listModel.addElement(s + answers.elementAt(i).getText());
        }
        // add the list model to the list component
        answersList = new JList(listModel);
        
        isCorrect = false;
        listIndex = -1;
        
        //this.questionSet = qs;
        //initComponents();
    }
    
    void initComponents(){      
        
       
        questionTextArea.setText(question.getText());
        
        /*
         * set components size
         */ 
        
        addButton.setPreferredSize(new Dimension(100,25));
        editButton.setPreferredSize(new Dimension(100,25));
        deleteButton.setPreferredSize(new Dimension(100,25));
        saveButton.setPreferredSize(new Dimension(100,25));
        okButton.setPreferredSize(new Dimension(100,25));
        cancelButton.setPreferredSize(new Dimension(100,25));
        questionTextArea.setPreferredSize(new Dimension(300,100));
        answerTextArea.setPreferredSize(new Dimension(300,100));
        
        
        answersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScrollPane = new JScrollPane(answersList);
        answersList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                
                listIndex = answersList.getSelectedIndex();
            }
        });
        
        /*  
         * set components layout
         */
        
        JPanel questionPanel = new JPanel();
        JPanel answerPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        JPanel confirmPanel = new JPanel();
        
        final JPanel verticalPanel = new JPanel();      

        questionScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        listScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        answerScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        correctCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        saveButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        questionPanel.add(questionScrollPane);
        questionPanel.add(listScrollPane);
        questionPanel.setBorder(BorderFactory.createTitledBorder("Text"));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        
        
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        answerPanel.add(buttonsPanel); 
        answerPanel.add(correctCheckBox);
        answerPanel.add(answerScrollPane);
        answerPanel.add(saveButton);
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
                else {
                    if (state == "editing") {
                        correctCount--;
                        
                    }
                    isCorrect = false;
                }
                
            }
            
        }) ;
        
        addButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                String answerText = answerTextArea.getText();
                if (answerText.length() == 0) {
                    JOptionPane.showMessageDialog(verticalPanel,
                            "Please introduce the answer's text!", 
                                "", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                answerTextArea.setText("");
                correctCheckBox.setSelected(false);
            }
        });
        
        editButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                if (listIndex == -1) {
                    JOptionPane.showMessageDialog(verticalPanel,
                            "Please select an answer!", 
                                "", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
               String answerText = answers.elementAt(listIndex).getText();
               
               // sets the component's content
               answerTextArea.setText(answerText);
               state = "editing";
               if (answers.elementAt(listIndex).isCorrect()) {
                   correctCheckBox.setSelected(true);
                   isCorrect = true;
               }
               else {                   
                   correctCheckBox.setSelected(false);
                   isCorrect = false;
               }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                               
            }
        });
        saveButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String answerText = answerTextArea.getText();
               if (answerText.length() == 0) {
                   JOptionPane.showMessageDialog(verticalPanel,
                           "The answer's text is empty!", 
                               "", JOptionPane.ERROR_MESSAGE);
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
      
               newAnswers.add(new Answer(isCorrect, answerText));
           }
            
        });
        
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String questionText = questionTextArea.getText();
               if (questionText.length() == 0) {
                   JOptionPane.showMessageDialog(verticalPanel,
                           "The question's text is empty!", 
                               "", JOptionPane.ERROR_MESSAGE);
                   return;
               }
               
               question.setId(questionText);
               for (int i = 0; i < newAnswers.size(); i++){
                   question.addAnswer(newAnswers.elementAt(i).isCorrect(), 
                                       newAnswers.elementAt(i).getText());
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

    