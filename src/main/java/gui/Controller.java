package gui;

import com.fazecast.jSerialComm.SerialPort;
import core.*;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private NtripClient ntripClient;
    private final NetworkPane networkPane;
    private final OutputPane outPane;
    private final ILogger logger;
    private final SettingsKeeper settingsKeeper = SettingsKeeper.getInstance();
    private final SerialPort[] sPorts;


    public Controller(WindowFrame frame) {
        networkPane = frame.getNetworkPane();
        outPane = frame.getOutputPane();
        setUpListeners();

        // Text output
        logger = new ILogger() {
            @Override
            public void log(String msg) {
              outPane.append(msg);
            }

            @Override
            public void log(String msg, String color) {
              outPane.append(msg, color);
            }

            @Override
            public void log(byte b) {
                outPane.append(String.format("0x%02X\t", b));
            }

            @Override
            public void log(byte b, String color) {
                outPane.append(String.format("0x%02X\t", b), color);
            }
        };

        // Serial ports
        sPorts = SerialPort.getCommPorts();
        String[] portNames = new String[sPorts.length];
        for (int i = 0; i < sPorts.length; i++) portNames[i] = sPorts[i].getDescriptivePortName();
        networkPane.populateComPorts(portNames);

        // Load settings from last time
        try {
            NetworkConfig config = settingsKeeper.loadConfig();
            if (config != null) networkPane.setConfig(config);
        } catch (Exception e) { logger.log("Misslyckades att läsa konfigurationsfil.\n"); }

        outPane.append(Constants.PROGRAM_NAME + "\n" + Constants.GITHUB_URL + "\n", Constants.PURPLE);
    }

    // Setup button-click listeners in the View
    private void setUpListeners() {
        ActionListener conL = e -> connect();
        ActionListener disL = e -> disconnect();
        ActionListener refL = e -> refreshMountpoints();
        networkPane.setupListeners(conL, disL, refL);
    }

    private void connect() {

        if (ntripClient != null && ntripClient.isRunning()) {
            logger.log("Det finns redan en aktiv anslutning. Koppla ner den först.\n", Constants.RED);
            return;
        }

        NetworkConfig config = networkPane.getConfig();
        if (!config.isValidConnect()) {
            logger.log("Kontrollera parametrarna och försök igen");
            return;
        }

        // What to do with received packets depends on whether serial output is enabled
        int sPortIndex = networkPane.getSerialPort();
        IPacketHandler handler;
        if (sPortIndex > sPorts.length - 1) handler = Rtcm3Packet::printPacket;  // Serial port not chosen
        else {
            SerialPort sPort = sPorts[sPortIndex];
            sPort.setBaudRate(19200);
            sPort.setNumDataBits(8);
            sPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            sPort.setParity(SerialPort.NO_PARITY);
            sPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            sPort.openPort();
            OutputStream out = sPort.getOutputStream();

            handler = packet -> {
                packet.printPacket();
                try {
                    out.write(packet.getPacket());
                    out.flush();
                } catch (Exception e) {
                    logger.log("Misslyckades skriva till COM port: " + e, Constants.RED);
                }
            };
        }

        ntripClient = new NtripClient(
                logger,
                handler,
                config.getServer(),
                config.getPort(),
                config.getMountpoint(),
                config.getUsername(),
                config.getPassword(),
                config.getLat(),
                config.getLon()
        );

        executor.submit(ntripClient);

        try { settingsKeeper.saveConfig(config); }
        catch (Exception e) { logger.log("Kunde inte spara inställningarna till fil\n" + e.getMessage(), Constants.RED); }
    }

    private void disconnect() {
          if (ntripClient == null) return;
          ntripClient.disconnect();
    }

    // Program (window) close
    protected void exit() {
        disconnect();
    }

    private void refreshMountpoints() {
        NetworkConfig config = networkPane.getConfig();
        if (!config.isValidRefresh()) {
            logger.log("Kontrollera att du matat in korrekt serveradress och portnummer\n");
            return;
        }
        MountpointFetcher.fetchMountpoints(config, mp -> {
            networkPane.populateMountpoints(mp);
            logger.log("Mountpoint lista uppdaterad.\n");
        });
    }

}
