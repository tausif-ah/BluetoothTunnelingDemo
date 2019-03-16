package tausif.androidprojects.bluetoothtunnelingdemo;

import java.io.Serializable;
import java.net.InetAddress;

class ServerMessage implements Serializable {
    int type;
    InetAddress srcAddress;
    int srcPort;
    InetAddress destAddress;
    private int destPort;
    String data;

    ServerMessage(int type, InetAddress srcAddress, int destPort, String data) {
        this.type = type;
        this.srcAddress = srcAddress;
        this.destPort = destPort;
        this.data = data;
    }

    void setDestAddress(InetAddress destAddress) {
        this.destAddress = destAddress;
    }
}
