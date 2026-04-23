package core;

import java.io.IOException;
import java.io.InputStream;

public class PacketFactory {

    public static Rtcm3Packet create(ILogger logger, InputStream inputStream) throws IOException {

        byte[] headerBytes = new byte[2];
        if (!readFully(inputStream, headerBytes)) return null;

        int length = ((headerBytes[0] & 0x03) << 8) | (headerBytes[1] & 0xFF);

        byte[] payload = new byte[length];
        if (!readFully(inputStream, payload)) return null;

        byte[] crc = new byte[3];
        if (!readFully(inputStream, crc)) return null;

        int msgType = getMsgType(payload[0], payload[1]);
        switch(msgType) {
            default -> {
                return new Rtcm3Packet(logger, msgType, headerBytes, payload, crc);
            }
        }
    }

    /**
     * Uses 10 bits to convert to the message type
     * @param part1 First byte (All 8 bits are used)
     * @param part2 Second byte (Upper 4 bits are used)
     * @return message type
     */
    private static int getMsgType(byte part1, byte part2) {
        // Convert signed bytes to unsigned
        int msgPart1 = part1 & 0xFF;
        int msgPart2 = part2 & 0xFF;
        return (msgPart1 << 4) | (msgPart2 >> 4);
    }

    private static boolean readFully(InputStream in, byte[] buffer) throws IOException {
        int totalRead = 0;
        while (totalRead < buffer.length) {
            int bytesRead = in.read(buffer, totalRead, buffer.length - totalRead);
            if (bytesRead == -1) return false; // Stream closed
            totalRead += bytesRead;
        }
        return true;
    }
}
