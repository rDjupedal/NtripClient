package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NetworkPane extends JPanel {
    private final JTextField serverTextField;
    private final JTextField portTextField;
    private final JTextField userTextField;
    private final JTextField passwordTextField;
    private final JTextField latTextField;
    private final JTextField lonTextField;
    private final JButton connectBtn, disconnectBtn, refreshBtn;
    private final JComboBox<String> mountpointCombo;
    private final JComboBox<String> comPortsCombo;

    public NetworkPane() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;

        // COLUMN 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;

        add(new JLabel("Server "), gbc);
        gbc.gridy++;
        add(new JLabel("Port "), gbc);
        gbc.gridy++;
        add(new JLabel("Mountpoint "), gbc);
        gbc.gridy++;
        add(new JLabel("Username "), gbc);
        gbc.gridy++;
        add(new JLabel("Password "), gbc);
        gbc.gridy++;
        add(new JLabel("Latitud "), gbc);
        gbc.gridy++;
        add(new JLabel("Longitud "), gbc);
        gbc.gridy++;
        add(new JLabel("Mata ut till serieport "), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectBtn = new JButton("Anslut"), gbc);

        // COLUMN 2
        gbc.gridx++;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(serverTextField = new JTextField(10), gbc);
        gbc.gridy++;
        add(portTextField = new JTextField(10), gbc);
        gbc.gridy++;

        JPanel mountPointPanel = new JPanel(new BorderLayout());
        mountPointPanel.add(mountpointCombo = new JComboBox<>(new String[]{"<TOM>"}), BorderLayout.CENTER);
        mountPointPanel.add(refreshBtn = new JButton("Uppdatera"), BorderLayout.EAST);
        add(mountPointPanel, gbc);

        gbc.gridy++;
        add(userTextField = new JTextField(10), gbc);
        gbc.gridy++;
        add(passwordTextField = new JTextField(10), gbc);
        gbc.gridy++;
        add(latTextField = new JTextField(10), gbc);
        gbc.gridy++;
        add(lonTextField = new JTextField(10), gbc);
        gbc.gridy++;
        add(comPortsCombo = new JComboBox<>(new String[]{"<INGEN>"}), gbc);

        gbc.gridy++;
        //gbc.fill = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        //gbc.anchor = GridBagConstraints.CENTER;
        add(disconnectBtn = new JButton("Koppla från"), gbc);
    }

    protected void setupListeners(ActionListener connBtnL, ActionListener disBtnL, ActionListener refreshBtnL) {
        connectBtn.addActionListener(connBtnL);
        disconnectBtn.addActionListener(disBtnL);
        refreshBtn.addActionListener(refreshBtnL);
    }

    protected void setConfig(NetworkConfig config) {
        serverTextField.setText(config.getServer());
        portTextField.setText(Integer.toString(config.getPort()));
        mountpointCombo.removeAllItems();
        mountpointCombo.addItem(config.getMountpoint());
        mountpointCombo.setSelectedIndex(0);
        userTextField.setText(config.getUsername());
        passwordTextField.setText(config.getPassword());
        latTextField.setText(Float.toString(config.getLat()));
        lonTextField.setText(Float.toString(config.getLon()));
    }

    protected NetworkConfig getConfig() {

        float lat = 0f, lon = 0f;
        int port = 0;

        try { lat = Float.parseFloat(latTextField.getText()); }
        catch (NumberFormatException e) { System.out.println("lat missing or illegal input"); }

        try { lon = Float.parseFloat(lonTextField.getText()); }
        catch (NumberFormatException e) { System.out.println("lon missing or illegal input"); }

        try { port = Integer.parseInt(portTextField.getText()); }
        catch (NumberFormatException e) { System.out.println("port missing or illegal input"); }


        NetworkConfig config = new NetworkConfig(
                serverTextField.getText(),
                port,
                (String) mountpointCombo.getSelectedItem(),
                userTextField.getText(),
                passwordTextField.getText(),
                lat,
                lon
        );

        return config;
    }

    protected void populateMountpoints(String[] mountpoints) {
        SwingUtilities.invokeLater(()-> {
            mountpointCombo.removeAllItems();
            for (String mp : mountpoints) mountpointCombo.addItem(mp);
        });
    }

    protected void populateComPorts(String[] comPorts) {
        SwingUtilities.invokeLater(()-> {
            comPortsCombo.removeAllItems();
            for (String cp : comPorts) comPortsCombo.addItem(cp);
            comPortsCombo.addItem("<Ingen seriell utdata>");
            comPortsCombo.setSelectedIndex(comPorts.length);
        });

    }

    protected int getSerialPort() { return comPortsCombo.getSelectedIndex(); }

}
