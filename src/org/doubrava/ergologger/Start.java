package org.doubrava.ergologger;

import org.doubrava.ergologger.ui.FormErgoLogger;

import javax.swing.JFrame;

public class Start {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frmMain = new JFrame("ErgoLogger");
        frmMain.setContentPane(new FormErgoLogger().getMainPanel());
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frmMain.pack();
        frmMain.setVisible(true);
    }

    // Test for change
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Start.createAndShowGUI();
            }
        });

    }

}
