package org.tigus.app.editor;

import java.util.Vector;
import java.awt.Dimension;
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
    
    
    public QuestionSetTab(JTabbedPane tabbedPane, 
                            QuestionSet qs, 
                            String qsName) {
        this.tabbedPane = tabbedPane;
        questionSet = qs;
        this.qsName = qsName;
        //initComponents();
        
    }
    
    public void initComponents() {   
        
        /* 
         * set components layout
         */
        
        tabbedPane.repaint();
        
        
        
        JPanel panel1  = new JPanel();
        JPanel buttonsPanel  = new JPanel();
       
        JPanel panel = new JPanel();
        JPanel qPanel = new JPanel();
        /*
         * create panels for each question
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
            Vector <Answer> answers = new Vector<Answer>(question.getAnswers());
            String answersText = "<html><ul>";
            
            
            for (j = 0; j < answers.size(); j++) {
                answersText += "<li ";
                if (answers.elementAt(j).isCorrect() == true){
                    answersText +=  "type=circle> correct    :";
                }
                else answersText += "type=disc> incorrect :";
                answersText += answers.elementAt(j).getText();
                answersText += "<br>";
            }
            answersText += "</ul></html>>";
            JPanel p = new JPanel();
            p.add(new JLabel(question.getText()));
            JLabel l = new JLabel();
            l.setText(answersText);
            p.add(l);//new JLabel().setText(answersText));            
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            
            
            questionPanels.add(p);
            
            
            listModel.addElement(p);
            
            
            System.out.println("answers:" + answersText);
            qPanel.add(p);
            
            i++;
        }
        MyCellRenderer cr = new MyCellRenderer();
        questionsList.setCellRenderer(cr);
        questionsList.setListData(questionPanels);
        //questionsList.setModel(listModel);
        JScrollPane listPanel = new JScrollPane(questionsList);
        panel1.add(new JLabel("Question Set : " + qsName));
        
      
                     
        buttonsPanel.add(addButton);             
        buttonsPanel.add(editButton);        
        buttonsPanel.add(deleteButton);        
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        panel.add(panel1);  
        panel.add(buttonsPanel);
        //panel.add(new JScrollPane(qPanel));
        panel.add(listPanel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        
        /* 
         * add buttons' listeners
         */
        
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                createQuestion();
            }
        });
       
        //  buttonsPanel.setLayout(new BoxLayout( buttonsPanel, BoxLayout.Y_AXIS));
        tabbedPane.addTab("QS",  panel);
 
    
    }

    private void createQuestion() {
        
        try{    
            QuestionSet questionSet = new QuestionSet();
            
            QuestionTabAdd qt = new QuestionTabAdd(tabbedPane, questionSet);
            qt.initComponents();
        
         }catch (Exception e){}
    }
}

class MyCellRenderer extends JPanel implements ListCellRenderer {
    
    
    public MyCellRenderer() {
  
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object label,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        
        setBorder(BorderFactory.createTitledBorder(""));
        add((JPanel)label);

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
            background = Color.BLUE;
            foreground = Color.WHITE;

        // unselected, and not the DnD drop location
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        };

        setBackground(background);
        setForeground(foreground);

        return this;
    }
}
