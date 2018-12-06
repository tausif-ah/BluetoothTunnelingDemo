package tausif.androidprojects.bluetoothtunnelingdemo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;

public class WDUDPListener extends Thread{
    private MainActivity homeActivity;
    private byte[] receivedBytes;
    private DatagramPacket receivedPkt;
    private DatagramSocket socket;

    WDUDPListener(MainActivity homeActivity) {
        this.homeActivity = homeActivity;
        receivedBytes = new byte[1024];
        receivedPkt = new DatagramPacket(receivedBytes, receivedBytes.length);
        try {
            socket = new DatagramSocket(Constants.WI_FI_DIRECT_UDP_LISTENING_PORT);
        }catch (IOException ex) {

        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket.receive(receivedPkt);
                long receivingTime = Calendar.getInstance().getTimeInMillis();
                InetAddress srcAddr = receivedPkt.getAddress();
                String pktStr = new String(receivedBytes, 0, receivedPkt.getLength());
                homeActivity.processReceivedWiFiPkt(srcAddr, receivingTime, pktStr);
            }catch (IOException ex) {

            }
        }
    }
}
