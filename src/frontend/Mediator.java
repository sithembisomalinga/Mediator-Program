package frontend;

import javax.swing.ImageIcon;

/**
 * This class basically runs the program as a main method.
 *
 * @author SITHEMBISO MALINGA
 */
public class Mediator {

    public static void main(String[] args) {   
        MainUI home = new MainUI();
        home.setVisible(true);
        home.setIconImage(new ImageIcon("src/images/mediator_logo.png").getImage());
        home.startUpHints();
    }
    
    /*
    ASSORTED COMMENTS
    Most of the brief I was able to keep the same, except for :
        -Removal of Expand Table completely in favor of ToolTips
          (when hovering your mouse over a table entry, it shows additional information)
        -Removal of importing due to constraints of how data can be added
        -Addition of "Manual Table Refresh" under File
        -Exporting is reworked to export the table's content.
        -Settings Options instead enables a single option
        -Database updateConnections has two extra paramters to actually work
        -jTable revamped to fit closer to my original vision of image and progress bars.
    These are some decent substantial changes done to make sure the program is better,
    but since these are deviations from Phase 2 Specification, marks are going to be lost.
    
    On Data Validation, every error SHOULD be caught either with a function or JOptionPane.
    Emphasis on should because there is a high chance i missed some and it just crashes the program
    
    One issue is that the program is slow. At this scale it is neglicable but with large
    amounts of data the differnece will be noteworthy. This is due to the CellRenderers, which
    creates a new component for EVERY row, which chugs performance.
    */
}
