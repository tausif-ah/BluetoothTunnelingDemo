package tausif.androidprojects.bluetoothtunnelingdemo;

import java.io.Serializable;
import java.net.InetAddress;

class Message implements Serializable {
    int type;
    InetAddress sourceIP;
    InetAddress destinationIP;
    String data;
    boolean interGroupMessage;

    Message(int type, String data) {
        this.type = type;
        this.data = data;
        this.interGroupMessage = false;
    }

    public void setInterGroupMessage(boolean interGroupMessage) {
        this.interGroupMessage = interGroupMessage;
    }
}
