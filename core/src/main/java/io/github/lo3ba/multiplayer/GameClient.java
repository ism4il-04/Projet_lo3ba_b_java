package io.github.lo3ba.multiplayer;

import io.github.lo3ba.Main_Game;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Main_Game game;

    public GameClient(String ipAddress, Main_Game game) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("SERVER >" + new String(packet.getData()));
        }
    }

    public void  sendData (byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1234);
        try{
            socket.send(packet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
