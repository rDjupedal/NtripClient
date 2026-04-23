package core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

public class NtripClient implements Runnable {

    private final ILogger logger;
    private final IPacketHandler handler;
    private final String server, mountpoint, username, password;
    private final int port;
    private final float lat, lon;
    private boolean isRunning = true;
    private OutputStream out;

    public NtripClient(ILogger logger, IPacketHandler handler, String server, int port, String mountpoint, String username, String password, float lat, float lon) {
        this.logger = logger;
        this.handler = handler;
        this.server = server;
        this.port = port;
        this.mountpoint = mountpoint;
        this.username = username;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(server, port)) {

            out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // Create Base64-auth
            String credentials = Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes());

            // NTRIP v2.0 GET-request (HTTP/1.1)
            String request = "GET /" + mountpoint + " HTTP/1.1\r\n" +
                    "Host: " + server + "\r\n" +
                    "User-Agent: NTRIP JavaClient/2.0\r\n" +
                    "Accept: */*\r\n" +
                    "Connection: close\r\n" +
                    "Ntrip-Version: Ntrip/2.0\r\n" +
                    "Authorization: Basic " + credentials + "\r\n\r\n";

            // Send request
            out.write(request.getBytes());
            out.flush();

            // Read headers
            StringBuilder header = new StringBuilder();
            int prev = 0, curr;
            while (true) {
                curr = in.read();
                if (curr == -1) break;
                header.append((char) curr);
                if (prev == '\r' && curr == '\n' && header.toString().endsWith("\r\n\r\n")) {
                    break;
                }
                prev = curr;
            }

            if (!header.toString().contains("200 OK")) {
                System.err.println("Anslutningen misslyckades.\n" + header);
                logger.log("Anslutning misslyckades\nHTTP error: " + header, Constants.RED);
                disconnect();
                return;
            }

            logger.log("-- Header --\n" + header);

            long lastSentGGA = 0;   // Timestamp

            logger.log("-- Tar emot RTCM3-paket --\n");

            while (isRunning) {

                if (System.currentTimeMillis() - lastSentGGA >= Constants.GGA_REPEAT_MILLIS) {
                    logger.log("(Sending GGA...)\n", Constants.BLUE);
                    sendGGA();
                    lastSentGGA = System.currentTimeMillis();
                }

                int b = in.read();
                if (b == -1) {
                    logger.log("Anslutningen avbröts", Constants.RED);
                    isRunning = false;
                    break;
                }

                if (b == 0xD3) {
                    Rtcm3Packet packet = PacketFactory.create(logger, in);
                    handler.handle(packet);
                } else {
                    // Unidentified packet
                    logger.log((byte) b, Constants.GRAY);
                }
            }
            logger.log("\n\nAnslutning stängd\n");

        } catch (Exception e) {
            logger.log("Anslutning misslyckades\n", Constants.RED);
            logger.log(e.getMessage() + "\n");
        }
    }

    private void sendGGA() {
        String gga = GgaGenerator.getGGA(lat, lon);
        try {
            out.write(gga.getBytes());
            out.flush();
        } catch (Exception e) {
            logger.log("Anslutning fel\n" + e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

