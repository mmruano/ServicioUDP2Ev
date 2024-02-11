package com.example.serverudp2ev;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import static com.example.serverudp2ev.HelloController.connectedClientsList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {

        VBox root = new VBox();
        root.getChildren().addAll(connectedClientsList);
        VBox.setVgrow(connectedClientsList, Priority.ALWAYS);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Connected clients list");
        stage.setScene(scene);
        stage.show();

        // Iniciar el servidor en un hilo en segundo plano
        Thread serverThread = new Thread(HelloController::runServer);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void main(String[] args) {
        launch();
    }
}