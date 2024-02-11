module com.example.serverudp2ev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.serverudp2ev to javafx.fxml;
    exports com.example.serverudp2ev;
}