package org.tigus.app.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import org.tigus.core.*;

/**
 * Creates a tab to display the questions from a QuestionSet object
 * @author Adriana Draghici
 *
 */
public class QuestionSetTab {
    
    
    MainWindow mainWindow;
    JTabbedPane tabbedPane;
    QuestionSet questionSet;
    String qsName;
    int listIndex;
    Vector <Question> questions = new Vector<Question>();
    Vector <JPanel> questionPanels = new Vector<JPanel>();
    Vector <TagSet> tags = new Vector<TagSet>();
    
    
    JList questionsList = new JList();    
    JButton addButton = new JButton("Add question");
    JButton editButton = new JButton("Edit Question");
    JButton deleteButton = new JButton("Delete question");    
    
    JPanel mainPanel = new JPanel();
    DefaultListModel listModel = new DefaultListModel();
 
    
    /**
     * Constructor
     * @param tabbedPane - the tabbed pane in which to add this classes's panel
     * @param qs - the QuestionSet object to be displayed
     * @param qsName - the question set's name
     */
    public QuestionSetTab(MainWindow mainWindow, 
                            QuestionSet qs, 
                            String qsName) {
        this.mainWindow = mainWindow;
        tabbedPane = mainWindow.getTabbedPane();
        questionSet = qs;
        this.qsName = qsName;
        listIndex = -1;
        //initComponents();
        
    }
    
   
    /**
     * Updates the objects that keep the questions, including the JList objects that displays them
     * @param op - the change made to the question set. Values : "ADD" , "EDIT", "DEL"
     * @param question - the Question object to be added, changed or deleted
     */
    public void updateQuestionsList(String op, Question question) {
        if (op.equals("ADD")) {
            questions.add(question);
            JPanel panel = createQuestionPanel(question);
            questionPanels.add(panel);  
            listModel.addElement(panel);
            
            
            return;
        }
        
        if (op.equals("EDIT")) {
            
            int index = questions.indexOf(question);
            System.out.println("la EDIT index = " + index);
            questions.setElementAt(question, index);
            
            JPanel panel = createQuestionPanel(question);          
      
            questionPanels.setElementAt(panel, index);
            listModel.setElementAt(panel, index);
            
            return;
        }
        
        if (op.equals("DEL")) {
            int index = questions.indexOf(question);
            questions.removeElementAt(index);
            questionPanels. removeElementAt(index);
           
            System.out.println("listModel.indexOf(question) = " + index);
            listModel.removeElementAt(index);            
        }
        
        // announces the main window that unsaved changes were made
        mainWindow.questionSetChanged();
    }
    /**
     * Creates a panel showing the question's text and it's answers.
     * @param question
     * @return JPanel object 
     */
    private JPanel createQuestionPanel(Question question) {
        
        // get answers
        Vector <Answer> answers = new Vector<Answer>(question.getAnswers());
        String answersText = "<html><ul>";            
        
        for (int j = 0; j < answers.size(); j++) {
            answersText += "<li ";
            if (answers.elementAt(j).isCorrect() == true){
                answersText +=  "type=circle> correct    : ";
            }
            else answersText += "type=disc> incorrect   : ";
            answersText += answers.elementAt(j).getText();
            answersText += "<br>";
        }
        answersText += "</ul></html>>";
        
        System.out.println("answers:" + answersText);    
        // get tags
        TagSet tagSet = question.getTags(); 
        tags.addElement(tagSet);
        
        /*
        String tagsNames = "<html><b>tags: <b>";
        
        if (!tagSet.isEmpty()) {
            
            Set <String> keys = tagSet.keySet();
        
            for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
               String tagName = new String(it.next());
               
               tagsNames += tagName;
               if(it.hasNext())
                   tagsNames += ", ";
                   
            }
        }*/
        // create question's panel
        
        JPanel p = new JPanel();
        p.add(new JLabel(question.getText()));
   
        p.add(new JLabel(answersText));    
        p.add(new JLabel(showTags(tagSet)));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    
    }
    /**
     * Initializes the objects containing the questions 
     * @params none
     * @return none
     */
    private void createQuestionsList() {
        /*
         * create panels for each question 
         * !!! not yet finished, it can show correctly only a QS with one question
         */
       
        int qsSize = questionSet.size();
        System.out.println("question set size:" + qsSize);
  
        int i = 0;    
        
        for (Iterator <Question> it = questionSet.iterator(); it.hasNext(); ) {
            
            Question question = it.next();
            System.out.println("question Text:" + question.getText());
            
            questions.add(question);
            
            JPanel p = createQuestionPanel(question);   
            
            questionPanels.add(p);
            listModel.addElement(p);      
            
            i++;
        }
        
        MyCellRenderer cr = new MyCellRenderer();
        questionsList.setCellRenderer(cr);
       // questionsList.setListData(questionPanels);
        questionsList.setModel(listModel);
    }
    /**
     * Shows the selected question's tags in the panel's combo box
     * @param index - item selected from the list
     */
    private String showTags(TagSet tagSet) {
        // get tags

        Set <String> keys = tagSet.keySet();
        String text = "<html><DL><DT> Tags: <br>";
        // insert tags' names into comboBox
        
        for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
           String tagName = new String(it.next());
           text += "<DD>";
           text += tagName;
           text += ": ";
           Vector <String> values = new Vector<String>(tagSet.get(tagName));
           text += values.elementAt(0);
           for (int i = 1; i < values.size(); i++){                    
               text += ", ";
               text += values.elementAt(i);
           }
           text += "<br>";
          
        } 
        text += "</DL> </html>";
        
        return text;    
    }
    
   
    /**
     * Initializes the GUI components
     * @param none
     * @return none
     * 
     */
    public void initComponents() {   
        
        tabbedPane.repaint();
      
        
        /* create JList object for displaying questions*/
        createQuestionsList();
        /* set layout */
        mainPanel = setLayout();
        /* add listeners*/
        addListeners();
        /* add panel to tabbedpane*/
        tabbedPane.addTab("QS",  mainPanel);
        
    } 
    
    /**
     * Set layout
     * @param none
     * @return JPanel object
     */
    private JPanel setLayout() {
        JPanel panel  = new JPanel();
        JPanel buttonsPanel  = new JPanel(); 
        JScrollPane listPanel = new JScrollPane(questionsList);
        
        buttonsPanel.add(addButton);             
        buttonsPanel.add(editButton);        
        buttonsPanel.add(deleteButton);        
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));        
             
        panel.add(buttonsPanel);
        panel.add(listPanel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        return panel;
    }
    /** 
     * Add buttons' listeners
     * @param none
     * @param none
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
                deleteQuestion();
            }
        });
        
        
        questionsList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int index = questionsList.getSelectedIndex();
                if(index == listIndex) {
                    return;
                }
                listIndex = index;
             //   showTags(index);
             
            }
        });
    }
       
    /**
     * Creates a QuestionTabAdd object for adding a new question
     * @param none
     * @retun none
     */
    private void createQuestion() {
        
        try{                
            Question question = new Question();
            //QuestionTabAdd qt = new QuestionTabAdd(this, tabbedPane, questionSet, qsName);
            QuestionTab qt = new QuestionTab("NewQ", this, tabbedPane,
                                                    question, questionSet, qsName);
            qt.initComponents();
        
         }catch (Exception e){}
    }
    /**
     * Creates a QuestionTabEdit object for editing a selected question
     * @param none
     * @retun none
     */
    private void editQuestion() {
        try{ 
            // 
            // testing QuestionTabEdit 
            //
            int index = questionsList.getSelectedIndex();
            System.out.println("index = "+index);
            Question question = questions.elementAt(index);
            QuestionTab qt = new QuestionTab("EditQ", this, tabbedPane, 
                                                    question, questionSet, qsName);
            qt.initComponents();
           
            
        }catch (Exception e) {}
        
    }
    /**
     * Removes the question selected from question set
     * @param none
     * @retun none
     */
    private void deleteQuestion() {
        int index = questionsList.getSelectedIndex();
        System.out.println("index = "+index);
        Question question = questions.elementAt(index);
        questionSet.remove(question);
        updateQuestionsList("DEL", question);
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
