package org.socket.server;

import java.net.*;
import java.io.*;
import java.util.*;

public class Main {
    public static int clientCounter = 0;
    public static final Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        final int PORT = 8080;

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Sunucu oluşturuldu, bağlantı bekleniyor...");

            while (true) {
                clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientUsername = in.readLine();

                if (clientUsername == null || clientUsername.isEmpty())
                {
                    out.println("Geçersiz kullanıcı adı.");
                    clientSocket.close();
                    continue;
                }

                if (clients.containsKey(clientUsername))
                {
                    out.println("Bu kullanıcı adı ile bağlanan başka biri var.");
                    clientSocket.close();
                    continue;
                }

                System.out.println("İstemci bağlandı: " + clientSocket);
                out.println("Başarıyla sunucuya bağlandınız.");
                int clientId = ++clientCounter;
                clients.put(clientUsername, out);
                new Thread(new ClientHandler(clientSocket, clientUsername)).start();
            }
        } catch (IOException e) {
            System.out.println("Bir hata oluştu: " + e.getMessage());
        }
        finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
