package core;

import gui.NetworkConfig;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

public class MountpointFetcher {

    public static void fetchMountpoints(NetworkConfig config, IMountpointCallback callback) {

        new Thread(() -> {

            ArrayList<String> mountpoints = new ArrayList<>();
            String host = config.getServer();
            int port = config.getPort();
            String username = config.getUsername();
            String password = config.getPassword();

            try (Socket socket = new Socket(host, port);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Header
                String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

                // HTTP GET-request
                writer.write("GET / HTTP/1.1\r\n");
                writer.write("Host: " + host + "\r\n");
                writer.write("Ntrip-Version: Ntrip/2.0\r\n");
                writer.write("Authorization: Basic " + encoded + "\r\n");
                writer.write("User-Agent: NTRIP JavaClient\r\n");
                writer.write("\r\n");
                writer.flush();

                // Reply
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("STR;")) {
                        String[] parts = line.split(";");
                        if (parts.length > 1) {
                            mountpoints.add(parts[1]); // Mountpoint
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("Fel vid socket-anslutning: " + e.getMessage());
            }

            callback.onFinish(mountpoints.toArray(new String[0]));

        }).start();

    }

}
