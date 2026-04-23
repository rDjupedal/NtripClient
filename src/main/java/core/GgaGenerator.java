package core;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GgaGenerator {

    public static String getGGA(float lat, float lon) {

        Locale.setDefault(Locale.US);       // Use decimal sign "."

        // 1. Convert latitude to ddmm.mmmm
        int latDegrees = (int) Math.abs(lat);
        double latMinutes = (Math.abs(lat) - latDegrees) * 60;
        String latNmea = String.format("%02d%06.3f", latDegrees, latMinutes);
        String latHemisphere = lat >= 0 ? "N" : "S";

        // 2. Convert longitude to dddmm.mmmm
        int lonDegrees = (int) Math.abs(lon);
        double lonMinutes = (Math.abs(lon) - lonDegrees) * 60;

        String lonNmea = String.format("%03d%06.3f", lonDegrees, lonMinutes);
        String lonHemisphere = lon >= 0 ? "E" : "W";

        // 3. Get current UTC time hhmmss
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        String utcTime = nowUtc.format(DateTimeFormatter.ofPattern("HHmmss"));

        // 4. Assemble GGA without checksum
        String ggaWithoutChecksum = String.format(Locale.US,
                "GPGGA,%s,%s,%s,%s,%s,%d,%02d,%.1f,%.1f,M,%.1f,M,,",
                utcTime,
                latNmea,
                latHemisphere,
                lonNmea,
                lonHemisphere,
                Constants.FIX_QUALITY,
                Constants.NUM_SATS,
                Constants.HDOP,
                Constants.HEIGHT,
                Constants.GEOID
        );

        // 5. Compute XOR checksum
        int checksum = 0;
        for (char c : ggaWithoutChecksum.toCharArray()) {
            checksum ^= c;
        }

        // 6. Return final GGA sentence
        return String.format("$%s*%02X", ggaWithoutChecksum, checksum) + "\r\n";
    }

}
