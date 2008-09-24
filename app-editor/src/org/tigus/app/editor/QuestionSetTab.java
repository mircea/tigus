package org.tigus.app.editor;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;

import org.tigus.core.*;

/**
 * Creates a tab to display the questions from a QuestionSet object
 * @author Adriana Draghici
 *
 */
public class QuestionSetTab implements ActionListener{    
    
    MainWindow mainWindow;
    JTabbedPane tabbedPane;
    QuestionSet questionSet;
    String qsName;    
    
    int listIndex;    
    Boolean hasPopup = false; // true if popupMenu is enabled
    
    Vector <Question> questions = new Vector<Question>();
    Vector <JPanel> questionPanels = new Vector<JPanel>();   
    Vector<String> words = new Vector<String>(); // keeps words used for filtering
    Vector<JMenuItem> menuItems = new Vector<JMenuItem>(); // menuItems for popupMenu
    
    HashMap <String,Vector<Question>> questionsTags = new HashMap <String, Vector<Question>>();
    
    DropTarget dt;
    DragSource ds;
    
    /* GUI components   */
    
    JPopupMenu popupMenu = new JPopupMenu();
    JList questionsList = new JList();    
    JButton addButton = new JButton("Add question");
    JButton editButton = new JButton("Edit Question");
    JButton reviewButton = new JButton("Review Question");
    JButton deleteButton = new JButton("Delete question");
    JButton viewReviewsButton = new JButton("Question's reviews");
    
    
    JLabel label1 = new JLabel("Filer criteria:");
    JLabel label2 = new JLabel("Tags:");
    JComboBox typeComboBox = new JComboBox();
    JComboBox tagsComboBox = new JComboBox();
    JTextField filterTextField = new JTextField();
    JButton filterButton  = new JButton("Filter");
    
    JPanel mainPanel = new JPanel();
    JPanel filterPanel = new JPanel();
    DefaultListModel listModel = new DefaultListModel();
    DefaultListModel filteredListModel = new DefaultListModel();
    DefaultComboBoxModel typeCBModel = new DefaultComboBoxModel();
    DefaultComboBoxModel tagsCBModel = new DefaultComboBoxModel();
    final String []filterCriterias = {"Text", "Tag", "Tag and Value"};
   
    /**
     * Constructor
     * @param mainWindow - the MainWindow in which to add this tab
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
        initComponents();
        
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
            
            if(questions.size() == 1) {
                filterPanel.setVisible(true);
            }
            mainWindow.questionSetChanged();
            return;
        }
        
        if (op.equals("EDIT")) {
            
            int index = questions.indexOf(question);
            System.out.println("la EDIT index = " + index);
            questions.setElementAt(question, index);
            
            JPanel panel = createQuestionPanel(question);          
      
            questionPanels.setElementAt(panel, index);
            listModel.setElementAt(panel, index);
            mainWindow.questionSetChanged();
            return;
        }
        
        if (op.equals("DEL")) {
            int index = questions.indexOf(question);
            questions.removeElementAt(index);
            questionPanels. removeElementAt(index);
           
            System.out.println("listModel.indexOf(question) = " + index);
            listModel.removeElementAt(index);
            
            if(questions.size() == 0) {
                filterPanel.setVisible(false);
            }
            
            // notifies the main window that unsaved changes were made
            mainWindow.questionSetChanged();
        }
        
        
    }
    
    public void enablePopupMenu(Boolean b) {      
        if(!hasPopup && b) {
            System.out.println("enable");
            initPopupMenu();
            filterTextField.add(popupMenu);
            filterTextField.addMouseListener(new MouseAdapter() {          
                public void mouseClicked(MouseEvent evt) {               
                        popupMenu.show(evt.getComponent(), 0, evt.getComponent().getHeight());             
                }          
            });
            hasPopup = b;
            return;
        }
        
        popupMenu = new JPopupMenu();
        popupMenu.setEnabled(false);
        words = new Vector<String>();
        hasPopup = b; 
    }
    
    public void clearPopupMenu() {
        popupMenu = new JPopupMenu();
        words = new Vector<String>();
    }
    
    public Question getQuestion(int index) {
        return questions.elementAt(index);
    }
    
    public QuestionSet getQuestionSet() {
        return questionSet;
    }
    public String getQuestionSetName() {
        return qsName;
    }
    
    public Vector<String> getFilteringWords() {
        return words;
    }
    /**
     * Returns the author's name, as it is saved in the application's configuration file
     * @return String object representing the author's name
     */
    public String getAuthor() {
        String name = new String(mainWindow.getAuthor());
        return name;
    }
    
    /**
     * Returns the author's name as it is saved in the question's "author" tag
     * @param question The question selected from the question set
     * @return String object representing the author's name
     */
    public String getAuthorTagValue(Question question) {
        
        TagSet tagSet = question.getTags();
        Set <String> keys = tagSet.keySet();
        
        for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
           String tagName = new String(it.next());
           if(tagName.toLowerCase().equals("author")) {
               
               Vector <String> values = new Vector<String>(tagSet.get(tagName));
               
               return values.elementAt(0);
           }
        }
        return "";               
    }
    /**
     * Creates a panel showing the question's text and it's answers.
     * @param question
     * @return JPanel object 
     */
    private JPanel createQuestionPanel(Question question) {
        
        /* get answers  */
        Vector <Answer> answers = new Vector<Answer>(question.getAnswers());
        String answersText = "<html><ul>";            
        
        for (Answer answer : answers) {
            answersText += "<li ";
            if (answer.isCorrect() == true){
                answersText +=  "type=circle> correct    : ";
            }
            else answersText += "type=disc> incorrect   : ";
            answersText += answer.getText();
            answersText += "<br>";
        }
        answersText += "</ul></html>>";
        
        System.out.println("answers:" + answersText);  
        
       
        /* create question's panel  */
        
        JPanel p = new JPanel();
        p.add(new JLabel(question.getText()));
   
        p.add(new JLabel(answersText));    
        p.add(new JLabel(showTags(question)));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;    
    }
    
    /**
     * Initializes the objects containing the questions  
     */
    private void createQuestionsList() {
        
        /* create panels for each question  */
        
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
    }
    
    /**
     * Builds a html string containing a question's tags and their values, and
     * creates a hashmap object with tag's name+value as key, and the question as it's value
     * @param tagSet - the question's set of tags
     * @return String object represintg tags and their values
     */
    private String showTags(Question question) {
        
        /* get tags */
        TagSet tagSet = question.getTags();
        Set <String> keys = tagSet.keySet();
        String text = "<html><DL><DT> Tags: <br>";       
        
        for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
           String tagName = new String(it.next());
           
           /* add to a HashMap object the "tag_name" as a key 
           and append the question it's value */
           Vector <Question> v = questionsTags.get(tagName);
           if(v == null) {
               v = new Vector<Question>();
           }
           // avoid duplicates
           if(!v.contains(question)) {
               v.addElement(question);
               System.out.println("aici la adaugarea in hashMap");
               questionsTags.put(tagName, v);
           }
            
           /* builds the string as tagName: values */
           text += "<DD>";
           text += tagName;
           text += ": ";
           Vector <String> values = new Vector<String>(tagSet.get(tagName));
           Boolean first  = true; // used for identifying first value 
           for (String val : values){
               if(!first) 
                   text += ", ";             
               else
                   first = false;
               
               text += val;               
               
               /* add to a HashMap object the "tag_name+value" as a key 
                  and append the question it's value */
               Vector <Question>vs = questionsTags.get(tagName+"+"+val);
               if(vs == null) {
                   vs = new Vector<Question>();
               }
               // avoid duplicates
               if(!vs.contains(question)) {
                   vs.addElement(question);               
                   questionsTags.put(tagName+"+"+val, vs);
               }
           }
           text += "<br>";          
        } 
        text += "</DL> </html>";
        
        return text;    
    }
    
   
    
     /**
     * Initializes GUI components of this tab
     */
    private void initComponents() {   
        
        tabbedPane.repaint();
      
        
        /* create JList object for displaying questions*/
        createQuestionsList();
        MyCellRenderer cr = new MyCellRenderer();
        questionsList.setCellRenderer(cr);
        questionsList.setModel(listModel);
        
        configureDnD();
        if(questions.size() == 0) {
            filterPanel.setVisible(false);
        }
   
        initComboBoxes();       
        
        /* configure popup menu*/
        enablePopupMenu(hasPopup);      
        
        
        /* set components' size*/
        setSize();
        
        /* set layout */
        mainPanel = setLayout();
        
        /* add listeners*/
        addListeners();
        
        /* add panel to tabbedpane*/
        tabbedPane.addTab("   QS   ",  mainPanel);
        
    } 
    
    /**
     *  Enable and configure Drag And Drop in/from the questions list
     */
    private void configureDnD() {
        questionsList.setDragEnabled(true);
        questionsList.setDropMode(DropMode.INSERT);
        questionsList.setTransferHandler(new ListTransferHandler(this));
    } 
    
    private void initComboBoxes() {
        
        for (int i = 0; i < filterCriterias.length ; i++) {
            typeCBModel.addElement(filterCriterias[i]);
        }
            
        typeComboBox.setModel(typeCBModel);
        typeComboBox.setEditable(false);
        typeComboBox.setSelectedItem(0);
        
        tagsCBModel.addElement("author");
        tagsCBModel.addElement("difficulty");
        tagsCBModel.addElement("chapter");
        tagsCBModel.addElement("Other tag...");
        tagsComboBox.setModel(tagsCBModel);
       // tagsComboBox.setEditable(true);    
        tagsComboBox.setEnabled(false);
    }
    
    private void initPopupMenu() {
        try {
            RandomAccessFile raf = new RandomAccessFile("filterwords.txt", "rw");
            
            String line = "";     
            
            while((line = raf.readLine() )!= null) {          
                words.add(line.trim());               
            }
            
            Collections.sort(words, String.CASE_INSENSITIVE_ORDER);  
           
            for (String word : words) {
                JMenuItem item = new JMenuItem(word);
                item.addActionListener(this);
                menuItems.add(item);
                popupMenu.add(item);                
            }
            
            raf.close();
            
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void setSize() {
        typeComboBox.setPreferredSize(new Dimension(60,25));
        typeComboBox.setMaximumSize(new Dimension(60,25));
        tagsComboBox.setPreferredSize(new Dimension(60,25));
        tagsComboBox.setMaximumSize(new Dimension(60,25));
        filterTextField.setPreferredSize(new Dimension(60,25));
        filterTextField.setMaximumSize(new Dimension(60,25));
        filterButton.setPreferredSize(new Dimension(60,25));
        filterButton.setMaximumSize(new Dimension(60,25));  
        label1.setPreferredSize(new Dimension(60,25));
        label1.setMaximumSize(new Dimension(60,25));     
        label2.setPreferredSize(new Dimension(60,25));
        label2.setMaximumSize(new Dimension(60,25)); 
    }
    
    /**
     * Sets layout
     * @return JPanel object
     */
    private JPanel setLayout() {
        JPanel panel  = new JPanel();
        JPanel buttonsPanel  = new JPanel(); 
        JScrollPane listPanel = new JScrollPane(questionsList);
        
        buttonsPanel.add(addButton);             
        buttonsPanel.add(editButton); 
        buttonsPanel.add(reviewButton); 
        buttonsPanel.add(deleteButton); 
        buttonsPanel.add(viewReviewsButton); 
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));        
        
        filterPanel.setLayout(new GridLayout(2,4,2,0)); // 2 rows, 4 columns,
                                                        // 2 - size of horizontal gap, 
                                                        // 0 - size of vertical gap
        filterPanel.add(label1);
        filterPanel.add(label2);      
        filterPanel.add(new JLabel(" "));
        filterPanel.add(new JLabel(" "));
        filterPanel.add(typeComboBox);
        filterPanel.add(tagsComboBox);
        filterPanel.add(filterTextField);
        filterPanel.add(filterButton);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter questions"));          
      
        filterPanel.setPreferredSize(new Dimension(610, 75));
        filterPanel.setMaximumSize(new Dimension(610, 75));
        
        panel.add(buttonsPanel);    
        panel.add(filterPanel);
        panel.add(listPanel);
     
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        return panel;
    }
    
    /** 
     * Add buttons' listeners     
     */
    private void addListeners() {
        addButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)  {
                createQuestion();
            }
        });
        
        editButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                editQuestion();
            }
        });
        reviewButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                reviewQuestion();
            }
        });
        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                deleteQuestion();
            }
        });
        viewReviewsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                viewReviews();
            }
        });
        filterButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                filterQuestions();
                tagsComboBox.setEditable(false);
            }
        });
        
        typeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* When filter after Text option is selected, there is no need for
                 * tagsComboBox, therefore this component is disabled
                 * When filter after Tag option is enabled there is no need for
                 * the textField object, therefore this component is disabled
                 */
                String type = (String)typeComboBox.getSelectedItem();
                if(type.equals(filterCriterias[0])) { 
                    tagsComboBox.setEnabled(false);
                    filterTextField.setEnabled(true);
                }
                else {
                    if(type.equals(filterCriterias[1])) {
                        filterTextField.setText("");
                        filterTextField.setEnabled(false);
                    }                    
                    tagsComboBox.setEnabled(true);
                    filterTextField.setEnabled(true);
                }
            }
        });
        
        tagsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String)tagsComboBox.getSelectedItem();
                if(s.equals("Other tag...")) {
                    tagsComboBox.setEditable(true);
                    tagsComboBox.setSelectedItem("");
                }                
            }
        });
        questionsList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                int index = questionsList.getSelectedIndex();
                if(index == listIndex) {
                    return;
                }
                listIndex = index;
            }
        });
    }
    
    public void actionPerformed(ActionEvent e) {
        String command  = e.getActionCommand();
        System.out.println(command);
        if(words.contains(command)) {
            filterTextField.setText(command);
        }
    }
    
    /**
     * Creates a QuestionTab object for adding a new question
     */
    private void createQuestion() {
        
        try{      
            /*
              The application's user can not create new questions unless 
              she/he sets the author's name in "preferences" menu
            */
            String author = getAuthor();
            
            if (author.length() == 0) {
                return;
            }
            Question question = new Question();
            @SuppressWarnings("unused")
            QuestionTab qt = new QuestionTab("NewQ", this, tabbedPane, question);
        
         }catch (Exception e){}
    }
    
    /**
     * Creates a QuestionTab object for editing a selected question
     */
    private void editQuestion() {
        try{   
            
            int index = questionsList.getSelectedIndex();   
            if(index < 0) return;
            Question question = questions.elementAt(index);
            /*   
                The appplication's user can not edit a question unless 
                he/she is the question's author
            */
           
            if(!getAuthorTagValue(question).equals(getAuthor())) {
               JOptionPane.showMessageDialog(mainPanel,
                       "You can edit only your questions!", 
                           "error", JOptionPane.ERROR_MESSAGE);
               
               return;
            }
            
            @SuppressWarnings("unused")
            QuestionTab qt = new QuestionTab("EditQ", this, tabbedPane, question);
            
        }catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * Creates a "ReviewQuestionTab" object for reviewing the question
     */
    
    private void reviewQuestion() {
        int index = questionsList.getSelectedIndex();
        if(index < 0) return;
        Question question = questions.elementAt(index);
        /*   
            The appplication's user can not review his/her question 
         */
        System.out.println("Autor:" + getAuthor());
        if(getAuthorTagValue(question).equals(getAuthor())) {
           JOptionPane.showMessageDialog(mainPanel,
                   "You can not review your question!", 
                       "error", JOptionPane.ERROR_MESSAGE);
           
           return;
        }
        
        @SuppressWarnings("unused")
        ReviewQuestionTab rqTab = new ReviewQuestionTab(this, tabbedPane, question);
        
        
    }
    /**
     * Removes the question selected from question set
     */
    private void deleteQuestion() {
        int index = questionsList.getSelectedIndex();
        if(index < 0) return;
        Question question = questions.elementAt(index);
        
        /*   
            The appplication's user can not remove a question unless 
            he/she is the question's author
         */
   
        if(!getAuthorTagValue(question).equals(getAuthor())) {
           JOptionPane.showMessageDialog(mainPanel,
                   "You can edit only your questions!", 
                       "error", JOptionPane.ERROR_MESSAGE);
           
           return;
        }
        
        questionSet.remove(question);
        updateQuestionsList("DEL", question);
    }
    
    private void viewReviews() {
        int index = questionsList.getSelectedIndex();
        if(index < 0) return;
        Question question = questions.elementAt(index);
        Vector <Review> reviews = (Vector<Review>)question.getReviews();
        if(reviews.size() > 0) {
            System.out.println("Review:");
            System.out.println(reviews.elementAt(0).getAuthor());
            System.out.println(reviews.elementAt(0).getDate());
            System.out.println(reviews.elementAt(0).getComment());
            
            @SuppressWarnings("unused")
            ReviewsTab rTab = new ReviewsTab(question, tabbedPane);
            tabbedPane.add(" Reviews ", rTab);
            int tabIndex = tabbedPane.getTabCount() -1;
            tabbedPane.setSelectedIndex(tabIndex);
        }
        
    }
    /**
     * Makes a new listModel in which it adds the filtering results
     */
    private void filterQuestions() {
        String type = (String)typeComboBox.getSelectedItem();
        System.out.println("type = " + type);
        String text = filterTextField.getText();
        
        if(text.length() == 0) {
           questionsList.setModel(listModel);
        }  
        if(type.equals(filterCriterias[2])) { // Tag and Values 
            String tag = (String)tagsComboBox.getSelectedItem();
            if(tag.length() == 0) return;
            System.out.println("tag = " + tag);
            filterTags(tag, text);
            
        }
        if(type.equals(filterCriterias[1])) { // Tag
            String tag = (String)tagsComboBox.getSelectedItem();         
            filterTags(tag, "");
            filterTextField.setEnabled(true);
            
        }
        if(type.equals(filterCriterias[0])) { // Text
            filterText(text);
        }
        
        // update words' vector
        if ((text.length() != 0) && (!words.contains(text))) {
            words.add(text);
            
            // refresh the popup menu, sort the words
            popupMenu.removeAll();
            menuItems.removeAllElements();
            Collections.sort(words, String.CASE_INSENSITIVE_ORDER);
            
            for (String word : words) {
                JMenuItem item = new JMenuItem(word);
                item.addActionListener(this);
                menuItems.add(item);
                popupMenu.add(item);
            }
        }
        
    }
    
    private void filterText(String text) {
        if(!filteredListModel.isEmpty())
            filteredListModel.removeAllElements();
        for (Question question : questions) {   
            String questionText = question.getText();
            if(questionText.indexOf(text) != -1) {
                filteredListModel.addElement(createQuestionPanel(question));
            }            
        }
        questionsList.setModel(filteredListModel);        
    }
    
    private void filterTags(String tagName, String text) {
        Vector <Question> values;
        if(text.length() == 0) {
            values = questionsTags.get(tagName);
        }
        else {
            values = questionsTags.get(tagName+"+"+text);
        }
        if(values == null) {
            // when there are no results after filering, it is shown an empty list
            questionsList.setModel(new DefaultListModel());
            return;
        }
        if(!filteredListModel.isEmpty()) {           
            filteredListModel.removeAllElements();
        }
        Vector <Question> copy = new Vector<Question>(values);
        for(Question question : copy){ 
           filteredListModel.addElement(createQuestionPanel(question));
        }
        
        questionsList.setModel(filteredListModel);       
    }
    

}


class ListTransferHandler extends TransferHandler{
    QuestionSetTab qsTab;
    Question q;
    int index;
    private static final long serialVersionUID = 2L;
   
     ListTransferHandler(QuestionSetTab qsTab) {
         //super();
        this.qsTab = qsTab;       
    }
     
    public boolean canImport(TransferHandler.TransferSupport support) {
    
        System.out.println("aici in canImport");
        if (!support.isDrop()) {
            return false;
        }
       
        return true;
    }
    
    public boolean importData(TransferHandler.TransferSupport info) {
        System.out.println("aici in importData");
        if (!info.isDrop()) {
            return false;
        }
        
        String data;
        try {
            data = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (java.io.IOException e) {
            return false;
        }
        
        System.out.println(data);
        QuestionSet newQS = QuestionSet.createFromXML(data);

        QuestionSet oldQS = qsTab.getQuestionSet();
        for (Iterator <Question> it = newQS.iterator(); it.hasNext(); ) {
            
            Question question = it.next();
            oldQS.add(question);
            qsTab.updateQuestionsList("ADD", question);
        }
            
        return true;
    }
    
    public int getSourceActions(JComponent comp) {
        System.out.println("aici in getSourceActions");
        return COPY_OR_MOVE;
    }
    
    public Transferable createTransferable(JComponent comp) {
        JList list = (JList)comp;
        index = list.getSelectedIndex();
        if (index < 0 || index >= list.getModel().getSize()) {
            return null;
        }
        q = qsTab.getQuestion(index);
        QuestionSet newQS = new QuestionSet();
        newQS.add(q);
        String data = newQS.toXML();
        System.out.println("aici in createTransferable");
   
        return new StringSelection(data);
    }
    
    public void exportDone(JComponent comp, Transferable trans, int action) {
        System.out.println("aici in exportDone");
        if (action != MOVE) {
            return;
        }
        System.out.println("aici in exportDone");
        qsTab.updateQuestionsList("DEL", q);
    }
    
    
}
/*To Do : mircea@bardac.net: mda... cred ca se poate verifica daca sursa e aceeasi cu destinatia
mircea@bardac.net: si se poate ignora drag and drop-ul*/

class MyCellRenderer extends JPanel implements ListCellRenderer {

    private static final long serialVersionUID = 3L;
    
    public MyCellRenderer() {
  
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {        
   
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
