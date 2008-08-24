package org.tigus.app.editor;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import org.tigus.core.*;

public class QuestionSetTab {
    
    JTabbedPane tabbedPane = new JTabbedPane();
    QuestionSet questionSet;
    String qsName;
    
    JList questionsList = new JList();    
    JButton addButton = new JButton("Add question");
    JButton editButton = new JButton("Edit Question");
    JButton deleteButton = new JButton("Delete question");    
    JLabel qsNameLabel = new JLabel("New Question Set : ");
    JPanel mainPanel = new JPanel();
    
    public QuestionSetTab(JTabbedPane tabbedPane, 
                            QuestionSet qs, 
                            String qsName) {
        this.tabbedPane = tabbedPane;
        questionSet = qs;
        this.qsName = qsName;
        //initComponents();
        
    }
    
    public void showQuestionSetName(String name) {
        qsNameLabel.setText("Question Set : " + name);
    }
    
    public void updateQuestionsList() {
        /*
         * create panels for each question 
         * !!! not yet finished, it can show correctly only a QS with one question
         */
       
        int qsSize = questionSet.size();
        System.out.println("question set size:" + qsSize);
        Vector <JPanel> questionPanels = new Vector<JPanel>();
        DefaultListModel listModel = new DefaultListModel();
        int i = 0;
        int j; 
   
        
        for (Iterator <Question> it = questionSet.iterator(); it.hasNext(); ) {
            
            Question question = it.next();
            System.out.println("question Text:" + question.getText());
            
            // get answers
            Vector <Answer> answers = new Vector<Answer>(question.getAnswers());
            String answersText = "<html><ul>";            
            
            for (j = 0; j < answers.size(); j++) {
                answersText += "<li ";
                if (answers.elementAt(j).isCorrect() == true){
                    answersText +=  "type=circle> correct    : ";
                }
                else answersText += "type=disc> incorrect   : ";
                answersText += answers.elementAt(j).getText();
                answersText += "<br>";
            }
            answersText += "</ul></html>>";
            
            // create question's panel
            
            JPanel p = new JPanel();
            p.add(new JLabel(question.getText()));
       
            p.add(new JLabel(answersText));    
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            
            
            questionPanels.add(p);
            
            
            listModel.addElement(p);
            
            
            System.out.println("answers:" + answersText);            
            
            i++;
        }
        MyCellRenderer cr = new MyCellRenderer();
        questionsList.setCellRenderer(cr);
        questionsList.setListData(questionPanels);
        //questionsList.setModel(listModel);
    }
    
    public void initComponents() {   
        
        /* 
         * set components layout
         */
        
        tabbedPane.repaint();
        
        JPanel panel1  = new JPanel();
        JPanel buttonsPanel  = new JPanel(); 
        JScrollPane listPanel = new JScrollPane(questionsList);
        
        if (qsName!="") {
            showQuestionSetName(qsName);
        }
        
        panel1.add(qsNameLabel);
        
        /* create JList object for displaying questions*/
        updateQuestionsList();
        /* set layout */
                     
        buttonsPanel.add(addButton);             
        buttonsPanel.add(editButton);        
        buttonsPanel.add(deleteButton);        
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        
        mainPanel.add(panel1);  
        mainPanel.add(buttonsPanel);
        mainPanel.add(listPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        /* add listeners*/
        addListeners();
   
        tabbedPane.addTab("QS",  mainPanel);
        
    } 
    
    /** 
     * add buttons' listeners
     */
    private void addListeners() {
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                createQuestion();
            }
        });
        
        editButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                editQuestion();
            }
        });
        
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                //TODO
            }
        });
    
    }
       
 
    private void createQuestion() {
        
        try{                
            
            QuestionTabAdd qt = new QuestionTabAdd(this, tabbedPane, questionSet, qsName);
            qt.initComponents();
        
         }catch (Exception e){}
    }
    
    private void editQuestion() {
        try{ 
            // 
            // testing QuestionTabEdit 
            //
            
            Iterator <Question> it = questionSet.iterator(); 
            if (!it.hasNext()) return ;
            Question question = it.next();
            QuestionTabEdit qt = new QuestionTabEdit(this, tabbedPane, 
                                                    question, questionSet, qsName);
            qt.initComponents();
           
            
        }catch (Exception e) {}
        
    }
    

}

class MyCellRenderer extends JPanel implements ListCellRenderer {
    
    
    public MyCellRenderer() {
  
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        
       
      
       // add((JPanel)panel);
        JPanel panel = (JPanel)value;
        panel.setBorder(BorderFactory.createTitledBorder(""));
        Component component = (Component)panel; 
        
        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            background = Color.BLUE;
            foreground = Color.WHITE;

        // check if this cell is selected
        } else if (isSelected) {
            background = new Color(177,196,219);
            foreground = Color.WHITE;

        // unselected, and not the DnD drop location
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        };

        component.setBackground(background);
        component.setForeground(foreground);

        return component;
    }
}
