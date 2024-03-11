package org.socket.server;
import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String clientUsername;

    public ClientHandler(Socket socket, String clientUsername) {
        this.clientSocket = socket;
        this.clientUsername = clientUsername;
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientMessage;

            while ((clientMessage = in.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(clientMessage);
                String senderUsername = jsonObject.getString("username");
                String incomingMessage = jsonObject.getString("message");
                System.out.println(senderUsername + ": " + incomingMessage);
                for (Map.Entry<String, PrintWriter> entry : Main.clients.entrySet()) {
                    String username = entry.getKey();
                    PrintWriter writer = entry.getValue();

                    
                    if (!username.equals(clientUsername)) {
                        writer.println(clientMessage);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Bir hata oluştu: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
