package com.example;

import com.example.Renderer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static void main(String[] args) throws IOException {

        Renderer renderer = new Renderer();
        //launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {

    }

    public static void alert(String text) {
        ProgressBox.alert(text);
    }

    private static JFrame frame;
    private static JLabel label;
    public static void display(BufferedImage image){
        if(frame==null){
            frame=new JFrame();
            frame.setTitle("stained_image");
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label=new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label,BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        }else label.setIcon(new ImageIcon(image));
    }
}