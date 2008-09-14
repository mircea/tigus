
package org.tigus.app.editor;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
   
    JSplitPane splitPane;
    JScrollPane listScrollPane;
    JScrollPane optionScrollPane;
    JList optionsList;
    DefaultListModel listModel;
    
    int index = -1;
    String author = "";
    PreferencesWindow(){
        
        super("Preferences");
 
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
    public void initComponents(){
        optionScrollPane = new JScrollPane();
        listModel = new DefaultListModel();
       
        
       
        
        listModel.addElement("<html><ul><li type=square> Author </ul></html>");
        optionsList = new JList();
        optionsList.setModel(listModel);
        listScrollPane = new JScrollPane(optionsList);
        addListeners(); 
        
         //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        listScrollPane.setMinimumSize(minimumSize);
        optionScrollPane.setMinimumSize(minimumSize);
        //showLoginPanel();
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
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                
                
                
            }
        });
    }
    private void showAuthorPanel() throws IOException{
            
       /*
        *   read editor.conf file for extarcting the author's name 
        */
        byte []bytes;
        final boolean empty; //true if there is no author name saved
        RandomAccessFile configFile = new RandomAccessFile("editor.conf", "rw");
        bytes= new byte[(int)(configFile.length())];
        final int beginIndex;
        final int endIndex;
        configFile.readFully(bytes);
        configFile.close(); 
        
        String s = new String(bytes);
        final StringBuffer buffer = new StringBuffer(s);
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
                return;
            }
                
            author = s.substring(beginIndex, endIndex).trim();
        }         
       
        
        final JPanel panel = new JPanel();
        JPanel horizontalPanel = new JPanel();
        JLabel nameLabel = new JLabel("Autor");
        final JTextField nameTextField = new JTextField();
        JButton okButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
        
        nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);  
        nameTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);        
        nameTextField.setMinimumSize(new Dimension(100,30));
        nameTextField.setPreferredSize(new Dimension(300,30));
        nameTextField.setMaximumSize(new Dimension(800,30));
        
        horizontalPanel.add(okButton);
        horizontalPanel.add(cancelButton);
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);        
        
        
        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(Box.createRigidArea(new Dimension(0,40)));
        panel.add(horizontalPanel);
        
        panel.setBorder(BorderFactory.createTitledBorder("Author's name"));
        
        nameTextField.setText(author);
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String name = nameTextField.getText();
               // frame closes if the user clicked apply but there are no changes to be made
               if (name.length() == 0 || name.equals(author)) {
                   setVisible( false );
                   dispose();
               }
               if (name.equals("AUTOR")) {
                   JOptionPane.showMessageDialog(panel,
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
                       if(name.length() < author.length()){
                           int difference = (author.length() - name.length())+1;
                           for(int i=0; i < difference; i++){
                           raf.writeBytes(" ");
                           }
                       }
                       raf.close();
                    }catch(Exception ex) {
                       ex.printStackTrace();
                   }
               }
        
               setVisible( false );
               dispose();
               
             
           }
           
        });
      

        
        
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        splitPane.setRightComponent(panel);
     
    }
   
}
