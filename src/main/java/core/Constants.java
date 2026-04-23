package core;

public class Constants {
    public static final String PROGRAM_NAME = "Enkel NTRIP klient 1.0";
    public static final String GITHUB_URL = "https://github.com/rDjupedal/NtripClient";
    public static final int WINDOW_WIDTH = 550;
    public static final int WINDOW_HEIGHT = 600;
    public static final int GGA_REPEAT_MILLIS = 10000;
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String GRAY = "\u001B[90m";
    public final static String RESET = "\u001B[0m";
    public final static String DEFAULT = "\u001B[0m";
    public final static String DEFAULT_SERVER = "rtk.ncgeo.se";
    public final static int DEFAULT_PORT = 8500;
    public final static String DEFAULT_MOUNTPOINT = "MSM_GNSS";
    public final static float DEFAULT_LAT = 59f;
    public final static float DEFAULT_LON = 19f;
    public final static int FIX_QUALITY = 1;
    public final static int NUM_SATS = 20;
    public final static float HDOP = 0.8f;
    public final static float HEIGHT = 0f;
    public final static float GEOID = 0f;
    public final static String HELP_TEXT = """
    Kan användas för att testa en NTRIP inlogging eller för att skriva mottagan data till en serieport.

    \t-user <användarnamn>\t(krävs)
    \t-password <lösenord>\t(krävs)
    \t-server <server> adress (default %s)
    \t-port <port> (default %s)
    \t-mountpoint <mountpoint> (default %s)
    \t-lat <latitud> (default %s)
    \t-lon <longitud> (default %s)

    NTRIP anslutning stoppas med <Enter>
    Om utmatad text ser konstig ut eller saknar färg (på Windows), kör kommandot
    reg add HKCU\\Console /v VirtualTerminalLevel /t REG_DWORD /d 1


    """.formatted(
            Constants.DEFAULT_SERVER,
            Constants.DEFAULT_PORT,
            Constants.DEFAULT_MOUNTPOINT,
            Constants.DEFAULT_LAT,
            Constants.DEFAULT_LON
    );
}
