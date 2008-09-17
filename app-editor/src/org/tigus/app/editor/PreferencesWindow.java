
package org.tigus.app.editor;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.awt.*;
/**
 * 
 * @author Adriana Draghici
 *
 */

public class PreferencesWindow extends JFrame{
    
    private static final long serialVersionUID = 1L;
    
    QuestionSetTab qsTab;
    JSplitPane splitPane;
    JScrollPane listScrollPane;
    JScrollPane optionScrollPane;
    JList optionsList;
    DefaultListModel listModel;
    
    int index = -1;

    PreferencesWindow(QuestionSetTab qsTab){
        
        super("Preferences");
        this.qsTab = qsTab;
        initComponents();
        setSize(400,500);
        setVisible(true);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible( false );
                dispose();
            }
        });  
    }
    
    public void setQuestionTab(QuestionSetTab qsTab) {
        this.qsTab = qsTab;
        listModel.addElement("<html><ul><li type=square> Filter </ul></html>");
    }
    
    private void initComponents(){
        optionScrollPane = new JScrollPane();
        listModel = new DefaultListModel(); 
        listModel.addElement("<html><ul><li type=square> Author </ul></html>");
        if(qsTab != null)
            listModel.addElement("<html><ul><li type=square> Filter </ul></html>");
        optionsList = new JList();
        optionsList.setModel(listModel);
        listScrollPane = new JScrollPane(optionsList);
        addListeners(); 
        
         //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        listScrollPane.setMinimumSize(minimumSize);
        optionScrollPane.setMinimumSize(minimumSize);
       
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listScrollPane,
                                                                optionScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
        setContentPane(splitPane);
        
    }
    
    private void addListeners() {
        optionsList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int newindex = optionsList.getSelectedIndex();
                if(index == newindex)
                    return;
                index = newindex;
                if(index == 0) {                    
                    try {
                        showAuthorPanel();      
                        index = -1;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if(index == 1) { 
                    showFilterPanel();
                    index = -1;
                }
            }
        });
    }
   
    public void showDefaultPanel() {
        splitPane.setRightComponent(new JPanel());
    }
    
    private void showFilterPanel() {
        FilterPanel filterPanel = new FilterPanel(this, qsTab);
        splitPane.setRightComponent(filterPanel); 
    }
    
    private void showAuthorPanel() throws IOException{       
        AuthorPanel authorPanel = new AuthorPanel(this);
        splitPane.setRightComponent(authorPanel); 
    }
}


class AuthorPanel extends JPanel {
   
    private static final long serialVersionUID = 1L;

    PreferencesWindow preferancesWindow;
    
    JTextField nameTextField = new JTextField();
    JButton okButton = new JButton("Apply");
    JButton cancelButton = new JButton("Cancel");
    
    String configFile = "editor.conf";
    String authorName;
    boolean empty; //true if there is no author name saved
    int beginIndex; // position of author's name in the string in which the file is fully read
    int endIndex;
    StringBuffer buffer;
    
    AuthorPanel(PreferencesWindow preferancesWindow){
        this.preferancesWindow = preferancesWindow;
        authorName = getAuthorName();
        initComponents();
        addListeners();
    }
    /**
     *   read editor.conf file for extracting the author's name 
     *   @param a String representing the author name
     */
    private String getAuthorName() {
        
        byte []bytes;
        try {
        RandomAccessFile raf = new RandomAccessFile("editor.conf", "rw");
        bytes= new byte[(int)(raf.length())];
        
        raf.readFully(bytes);
        raf.close(); 
        String author = "";
        String s = new String(bytes);
        buffer = new StringBuffer(s);
        if (!s.contains("AUTHOR:")) {
            empty = true;
            beginIndex = 0;
            endIndex = 0;            
        }
        
        else {
            empty = false;
            beginIndex = s.indexOf("AUTHOR:") + 7;
            endIndex = s.indexOf("/AUTHOR"); 
            if(beginIndex < 0 || endIndex < 0) {
                System.err.println("wrong written file!");
                return "";
            }
                
            author = s.substring(beginIndex, endIndex).trim();
            return author;
        }     
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return "";
       
    }
    
    private void initComponents() {
        JPanel horizontalPanel = new JPanel();
        JLabel nameLabel = new JLabel("Autor");       
        
        nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);  
        nameTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);        
        nameTextField.setMinimumSize(new Dimension(100,30));
        nameTextField.setPreferredSize(new Dimension(300,30));
        nameTextField.setMaximumSize(new Dimension(800,30));
        
        horizontalPanel.add(okButton);
        horizontalPanel.add(cancelButton);
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);                
        
        add(nameLabel);
        add(nameTextField);
        add(Box.createRigidArea(new Dimension(0,40)));
        add(horizontalPanel);
        
        setBorder(BorderFactory.createTitledBorder("Author's name"));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        nameTextField.setText(authorName);
    }
    
    /**
     * saves the author's name to editor.conf file
     */  
    private void saveToFile() {
        String name = nameTextField.getText();
        // frame closes if the user clicked apply but there are no changes to be made
        if (name.length() == 0 || name.equals(authorName)) {
            return;
        }
        if (name.equals("AUTOR")) {
            JOptionPane.showMessageDialog(this,
                    "Use other name!", 
                        "", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (empty)
        {
            try {
                RandomAccessFile raf = new RandomAccessFile("editor.conf","rw");
            
                
                raf.writeBytes("AUTHOR:\n" + name + "\n/AUTHOR\n");
                raf.close();
                System.out.println("am inchis fisierul");
            }catch(Exception ex) {
                ex.printStackTrace();
            }
         }
        else {
            buffer.replace(beginIndex, endIndex, "\n"+name+"\n");
            try {
                RandomAccessFile raf = new RandomAccessFile("editor.conf", "rw");
                raf.writeBytes(buffer.toString());
                //if the replacingString has less number of characters than the matching string line then enter blank spaces.
                if(name.length() < authorName.length()){
                    int difference = (authorName.length() - name.length())+1;
                    for(int i=0; i < difference; i++){
                    raf.writeBytes(" ");
                    }
                }
                raf.close();
             }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
 
    }
    private void addListeners() {  
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {               
               saveToFile(); 
               preferancesWindow.showDefaultPanel();
           }           
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                preferancesWindow.showDefaultPanel();
            }           
         });
    } 
}


class FilterPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    JCheckBox checkBox1 = new JCheckBox("enable");
    JCheckBox checkBox2 = new JCheckBox("disable ");
    JCheckBox checkBox3 = new JCheckBox("Clear filtering archive");
    JButton okButton = new JButton("Ok");
    JButton cancelButton = new JButton("Cancel");    
   
    Boolean enable;
    Boolean clear;
    QuestionSetTab qsTab;
    PreferencesWindow preferancesWindow;
    FilterPanel(PreferencesWindow preferancesWindow, QuestionSetTab qsTab) {
        this.qsTab = qsTab;
        this.preferancesWindow = preferancesWindow;
        enable = false;
        clear = false;
       
        initComponents();
    }
    
    private void initComponents() {
        
        JPanel buttonsPanel = new JPanel();
        JPanel checkBoxPanel = new JPanel();
        checkBox1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);  
        checkBox2.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);    
    
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT); 
        
        checkBoxPanel.add(checkBox1);
        checkBoxPanel.add(checkBox2);
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.X_AXIS));
        checkBoxPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);   
        
        add(Box.createRigidArea(new Dimension(0,10)));
        
        
        add(new JLabel("Filter's Popup menu:"));
        add(checkBoxPanel);
        add(Box.createRigidArea(new Dimension(0,10)));
        add(checkBox3);
        add(Box.createRigidArea(new Dimension(0,40)));
        add(buttonsPanel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        addListeners();
    }
    
    private void addListeners() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(qsTab == null) return;
                
                if(enable) {
                     qsTab.enablePopupMenu(true);                  
                }
                else{
                     qsTab.enablePopupMenu(false);
                }
                
                if(clear) {
                     File f = new File("./filterwords.txt");
                     if(f.exists())
                         f.delete();
                     try {
                         f.createNewFile();
                     } catch(Exception ex) {
                         ex.printStackTrace();
                     }
                }
                preferancesWindow.showDefaultPanel();
            }
            
         });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                preferancesWindow.showDefaultPanel();
            }            
         });
        
        checkBox1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    enable = true;
                    checkBox2.setSelected(false);
                }
                else {
                    enable = false;                    
                }                
            }
        }) ;
        
        checkBox2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    enable = false;
                    checkBox1.setSelected(false);
                }
                else {
                    enable = true;                  
                }                
            }
        }) ;
        
        checkBox3.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   clear = true;
                }
                else {
                    clear = false;
                }
            }
        }) ;
    }
    
    
}
