package tausif.androidprojects.bluetoothtunnelingdemo;

public class PacketManager {
    public static String createIpMacSyncPkt(int pktType, String macAddr) {
        return (String.valueOf(pktType) + "#" + macAddr + "#");
    }

    public static String createServerRequest(int pktType) {
        String pkt = String.valueOf(pktType) + "#" + "Request from " + Constants.hostWifiName;
        return pkt;
    }

    public static String createServerResponse(int pktType) {
        String pkt = String.valueOf(pktType) + "#" + "Response from " + Constants.hostWifiName;
        return pkt;
    }
}
