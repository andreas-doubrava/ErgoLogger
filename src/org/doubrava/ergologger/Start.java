package org.doubrava.ergologger;

import org.doubrava.ergologger.ui.FormErgoLogger;

import javax.swing.JFrame;

public class Start {

    /**
     * 0.1  Original version from 2020-04
     * 0.2  Fix DataSet (NullPointerException on start recording data)
     * 0.3  Fix DataSet.setActivityType
     *      Fix TCX-Export: ExportEngine =>add Element <Author>...</Author>
     */
    public final static String ergologgerVersion = "0.3";

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frmMain = new JFrame("ErgoLogger " + Start.ergologgerVersion);
        frmMain.setContentPane(new FormErgoLogger().getMainPanel());
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frmMain.pack();
        frmMain.setVisible(true);
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(Start::createAndShowGUI);

    }

}
