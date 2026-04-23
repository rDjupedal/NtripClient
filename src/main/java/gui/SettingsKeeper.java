package gui;

import java.io.*;

/**
 * Singleton to load and save last-use settings
 */
public class SettingsKeeper {

    private static final SettingsKeeper INSTANCE = new SettingsKeeper();

    private SettingsKeeper() {}

    public static SettingsKeeper getInstance() { return INSTANCE; }

    public NetworkConfig loadConfig() throws IOException, ClassNotFoundException {
        NetworkConfig cfg;
        ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("settings.ser")
        );
        cfg = (NetworkConfig) in.readObject();
        in.close();

        return cfg;
    }

    public void saveConfig(NetworkConfig cfg) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("settings.ser")
        );
        out.writeObject(cfg);
        out.close();
    }


}
