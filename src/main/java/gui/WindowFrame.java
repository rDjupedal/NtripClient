package gui;

import core.Constants;

import javax.swing.*;
import java.awt.*;

public class WindowFrame extends JFrame {

    private final NetworkPane networkPane = new NetworkPane();
    private final OutputPane outputPane = new OutputPane();

    public WindowFrame() {
        super(Constants.PROGRAM_NAME);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // center
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;    // Only take up enough vertical space to fit the components
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);
        add(networkPane, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        add(outputPane, gbc);

    }

    protected NetworkPane getNetworkPane() { return this.networkPane; }
    protected OutputPane getOutputPane() { return this.outputPane; }



}
