package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.Buffer;

import javafx.util.StringConverter;
import sun.font.FontFamily;

public class App extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private DirectoryChooser directoryChooser = new DirectoryChooser();

    final String TITLE_OF_PROGRAM = "SketchIt";

    public static final String TEMP_DIRECTORY_ADDRESS = "C:\\temp";

    public static final String PROGRAM_DIRECTORY_ADDRESS = "C:\\temp\\SketchIt", RENDERED_FRAME_DIRECTORY_ADDRESS = PROGRAM_DIRECTORY_ADDRESS + "\\RenderedFrames", RAW_FRAME_DIRECTORY_ADDRESS = PROGRAM_DIRECTORY_ADDRESS + "\\RawFrames";

    private String fileAddress;

    public static void main(String[] args) {
        launch(args);
    }

    private static JFrame WINDOW;
    private static JLabel IMAGE_PREVIEW;

    static ImageView imgPreview = new ImageView();
    static StackPane inner = new StackPane();

    static final double[] DOWN_RES_ARRAY = {0.1, 0.2, 0.5, 1};
    static final double[] FINAL_RES_ARRAY = {0.1, 0.35, 0.65, 1, 1.5};

    //RENDERER PARAMETERS
    static BufferedImage RENDERED_IMAGE;
    static BufferedImage ORIGINAL_IMAGE;
    static double CONTRAST;
    static int BLUR, DETAIL, DOWNSCALE_RESOLUTION, FINAL_RESOLUTION;;

    @Override
    public void start(Stage window) throws Exception, IOException {

        window.setTitle("SketchIt");
        window.setMinWidth(1180);
        window.setMinHeight(720);
        window.sizeToScene();

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
        borderPane.setPadding(new Insets(20, 20, 20, 20));

        //===============================================================CENTER

        StackPane sp = new StackPane();
        sp.setPadding(new Insets(15, 15, 15 ,15));
        inner.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(20), BorderWidths.DEFAULT)));
        sp.getChildren().add(inner);
        inner.setPadding(new Insets(10, 10, 10, 10));
        inner.getChildren().add(new Label("No Photo Selected"));
        inner.getChildren().add(imgPreview);
        //inner.getChildren().add(imgPreview);
        //new Border(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), BorderWidths.DEFAULT)

        borderPane.setCenter(sp);

        //===============================================================TOP

        HBox top1 = new HBox();

        //Currently Selected File Label
        javafx.scene.control.Label currentlySelectedFile = new Label("No File Selected");
        currentlySelectedFile.setMinWidth(300);
        currentlySelectedFile.setMaxWidth(300);

        //Browse Currently Selected File Button
        Button browseCurrentlySelectedFile = new Button("Select Photo");
        top1.getChildren().add(browseCurrentlySelectedFile);
        browseCurrentlySelectedFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                fileAddress = file.getAbsolutePath();
                currentlySelectedFile.setText(fileAddress.substring(fileAddress.lastIndexOf("\\")+1));
                try {
                    Image img = new Image(new FileInputStream(fileAddress));
                    double imgHeight = img.getHeight();
                    double imgWidth = img.getWidth();
                    double aspectRatio = imgWidth / imgHeight;

                    ORIGINAL_IMAGE = ImageIO.read(new File(fileAddress));

                    CONTRAST = 1;
                    DOWNSCALE_RESOLUTION = 3;
                    FINAL_RESOLUTION = 3;
                    DETAIL = 12;
                    BLUR = 9;
                    updateRender();

                    imgPreview.setImage(convertToFxImage(ORIGINAL_IMAGE));

                    if (aspectRatio > 1.5) {
                        imgPreview.setFitHeight(window.getWidth()*0.5 / aspectRatio);
                        imgPreview.setFitWidth(window.getWidth()*0.5);
                    } else {
                        imgPreview.setFitHeight(window.getHeight()*0.7);
                        imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
                    }
                } catch (IOException ex) {
                    //File not capatable label
                    ex.printStackTrace();
                }
            }
        });

        top1.getChildren().add(currentlySelectedFile);

        top1.setSpacing(15);
        top1.alignmentProperty().setValue(Pos.CENTER_LEFT);

        HBox top2 = new HBox();
        top2.setPadding(new Insets(0, 5, 0, 0));

        javafx.scene.control.Label text2 = new Label("Select Mode");
        top2.getChildren().add(text2);

        ObservableList<String> modes =
                FXCollections.observableArrayList(
                        "Default"
                        //"Default", "Mode 1", "Mode 2", "Mode 3", "Custom"
                );
        ComboBox modeSelection = new ComboBox(modes);
        modeSelection.setValue("Default");
        top2.getChildren().add(modeSelection);

        top2.setSpacing(15);
        top2.alignmentProperty().setValue(Pos.CENTER_RIGHT);

        BorderPane top = new BorderPane();
        top.setLeft(top1);
        top.setRight(top2);

        borderPane.setTop(top);
        //===============================================================LEFT

        BorderPane left = new BorderPane();

        VBox left1 = new VBox();


        Text title = new Text("Instructions\n");
        title.setUnderline(true);
        title.setTextAlignment(TextAlignment.CENTER);
        left1.getChildren().add(title);

        Text desc = new Text("1) Select a Photo\n2) Select a Mode\n3) Adjust Settings\n4) View Edge Detection in Action!\n5) Export Your Render");
        desc.lineSpacingProperty().setValue(30);
        left1.getChildren().add(desc);

        Text note = new Text("\n\nNote: a BUG you may experience\n(is being fixed) where GUI\nelements are outside of window\ncan be RESOLVED be resizing\nthe window using the mouse!\nAs in, grab bottom right corner of\nthis app window and resize it.");
        left1.getChildren().add(note);

        BorderPane.setAlignment(left1, Pos.CENTER);
        BorderPane.setMargin(left1, new Insets(20, 10, 20, 0));
        left.setTop(left1);

        VBox left2 = new VBox();

        Label contactInfoTitle = new Label("Author");
        contactInfoTitle.setUnderline(true);
        contactInfoTitle.alignmentProperty().setValue(Pos.CENTER);
        left2.getChildren().add(contactInfoTitle);

        Label contactInfo = new Label("Alexander Kranias\nalexander.kranias@gatech.edu\n\nLast Updated: September 2022");
        left2.getChildren().add(contactInfo);

        left.setBottom(left2);

        borderPane.setLeft(left);

        //===============================================================RIGHT

        VBox right = new VBox(0);

        VBox right1 = new VBox(12);
        VBox right2 = new VBox(12);
        VBox right3 = new VBox(12);

        right2.setPadding(new Insets(10, 0, 0, 0));
        right1.setPadding(new Insets(20, 0, 20, 0));

        BorderPane right_outer_outer = new BorderPane();
        StackPane right_outer = new StackPane(right);
        right_outer.setPadding(new Insets(10, 10, 10, 10));
        right_outer_outer.setTop(right_outer);

        //DEFAULT MODE
        Text preprocess = new Text("Pre-Processing Settings");
        preprocess.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        right2.getChildren().add(preprocess);

        Label contrast_l = new Label("Image Contrast");
        Slider con = new Slider(0, 1, 0.5); //make actual range some sqrt thing up from 0.5 - 2
        right2.getChildren().add(new VBox(contrast_l, con));

        Label preres_label = new Label("Downsample Resolution");
        Slider preres = new Slider(0, 3, 3);
        preres.setMin(0);
        preres.setMax(3);
        preres.setValue(3);
        preres.setMinorTickCount(0);
        preres.setMajorTickUnit(1);
        preres.setSnapToTicks(true);
        preres.setShowTickMarks(true);
        preres.setShowTickLabels(true);

        preres.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "1/10";
                if (n < 1.5) return "1/5";
                if (n < 2.5) return "1/2";

                return "Original";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "1/10":
                        return 0d;
                    case "1/5":
                        return 1d;
                    case "1/2":
                        return 2d;
                    case "Original":
                        return 3d;
                    default:
                        return 3d;
                }
            }
        });
        preres.valueProperty().addListener(((observable, oldValue, newValue) -> {
            DOWNSCALE_RESOLUTION = (int)preres.getValue();
            try {
                updateRender();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        right2.getChildren().add(new VBox(preres_label, preres));


        Text renderer = new Text("Renderer Settings");
        renderer.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        Label res_label = new Label("Resolution");
        Slider res = new Slider(0, 4, 3);
        res.setMin(0);
        res.setMax(4);
        res.setValue(3);
        res.setMinorTickCount(0);
        res.setMajorTickUnit(1);
        res.setSnapToTicks(true);
        res.setShowTickMarks(true);
        res.setShowTickLabels(true);

        res.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "Lowest";
                if (n < 1.5) return "Low";
                if (n < 2.5) return "Normal";
                if (n < 3.5) return "High";

                return "Highest";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Lowest":
                        return 0d;
                    case "Low":
                        return 1d;
                    case "Normal":
                        return 2d;
                    case "High":
                        return 3d;
                    case "Highest":
                        return 4d;

                    default:
                        return 3d;
                }
            }
        });
        res.valueProperty().addListener(((observable, oldValue, newValue) -> {
            FINAL_RESOLUTION = (int)res.getValue();
            try {
                updateRender();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        right1.alignmentProperty().setValue(Pos.TOP_CENTER);
        right1.getChildren().add(renderer);
        right1.getChildren().add(new VBox(res_label, res));

        Label detail_label = new Label("Detail");
        Slider detail = new Slider(0, 1, 0.5);
        detail.valueProperty().addListener(((observable, oldValue, newValue) -> {
            DETAIL = (int)((detail.getValue() * 99) + 1);
            try {
                updateRender();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        right1.getChildren().add(new VBox(detail_label, detail));

        Text app_settings = new Text("Application Settings");
        app_settings.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        Label v = new Label("Image Display");
        Slider view_og = new Slider(0, 1, 1);
        view_og.setMin(0);
        view_og.setMax(1);
        view_og.setValue(1);
        view_og.setMinorTickCount(0);
        view_og.setMajorTickUnit(1);
        view_og.setSnapToTicks(true);
        view_og.setShowTickMarks(true);
        view_og.setShowTickLabels(true);

        view_og.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "Original";

                return "Render";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Original":
                        return 0d;
                    case "Render":
                        return 1d;

                    default:
                        return 1d;
                }
            }
        });

        view_og.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (view_og.getValue() == 0) {
                imgPreview.setImage(convertToFxImage(ORIGINAL_IMAGE));
            } else if (view_og.getValue() == 1) {
                imgPreview.setImage(convertToFxImage(RENDERED_IMAGE));
            }
        }));

        right3.getChildren().add(app_settings);
        right3.getChildren().add(new VBox(v, view_og));

        right.getChildren().add(right2);
        right.getChildren().add(right1);
        right.getChildren().add(right3);

        StackPane bottomofright = new StackPane();
        bottomofright.setPadding(new Insets(0, 0, 20, 0));

        Button export = new Button("Export");
        export.setOnAction(e -> {
            String exportFolderAddress;
            configureDirectoryChooser(directoryChooser);
            File file = directoryChooser.showDialog(window);
            if (file != null) {
                exportFolderAddress = file.getAbsolutePath();
            }
            long timestamp = System.nanoTime();
            String exportFileName = Long.toString(timestamp);
            //System.out.println(exportFileName);
        });

        export.setMinWidth(150);
        bottomofright.getChildren().add(export);
        BorderPane.setAlignment(bottomofright, Pos.CENTER);

        right_outer_outer.setBottom(bottomofright);

        borderPane.setRight(right_outer_outer);

        //===============================================================

        Scene home = new Scene(borderPane, 1280, 720);
        window.setScene(home);
        window.show();

        window.widthProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                updateImgPreview(window);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        window.heightProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                updateImgPreview(window);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

    }

    /**
     * Opens file chooser to select files
     * @param fileChooser FileChooser object
     */
    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Select File");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    /**
     * Opens DirectoryChooser to choose directory to export file to
     * @param fileChooser DirectoryChooser object
     */
    private static void configureDirectoryChooser(final DirectoryChooser fileChooser){
        fileChooser.setTitle("Select Export Location");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private void updateImgPreview(Stage window) throws IOException {
        if (imgPreview != null) {
            double aspectRatio = (imgPreview.getImage().getWidth() / imgPreview.getImage().getHeight());

            //System.out.println(aspectRatio);
            if (aspectRatio > 1.5 && imgPreview.getFitHeight() < 0.7*window.getHeight()) {
                //System.out.println(1);
                imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                imgPreview.setFitWidth(window.getWidth()*0.45);
            } else if (aspectRatio <= 1.5 && imgPreview.getFitWidth() < window.getWidth()*0.45) {
                //System.out.println(2 + "\t" + imgPreview.getFitWidth());
                imgPreview.setFitHeight(window.getHeight()*0.7);
                imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
            } else {
                if (aspectRatio > 1.5) {
                    //System.out.println(3);
                    imgPreview.setFitHeight(window.getHeight()*0.7);
                    imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
                } else if (aspectRatio <= 1.5) {
                    //System.out.println(4 + "\t" + imgPreview.getFitWidth());
                    imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                    imgPreview.setFitWidth(window.getWidth()*0.45);
                }
            }
        }
    }

    private void updateImgPreview(Stage window, BufferedImage image) throws FileNotFoundException {
        //imgPreview.setImage());
        if (imgPreview != null) {
            double aspectRatio = (imgPreview.getImage().getWidth() / imgPreview.getImage().getHeight());

            //System.out.println(aspectRatio);
            if (aspectRatio > 1.5 && imgPreview.getFitHeight() < 0.7*window.getHeight()) {
                //System.out.println(1);
                imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                imgPreview.setFitWidth(window.getWidth()*0.45);
            } else if (aspectRatio <= 1.5 && imgPreview.getFitWidth() < window.getWidth()*0.45) {
                //System.out.println(2 + "\t" + imgPreview.getFitWidth());
                imgPreview.setFitHeight(window.getHeight()*0.7);
                imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
            } else {
                if (aspectRatio > 1.5) {
                    //System.out.println(3);
                    imgPreview.setFitHeight(window.getHeight()*0.7);
                    imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
                } else if (aspectRatio <= 1.5) {
                    //System.out.println(4 + "\t" + imgPreview.getFitWidth());
                    imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                    imgPreview.setFitWidth(window.getWidth()*0.45);
                }
            }
        }
    }

    private void updateImgPreview(Stage window, String fileAddress) throws FileNotFoundException {
        imgPreview.setImage(new Image(new FileInputStream(fileAddress)));
        if (imgPreview != null) {
            double aspectRatio = (imgPreview.getImage().getWidth() / imgPreview.getImage().getHeight());

            //System.out.println(aspectRatio);
            if (aspectRatio > 1.5 && imgPreview.getFitHeight() < 0.7*window.getHeight()) {
                //System.out.println(1);
                imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                imgPreview.setFitWidth(window.getWidth()*0.45);
            } else if (aspectRatio <= 1.5 && imgPreview.getFitWidth() < window.getWidth()*0.45) {
                //System.out.println(2 + "\t" + imgPreview.getFitWidth());
                imgPreview.setFitHeight(window.getHeight()*0.7);
                imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
            } else {
                if (aspectRatio > 1.5) {
                    //System.out.println(3);
                    imgPreview.setFitHeight(window.getHeight()*0.7);
                    imgPreview.setFitWidth(window.getHeight()*0.7 * aspectRatio);
                } else if (aspectRatio <= 1.5) {
                    //System.out.println(4 + "\t" + imgPreview.getFitWidth());
                    imgPreview.setFitHeight(window.getWidth()*0.45 / aspectRatio);
                    imgPreview.setFitWidth(window.getWidth()*0.45);
                }
            }
        }
    }

    private void printRenderSettings() {
        System.out.println("=================================================\nfileAddress: " + fileAddress + "\nCONTRAST: " + CONTRAST + "\nDOWNSCALE_RES: " + DOWN_RES_ARRAY[DOWNSCALE_RESOLUTION] + "\nBLUR: " + BLUR + "\nDETAIL: " + DETAIL + "\nFINAL_RES: " + FINAL_RES_ARRAY[FINAL_RESOLUTION] + "\n=================================================\n");
    }

    private String oldFileAddress = fileAddress;
    private static double oldContrast = CONTRAST, oldDetail = DETAIL;
    private static int oldDownScale = DOWNSCALE_RESOLUTION, oldFinalRes = FINAL_RESOLUTION;

    private void updateRender() throws IOException {
        if (imgPreview != null) {
            //only update when values change
            if (CONTRAST != oldContrast || DETAIL != oldDetail || oldFileAddress.compareTo(fileAddress) != 0 || oldDownScale != DOWNSCALE_RESOLUTION || oldFinalRes != FINAL_RESOLUTION) {
                printRenderSettings();
                RENDERED_IMAGE = Renderer.displayRenderFrame(fileAddress, CONTRAST, DOWN_RES_ARRAY[DOWNSCALE_RESOLUTION], BLUR, DETAIL, FINAL_RES_ARRAY[FINAL_RESOLUTION]);

                oldFileAddress = fileAddress;
                oldContrast = CONTRAST;
                oldDetail = DETAIL;
                oldDownScale = DOWNSCALE_RESOLUTION;
                oldFinalRes = FINAL_RESOLUTION;

                imgPreview.setImage(convertToFxImage(RENDERED_IMAGE));
            }
        }
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

    private static JFrame frame;
    private static JLabel label;
    /**
     * Used in testing to view images
     * @param image
     */
    public static void display(BufferedImage image) {
        if (frame == null) {
            frame = new JFrame();
            frame.setTitle("stained_image");
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        } else label.setIcon(new ImageIcon(image));
    }

}