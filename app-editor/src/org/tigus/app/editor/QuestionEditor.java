package org.tigus.app.editor;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.*;
;
/**
 * Main class for the  Question Editor GUI application
 * 
 * @author Adriana Draghici
 * 
 */
public class QuestionEditor {

    /**
     * @param args
     */
    public static void main(String[] args) {
       System.out.println("Tigus");
       modifyLookAndFeel();
       new MainWindow();
       
    }
    /**
     * Determine if the system's installed look and feel includes MetalLookAndFeel 
     */
    private static boolean installedLookAndFeel() {
        UIManager.LookAndFeelInfo laf[] = UIManager.getInstalledLookAndFeels();
       
        for (int i = 0, n = laf.length; i < n; i++) {
          System.out.print("LAF Name: " + laf[i].getName()+"\t");
          if (laf[i].getName() == "GTK+")
              return true;
        }
        return false;
    }
    /**
     * Sets the look and feel to Metal if it is installed in the system
     */
    private static void modifyLookAndFeel() {
        if (installedLookAndFeel() == true) {
   
            String laf = "javax.swing.plaf.metal.MetalLookAndFeel";
            try {
                UIManager.setLookAndFeel(laf);
            }
            catch (Exception ex) {
                System.out.println( "Exception : " + ex.toString());
            }
            //SwingUtilities.updateComponentTreeUI(this);
    
        }
    }

}
 
