package io.github.lo3ba.multiplayer;

import com.badlogic.gdx.Gdx;
import io.github.lo3ba.screens.MultiplayerScreen;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private MultiplayerScreen multiplayerScreen;

    public String getUsername() {
        return username;
    }

    public Client(Socket socket, String username) {
        try {
            System.out.println("creation de "+username);
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            sendMessage(username);
            listenForMessages();
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
        new Thread(() -> {
            String msg;
            while (socket.isConnected()) {
                try {
                    msg = bufferedReader.readLine();
                    if (msg.startsWith("POS:")) {
                        String[] parts = msg.substring(4).split(",");
                        float x = Float.parseFloat(parts[0]);
                        float y = Float.parseFloat(parts[1]);
                        if (multiplayerScreen != null) {
                            multiplayerScreen.updateEnemyPosition(x, y);
                        }
                    }
                    if (msg.equals("SHOOT")) {
                        Gdx.app.postRunnable(() -> multiplayerScreen.enemyFire());
                    }

                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
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
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public static void main(String[] args) throws IOException {
        //Scanner scanner = new Scanner(System.in);
        //System.out.println("Entrez nom du client: ");
        //String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket,"ismail");
        client.listenForMessages();
        client.sendMessage();
    }

    public String getName() {
        return username;
    }

    public void setMultiplayerScreen(MultiplayerScreen multiplayerScreen) {
        this.multiplayerScreen = multiplayerScreen;
    }
}
