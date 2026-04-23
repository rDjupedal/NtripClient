package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main_GUI {

    private static WindowFrame windowFrame;
    private static Controller controller;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main_GUI::showGUI);
    }

    private static void showGUI() {

        windowFrame = new WindowFrame();

        windowFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.exit();
                windowFrame.dispose();
                System.exit(0);
            }
        });



        controller = new Controller(windowFrame);
        windowFrame.setVisible(true);
    }
}
