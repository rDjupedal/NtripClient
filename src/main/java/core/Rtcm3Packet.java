package core;

public class Rtcm3Packet {

    private final ILogger logger;
    private final int msgType;
    private final byte[] headerBytes, payload, crc;

    public Rtcm3Packet(ILogger logger, int msgType, byte[] headerBytes, byte[] payload, byte[] crc) {
        this.logger = logger;
        this.msgType = msgType;
        this.headerBytes = headerBytes;
        this.payload = payload;
        this.crc = crc;
    }

    public void printPacket() {
        logger.log("\n---------------------\n");
        logger.log("RTCM3-paket (" + payload.length + " bytes), meddelandetyp " + msgType + "\n");
        logger.log("0xD3 ");
        for (byte hb : headerBytes) logger.log(hb, Constants.RED);
        for (byte pb : payload) logger.log(pb, Constants.GREEN);
        for (byte cb : crc)    logger.log(cb, Constants.BLUE);
    }

    public byte[] getPacket() {
        byte[] packet = new byte[headerBytes.length + payload.length + crc.length];
        int counter = 0;
        for (byte b : headerBytes) {
            packet[counter] = b;
            counter++;
        }
        for (byte b : payload) {
            packet[counter] = b;
            counter++;
        }
        for (byte b : crc) {
            packet[counter] = b;
            counter++;
        }
        return packet;
    }
}
