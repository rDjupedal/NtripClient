package core;

/**
 * The handler defines what to do with each RTCM3 packet received
 */
public interface IPacketHandler {

    void handle(Rtcm3Packet packet);
}
