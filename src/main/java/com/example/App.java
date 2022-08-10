package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();

    final String TITLE_OF_PROGRAM = "SketchIt";

    public static final String TEMP_DIRECTORY_ADDRESS = "C:\\temp";

    public static final String PROGRAM_DIRECTORY_ADDRESS = "C:\\temp\\SketchIt", RENDERED_FRAME_DIRECTORY_ADDRESS = PROGRAM_DIRECTORY_ADDRESS + "\\RenderedFrames", RAW_FRAME_DIRECTORY_ADDRESS = PROGRAM_DIRECTORY_ADDRESS + "\\RawFrames";

    private String fileAddress, exportFolderAddress, exportFileName;

    public static void main(String[] args) {
        launch(args);
    }

    private static JFrame WINDOW;
    private static JLabel IMAGE_PREVIEW;

    @Override
    public void start(Stage window) throws Exception {

        File PROGRAM_DIRECTORY = new File(PROGRAM_DIRECTORY_ADDRESS), TEMP_DIRECTORY = new File(TEMP_DIRECTORY_ADDRESS), RENDERED_FRAME_DIRECTORY = new File(RENDERED_FRAME_DIRECTORY_ADDRESS), RAW_FRAME_DIRECTORY = new File(RAW_FRAME_DIRECTORY_ADDRESS);

        //THE FOLLOWING CODE ALLOWS EACH DIRECTORY TO BE ACCESSIBLE TO THE PROGRAM TO FETCH AND SAVE FILES

        PROGRAM_DIRECTORY.setExecutable(true);
        PROGRAM_DIRECTORY.setWritable(true);
        PROGRAM_DIRECTORY.setReadable(true);

        TEMP_DIRECTORY.setExecutable(true);
        TEMP_DIRECTORY.setWritable(true);
        TEMP_DIRECTORY.setReadable(true);

        RENDERED_FRAME_DIRECTORY.setExecutable(true);
        RENDERED_FRAME_DIRECTORY.setWritable(true);
        RENDERED_FRAME_DIRECTORY.setReadable(true);

        RAW_FRAME_DIRECTORY.setExecutable(true);
        RAW_FRAME_DIRECTORY.setWritable(true);
        RAW_FRAME_DIRECTORY.setReadable(true);

        //================================================================================================

        //Checks to see if directories exist for frame rendering and if they don't it creates the directories
        if (!TEMP_DIRECTORY.exists()) {
            TEMP_DIRECTORY.mkdir();
        }
        if (!PROGRAM_DIRECTORY.exists()) {
            PROGRAM_DIRECTORY.mkdir();
        }
        if (!RENDERED_FRAME_DIRECTORY.exists() && PROGRAM_DIRECTORY.exists()) {
            RENDERED_FRAME_DIRECTORY.mkdir();
        }
        if (!RAW_FRAME_DIRECTORY.exists() && PROGRAM_DIRECTORY.exists()) {
            RAW_FRAME_DIRECTORY.mkdir();
        }

        BorderPane borderPane = new BorderPane();

        //===============================================================
        GridPane grid = new GridPane();

        //Currently Selected File Label
        javafx.scene.control.Label currentlySelectedFile = new javafx.scene.control.Label("No File Selected");
        currentlySelectedFile.setMinWidth(300);
        currentlySelectedFile.setMaxWidth(300);
        GridPane.setConstraints(currentlySelectedFile, 1, 0);

        //Browse Currently Selected File Button
        javafx.scene.control.Button browseCurrentlySelectedFile = new Button("Select File to Render");
        GridPane.setConstraints(browseCurrentlySelectedFile, 0, 0);
        browseCurrentlySelectedFile.setOnAction(e -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                fileAddress = file.getAbsolutePath();
                currentlySelectedFile.setText(fileAddress);
            }
        });

        borderPane.setTop(grid);
        //===============================================================

        Scene home = new Scene(borderPane, 1280, 720);
        window.setScene(home);
        window.show();

        //Renderer renderer = new Renderer();
    }

    /**
     * Opens file chooser to select files
     * @param fileChooser FileChooser object
     */
    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Select File(s)");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    public static void alert(String text) {
        ProgressBox.alert(text);
    }


    private static JFrame frame;
    private static JLabel label;
    /**
     * Used in testing to view images
     * @param image
     */
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