package org.socket.client;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        final String address = "127.0.0.1";
        final int port = 8080;
        Socket socket = null;
        Scanner messageScanner = null;

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Lütfen isminizi girin: ");
            String username = scanner.nextLine();

            socket = new Socket(address, port);
            System.out.println("Sunucu'ya bağlanılıyor...");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            JSONObject connectRequest = new JSONObject();
            connectRequest.put("username", username);
            connectRequest.put("reason", "connection");
            out.println(connectRequest.toString());

            String connectServerResponse = in.readLine();
            System.out.println(connectServerResponse);

            if (!connectServerResponse.toLowerCase().contains("bağlandınız")) {
                return;
            }

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverResponse = null;
                    while ((serverResponse = in.readLine()) != null) {
                        JSONObject jsonObject = new JSONObject(serverResponse);
                        System.out.println("\n" + jsonObject.getString("username") + ": " + jsonObject.getString("message"));
                        // Yeni mesaj geldiğinde tekrar "Siz:" yazısını göster
                        System.out.print("Siz: ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            
            messageScanner = new Scanner(System.in);
            while (true) {
                
                System.out.print("Siz: ");
                String userMessage = messageScanner.nextLine();

                
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("username", username);
                jsonMessage.put("message", userMessage);
                out.println(jsonMessage.toString());
            }
        } catch (UnknownHostException e) {
            System.out.println("Bilinmeyen host: " + address);
        } catch (IOException e) {
            System.err.println("I/O hatası: " + address);
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (messageScanner != null) {
                    messageScanner.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
