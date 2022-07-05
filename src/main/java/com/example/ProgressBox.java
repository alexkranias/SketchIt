package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ProgressBox {

    public static Stage window;
    static Label message;
    static VBox layout;

    public static void open(String text) {

        if (window == null) {
            window = new Stage();
            window.initModality(Modality.WINDOW_MODAL);
            window.setTitle("Progess");
            window.setMinWidth(400);
            window.setMinHeight(225);

            message = new Label();
            message.setTextAlignment(TextAlignment.CENTER);

            layout = new VBox(15);
            layout.setPadding(new Insets(10, 10,10,10));
            layout.getChildren().add(message);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout);
            window.setScene(scene);
        }

        message.setText(text);
        window.show();
    }

    public static void alert(String text) {

        if (window == null) {
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("IMPORTANT");
            window.setMinWidth(400);
            window.setMinHeight(225);

            message = new Label();
            message.setTextAlignment(TextAlignment.CENTER);
            message.setWrapText(true);

            layout = new VBox(15);
            layout.setPadding(new Insets(10, 10,10,10));
            layout.getChildren().add(message);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout);
            window.setScene(scene);
        }

        message.setText(text);
        window.show();
    }

    public static void close(Stage window) {
        window.close();
    }

}
