package gui;

import java.io.Serial;
import java.io.Serializable;

public class NetworkConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String server;
    private final int port;
    private final String mountpoint;
    private final String username;
    private final String password;
    private final float lat;
    private final float lon;

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getMountpoint() {
        return mountpoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public NetworkConfig(String server, int port, String mountpoint, String username, String password, float lat, float lon) {
        this.server = server;
        this.port = port;
        this.mountpoint = mountpoint;
        this.username = username;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
    }

    public boolean isValidConnect() {
        return isValidRefresh() && mountpoint != null && !mountpoint.isEmpty();
    }

    public boolean isValidRefresh() {
        if (server == null || server.isEmpty()) return false;
        if (port < 1 || port > 65535) return false;
        return true;
    }


}
