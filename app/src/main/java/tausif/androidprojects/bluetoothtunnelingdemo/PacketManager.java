package tausif.androidprojects.bluetoothtunnelingdemo;

public class PacketManager {
    public static String createIpMacSyncPkt(int pktType, String macAddr) {
        return (String.valueOf(pktType) + "#" + macAddr + "#");
    }

    public static String createHelloForGrpOwner(int pktType) {
        String pkt = String.valueOf(pktType) + "#" + "Hello from " + Constants.selfWifiName;
        return pkt;
    }
}
