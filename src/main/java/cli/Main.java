package cli;

import com.fazecast.jSerialComm.*;
import core.*;

import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final ILogger terminalLogger = new TerminalLogger();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {

        terminalLogger.log(Constants.PROGRAM_NAME + "\t(" + Constants.GITHUB_URL + ")\n", Constants.YELLOW);

        if (args.length < 1) {
            printHelp();
            return;
        }

        String server = Constants.DEFAULT_SERVER;
        String mountpoint = Constants.DEFAULT_MOUNTPOINT;
        int port = Constants.DEFAULT_PORT;
        float lat = Constants.DEFAULT_LAT;
        float lon = Constants.DEFAULT_LON;
        String username = "", password="";

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i];

            switch(arg) {
                case "-user":
                    username = args[++i];
                    break;
                case "-password":
                    password = args[++i];
                    break;
                case "-server":
                    server = args[++i];
                    break;
                case "-mountpoint":
                    mountpoint = args[++i];
                    break;
                case "-port":
                    port = Integer.parseInt(args[++i]);
                    break;
                case "-lat":
                    lat = Float.parseFloat(args[++i]);
                    break;
                case "-lon":
                    lon = Float.parseFloat(args[++i]);
                    break;
                default:
                    terminalLogger.log("Okänt kommando:\t" + args[i] + "\t" + args[++i], Constants.RED);
                    printHelp();
                    return;
            }
        }

        if (username.isEmpty()) {
            terminalLogger.log("Saknar användarnamn, anges med -user <användarnamn>", Constants.RED);
            return;
        } else if (password.isEmpty()) {
            terminalLogger.log("Saknar lösenord, anges med -password <lösenord>", Constants.RED);
            return;
        }

        SerialPort[] sPorts = SerialPort.getCommPorts();
        IPacketHandler handler;
        SerialPort sPort = null;

        if (sPorts.length == 0) {
            terminalLogger.log("Ingen serieport hittad, skriver till terminal..\n", Constants.PURPLE);
        } else {
            // Hittade serieportar
            terminalLogger.log("Serieportar hittade\n", Constants.PURPLE);
            terminalLogger.log("Vilken serieport ska användas för att skriva ut mottagen data? (Ange siffra)\n", Constants.BLUE);
            terminalLogger.log("0.\tMata ej ut seriell data\n");
            for (int i = 0; i < sPorts.length; i++) {
                SerialPort p = sPorts[i];
                terminalLogger.log((i + 1) + ".\t" + p.getSystemPortName() + ": " + p.getDescriptivePortName() + "\n");
            }
            System.out.println("\n");
            Scanner s = new Scanner(System.in);
            int input = 0;
            try {
                input = Integer.parseInt(s.nextLine());
            } catch (NumberFormatException e) {
                terminalLogger.log("Felaktigt val. Välj 0-" + sPorts.length, Constants.RED);
                return;
            }
            if (input < 0 || input > sPorts.length) {
                terminalLogger.log("Felaktigt val. Välj 0-" + sPorts.length, Constants.RED);
                return;
            }
            if (input != 0) sPort = sPorts[input - 1];
            }

        // No serial output
        if (sPort == null) {
            handler = packet -> {
                packet.printPacket();
                try {
                    terminalLogger.log("Mottaget:\t" + packet.getPacket());
                } catch (Exception e) {
                    terminalLogger.log("Fel:\t" + e, Constants.RED);
                }
            };
        } else {        // Use serial output
            // Open serial port for writing
            terminalLogger.log("Öppnar port " + sPort.getSystemPortName() + "...\n+");
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
                    terminalLogger.log("Misslyckades skriva till COM port: " + e, Constants.RED);
                }
            };
        }

        terminalLogger.log("Ansluter till NTRIP server \n" + server + ":" + port + " \nmountpoint: " + mountpoint + " \nanvändarnamn " + username + "\nlösenord " + password + "...\n", Constants.PURPLE);
        terminalLogger.log("Använder position lat / lon \t" + lat + "\t" + lon + "\n");
        NtripClient ntripClient = new NtripClient(terminalLogger, handler, server, port, mountpoint, username, password, lat, lon);

        executor.submit(ntripClient);

        // Stoppa programmet vid enter-tryckning
        new Scanner(System.in).nextLine();
        ntripClient.disconnect();
    }

    private static void printHelp() {
        terminalLogger.log(Constants.HELP_TEXT);
    }
}
