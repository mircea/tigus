package org.tigus.app.editor;

import org.tigus.core.*;
import java.io.*;
import java.util.Vector;

import javax.swing.*;

import java.awt.event.*; 
import java.awt.*;

/**
 * Class for the main window of Question Editor GUI application
 * 
 * @author Adriana Draghici
 * 
 */


public class MainWindow implements ActionListener {
 
    /*
     * GUI components
     */
    JFrame frame;
    JMenuBar menuBar;
    JToolBar toolBar;
    JMenuItem []menuItems;
    JMenu fileMenu;
    JMenu questionMenu;
    JMenu toolsMenu;
    JButton []toolBarButtons;
    JTabbedPane tabbedPane;
 
    String author;  // the name of the person who uses this editor to create questions question sets
    Boolean empty ; // true if the no question set is loaded in the window
    Boolean untitled;   // true if the QS was created but saved.
    Boolean unsaved;    // true if the QS was modified but not saved
    String qsPath;  // the last used path for loading/saving question sets
    QuestionSet qs; // the question set loaded/created in this window
    QuestionSetTab qsTab; //the tab showing the question set 
    PreferencesWindow preferencesWindow;
    
    /*file names     */
    final String configFile = "configFile";
    final String wordsFile = "filterwords.txt";
     
    
    /**
     * Class Constructor
     */
    
    public MainWindow() {
       
        frame = new JFrame("Question Editor");
        // SwingUtilities.updateComponentTreeUI(this);
        frame.setLocation(50,50);
        frame.setPreferredSize(new Dimension(700,550));
     
        /* add components : menu, toolbar, tooltips, panel  */
        initComponents();
        
        
        author = getAuthor();
        untitled = true;
        unsaved = false;
        empty = true;
        qsPath = "";
        
     
        frame.setTitle("Untitled - Question Editor");
        frame.setVisible(true);
        //frame.setDefaultLookAndFeelDecorated (true);
        frame.pack();
        
        /* Add a window listener for close button */
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                showQuitDialog();
            }
        });        
    }
    
    
    
    /**
     * Initializes the frame's components: menus, toolbars and the tabbedpane as main panel  
     */
    
    private void initComponents() {
        int i;
        
        String []menuItemsNames = {"New", "Open...", "Save", "Save As...",
                                    "Import...", "Include", "Quit", 
                                    "Create", "Delete", "Review", "Move"};
        String []iconNames = {"images/newQS.png", "images/open.png", "images/save.png",
                                "images/saveas.png", "", "", "images/exit.png",
                                "images/create.png", "images/delete.png", "images/edit.png",
                                "images/switch.png"};                               
        
        /* create menus */
        
        menuItems = new JMenuItem[12];
        // menuItems 0->6  for fileMenu, menuItems 7->10 for questionMenu
        
        for (i = 0; i < 11; i++) {
            /* initialize the JMenuItems components with text and icons */
            menuItems[i] = new JMenuItem(menuItemsNames[i], new ImageIcon(iconNames[i]));   
            
            /* add listeners to the JMenuItems components */
            menuItems[i].addActionListener(this);
        }
        /* disable "Save"/ "Save as" until a question set is loaded or created. */
        menuItems[2].setEnabled(false);
        menuItems[3].setEnabled(false);
        
        /* add tooltips to the JMenuItems components */
        menuItems[0].setToolTipText("Create a new question set");
        menuItems[1].setToolTipText("Select a question set and load its content");
        menuItems[2].setToolTipText("Save the changes made to a question set");        
        
        menuItems[7].setToolTipText("Write a new question and save it to a question set");
        menuItems[8].setToolTipText("Delete a question");
        menuItems[9].setToolTipText("Review/edit a question and save the changes");
        
        menuItems[10].setToolTipText("Move questions between question sets");
        
        /*set menu items' accelerators and mnemonics */
        
        menuItems[0].setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItems[1].setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItems[2].setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItems[3].setAccelerator( KeyStroke.getKeyStroke("shift ctrl S") );
        
        menuItems[0].setMnemonic('N');
        menuItems[1].setMnemonic('O');
        menuItems[2].setMnemonic('S');
        menuItems[3].setMnemonic('A');
       
        fileMenu = new JMenu("File");
        questionMenu = new JMenu("Question");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        for (i = 0; i < 6; i++) {
            fileMenu.add(menuItems[i]);
        }
        
        fileMenu.addSeparator();
        fileMenu.add(menuItems[6]);
        
        for (i = 7; i < 11; i++) {
            questionMenu.add(menuItems[i]);
            
            //disable question menu items 
            //These items are enabled only when a question set is loaded or created
            menuItems[i].setEnabled(false);
        }
        JMenuItem preferencesMenuItem = new JMenuItem("Preferences");
        preferencesMenuItem.addActionListener(this);
        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        toolsMenu.add(preferencesMenuItem);
        
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
       // menuBar.add(questionMenu);
        menuBar.add(toolsMenu);
        
        frame.setJMenuBar(menuBar);   
        
        /* create toolbar */
        
        toolBar = new JToolBar();
        toolBarButtons = new JButton[7];
        
        for (i = 0; i < 3; i++) {
            toolBarButtons[i] = new JButton(new ImageIcon(iconNames[i]));
            toolBarButtons[i].setActionCommand(menuItemsNames[i]);
            toolBarButtons[i].addActionListener(this);
            toolBar.add(toolBarButtons[i]);
        }
        
        toolBar.addSeparator();
        
        for (i = 3; i < 7; i++) {
            toolBarButtons[i] = new JButton(new ImageIcon(iconNames[i+4]));
            toolBarButtons[i].setActionCommand(menuItemsNames[i+4]);
            toolBarButtons[i].addActionListener(this);
            toolBar.add(toolBarButtons[i]);
        }
        
        frame.add(toolBar, BorderLayout.PAGE_START);
        
        /* add main component: JTabbedPane */
        tabbedPane = new JTabbedPane();        
        
        tabbedPane.setPreferredSize(new Dimension(600,500));        

        frame.add(tabbedPane);
    }
    
    /**
     * Reads the author's name from the application's "configFile" file
     * @return if the author's name is unknows returns a string equal to "null" else returns a String with author's name.
     */
    public String getAuthor() {
        try {
            RandomAccessFile raf = new RandomAccessFile(configFile, "rw");   
          
            byte[] bytes= new byte[(int)(raf.length())];
            raf.readFully(bytes);
            raf.close(); 
            
            String s = new String(bytes);

            if (!s.contains("AUTHOR:")) {
                return "";
            }
            else {
                int  beginIndex = s.indexOf("AUTHOR:") + 7;
                int  endIndex = s.indexOf("/AUTHOR"); 
                if(beginIndex < 0 || endIndex < 0) {
                    System.err.println("wrong written file!");
                    return "";
                }
                    
                String name = s.substring(beginIndex, endIndex).trim();
                return name;
            }
        }catch(Exception ex) {
             ex.printStackTrace();
             return "";
        }
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    public void questionSetChanged() {
        unsaved = true;
    }
    
    /**
     * Implementation of actionPerformed method inherited from ActionListener interface
     * @param e  
     */
    
    public void actionPerformed(ActionEvent e) {
        
        String command  = e.getActionCommand();
        System.out.println(command);
        if (command.equals("Quit")) {
            showQuitDialog(); 
        }
        
        if (command.equals("New")) {
            if (empty == false) {
                MainWindow newWindow = new MainWindow();                
                newWindow.showQuestionSet(new QuestionSet(), "", "");
                return;
            }
            unsaved = true;
           
            showQuestionSet(new QuestionSet(), "", "");               
        }
        
        if (command.equals("Open...")) { 
            
            qs = new QuestionSet();
           
            JFileChooser fileChooser = new JFileChooser();
            int action = fileChooser.showOpenDialog(frame);
            
            if (action != JFileChooser.APPROVE_OPTION)
                return;
            File file = fileChooser.getSelectedFile();
            String path = file.getPath();
            String qsName = file.getName();     
            
            try { 
                qs.loadFromFile(path);
               
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
            
            if (empty == false) {
                MainWindow newWindow = new MainWindow();
                newWindow.showQuestionSet(qs, qsName, path);
                return;
            }
            
            showQuestionSet(qs, qsName, path);
            return;            
        }
        
        if (command.equals("Save") && untitled == false) {
            try {
                qs.saveToFile(qsPath);
                
            } catch(IOException ex) {
                ex.printStackTrace();
            }       
           
            unsaved = false;
            return;
        }    
        
        if(command.equals("Save As...") ||
                (command.equals("Save") && untitled == true)) {
            JFileChooser fileChooser;
            
            if(qsPath.length() == 0) {
                fileChooser = new JFileChooser();
            }
            else  {
                fileChooser = new JFileChooser(qsPath);
            }
            
            int action = fileChooser.showSaveDialog(frame);
            
            if (action != JFileChooser.APPROVE_OPTION)
                return;
            File file = fileChooser.getSelectedFile();
            qsPath = file.getPath();
            String qsName = file.getName();
            frame.setTitle(qsName + " - Question Editor");
            
            try {
                qs.saveToFile(qsPath);
                System.out.println("dupa save!");
            } catch(IOException ex) {
                ex.printStackTrace();
            }      
            
            untitled = false;
            unsaved = false;
            
            return;
        }
        
        if (command.equals("Preferences")) {                
            
             preferencesWindow = new PreferencesWindow(qsTab);
          
        }
        
        
    }
    
    /**
     * Method used in case of closing the window or selecting Quit from File menu.
     * It prompts a confirm dialog
     */
    private void showQuitDialog()
    {
        String msg = new String();
        if(unsaved) 
            msg = "There are unsaved changes! Exit anyway?";
        else msg = "Exit Question Editor?";
        
        int value = JOptionPane.showConfirmDialog(frame.getContentPane(),
                    msg, "exit", JOptionPane.YES_NO_OPTION);
       
        if (value == JOptionPane.YES_OPTION) {
            saveFilterWords();
            if(preferencesWindow != null) {
                preferencesWindow.setVisible(false);
                preferencesWindow.dispose();
            }
            frame.setVisible(false);
            frame.dispose();
        }
        
    }
    
    /**
     * Creates a tab for the question set loaded or created
     * @param qs    question set
     * @param name  question set's name
     * @param path  the file path qhere the question set is saved
     */
    private void showQuestionSet(QuestionSet qs, String name, String path)
    {
        if(name.length() > 0)
            frame.setTitle(name + " - Question Editor");
        this.qs = qs;
        qsPath = path;        
        
        qsTab = new QuestionSetTab(this, qs, name);
        /* There is an option that is enabled only when a questionSet is open, 
           therefore, the PreferencesWindow object needs to be notified */
        if(preferencesWindow != null) 
            preferencesWindow.setQuestionTab(qsTab);
        
        menuItems[7].setEnabled(true);
        menuItems[8].setEnabled(true);
        menuItems[9].setEnabled(true);
        
        empty = false;
        untitled = false;
        
        
        /* enable "Save" / "Save As" menu items */
        menuItems[2].setEnabled(true);
        menuItems[3].setEnabled(true);
    }  
    
    private void saveFilterWords() {
       if(empty)
           return;
       Vector <String> words= qsTab.getFileringWords();
       try {
           RandomAccessFile raf = new RandomAccessFile(wordsFile, "rw");
          // raf.seek(raf.length());
           for (String word : words) {
               raf.writeBytes(word+"\n");
           }
           raf.close();
       }catch(Exception ex) {
           ex.printStackTrace();   
       }
           
       
    }
  
}


