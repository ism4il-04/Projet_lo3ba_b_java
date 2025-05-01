package io.github.lo3ba.multiplayer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+": "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        }
        catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat =  bufferedReader.readLine();
                        System.out.println("From groupChat: "+msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }

            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void sendMessage(String messageToSend) {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while (socket.isConnected()) {
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        }
        catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendPosition(float x, float y) {
        sendMessage("POS:" + x + "," + y);
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez nom du client: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket,username);
        client.listenForMessages();
        client.sendMessage();
    }
}
