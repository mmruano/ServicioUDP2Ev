package com.example.serverudp2ev;

import javafx.scene.control.ListCell;

public class Nickname extends ListCell<String> {
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);
            setGraphic(null);
        }
    }
}