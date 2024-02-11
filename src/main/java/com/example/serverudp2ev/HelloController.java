package com.example.serverudp2ev;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class HelloController {

    private static final int SERVER_PORT = 5010;
    private static final int CLIENT_PORT = 6010;

    private static final String DOWNLOAD_FOLDER = "C:\\Users\\maria\\OneDrive\\Escritorio\\";
    private static final Map<InetAddress, String> ipByNickname = new HashMap<>();
    private static final ObservableList<String> nicknameList = FXCollections.observableArrayList();
    @FXML
    protected static ListView<String> connectedClientsList = new ListView<>();

    static void runServer() {
        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("Server listening on port " + SERVER_PORT);

            while (true) {
                byte[] receiveData = new byte[65507];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (message.startsWith("FIRST_CONNECTION:")) {

                    String username = message.substring("FIRST_CONNECTION:".length());
                    firstConnection(clientAddress, username);

                } else if (message.startsWith("MESSAGE:")) {

                    message = message.substring("MESSAGE:".length());

                    broadcastMessage(message, clientAddress);
                    System.out.println(ipByNickname.get(clientAddress) + ": " + message);

                } else if (message.startsWith("IMAGE:")) {

                    message = message.substring("IMAGE:".length());
                    handleImage(message);

                } else if (message.equalsIgnoreCase("STOP")) {
                    System.out.println("Server is stopping...");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void firstConnection(InetAddress clientAddress, String username) {
        if (!ipByNickname.containsValue(username)) {
            // El nickname no existe, agrega la entrada al mapa
            ipByNickname.put(clientAddress, username);

            Platform.runLater(() -> {
                nicknameList.clear();
                nicknameList.addAll(ipByNickname.values());
                connectedClientsList.setItems(nicknameList);
            });

            String responseMessage = "NICKNAME_TRUE";
            sendResponse(clientAddress, responseMessage);

            System.out.println("New client " + ipByNickname.get(clientAddress) + " has joined!");
        } else {
            String responseMessage = "NICKNAME_FALSE";
            sendResponse(clientAddress, responseMessage);
        }
    }

    private static void sendResponse(InetAddress clientAddress, String responseMessage) {
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket sendPacket = new DatagramPacket(
                    responseMessage.getBytes(),
                    responseMessage.length(),
                    clientAddress,
                    CLIENT_PORT
            );
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(String message, InetAddress senderAddress) {
        try (DatagramSocket socket = new DatagramSocket()) {
            for (InetAddress clientAddress : ipByNickname.keySet()) {

                String username = ipByNickname.get(senderAddress) + ": ";
                String nameAndMessage = username + message;

                if (!clientAddress.equals(senderAddress)) {
                    DatagramPacket sendPacket = new DatagramPacket(
                            nameAndMessage.getBytes(),
                            nameAndMessage.length(),
                            clientAddress,
                            CLIENT_PORT
                    );
                    socket.send(sendPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleImage(String base64Image) {
        try {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            if (!Files.exists(Paths.get(DOWNLOAD_FOLDER))) {
                Files.createDirectories(Paths.get(DOWNLOAD_FOLDER));
            }

            Path imagePath = Paths.get(DOWNLOAD_FOLDER, "received_image.png");
            Files.write(imagePath, imageBytes, StandardOpenOption.CREATE);

            System.out.println("Image received and saved in: " + DOWNLOAD_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}