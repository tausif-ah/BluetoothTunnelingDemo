package tausif.androidprojects.bluetoothtunnelingdemo;

import java.io.Serializable;
import java.net.InetAddress;

class ServerMessage implements Serializable {
    int type;
    InetAddress source;
    int destPort;
    String data;

    ServerMessage(int type, InetAddress source, int destPort, String data) {
        this.type = type;
        this.source = source;
        this.destPort = destPort;
        this.data = data;
    }
}
