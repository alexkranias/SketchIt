package com.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Renderer {

    public Renderer() throws IOException {

        File file = new File("C:\\Users\\Alex Kranias\\Desktop\\fsf.jpg");
        BufferedImage frame = ImageIO.read(file);

        renderFrame(frame);

    }

    public void renderFrame(BufferedImage frame) throws IOException {


        int[][] imagePixels = new int[frame.getHeight()][frame.getWidth()];
        for (int j = 0; j < imagePixels.length; j++) {
            for (int i = 0; i < imagePixels[0].length; i++) {

                int[] rgb = getRGBfromBufferImage(frame, i, j);

                int grayscaled = RGBtoGrayscale(rgb);
                rgb[0] = grayscaled;
                rgb[1] = grayscaled;
                rgb[2] = grayscaled;

                //System.out.println("Red: " + rgb[0] + "  Green: " + rgb[1] + "  Blue: " + rgb[2]);

                frame.setRGB(i, j, toARGB(255, rgb));


            }
        }

        App.display(frame);

    }

    /**
     * Saves a BufferedImage as a JPG file at a specified file address
     * @param img An image
     * @param address Absolute path (including the file itself) of where the file will be stored.
     * @throws IOException
     */
    private static void saveAsJPG(BufferedImage img, String address) throws IOException {
        File outputfile = new File(address);
        ImageIO.write(img, "jpg", outputfile);
    }

    /**
     * Using an equation found on https://stackoverflow.com/questions/17615963/standard-rgb-to-grayscale-conversion
     * the following method converts RGB values in an array to their grayscale equivalent
     * @param rgb The three-element int array containing the RGB values of a pixel in the order: red, green, blue.
     * @return The int RGB value that the red, green, and blue values should be set to for the pixel's grayscale equivalent color.
     */
    private static int RGBtoGrayscale(int[] rgb) {
        return (int)((0.3 * rgb[0]) + (0.59 * rgb[1]) + (0.11 * rgb[2]));
    }

    private static int toARGB(int alpha, int[] rgb) {

        int argb = 0;

        argb += alpha;
        argb = argb << 8;
        argb += rgb[0];
        argb = argb << 8;
        argb += rgb[1];
        argb = argb << 8;
        argb += rgb[2];

        return argb;
        /*
        The << symbol is for BIT SHIFTING,
        it makes it so the RGB data is a single sequence of bits but it shifts each int.
        So it does this => aaaaaaaarrrrrrrrggggggggbbbbbbbb where each letter corresponds to where the bits will go.
         */
    }

    private static int[] getRGBfromBufferImage(BufferedImage frame, int x, int y) {

        int argb = frame.getRGB(x, y);

        int red = (argb >> 16) & 0x000000FF;
        int green = (argb >>8 ) & 0x000000FF;
        int blue = (argb) & 0x000000FF;

        int[] rgb = {red, green, blue};

        return rgb;
        /*
        The << symbol is for BIT SHIFTING,
        it makes it so the RGB data is a single dequence of bits but it shifts each int.
        So it does this => aaaaaaaarrrrrrrrggggggggbbbbbbbb where each letter corresponds to where the bits will go.
         */
    }

}
