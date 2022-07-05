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

                //Retrieve Color for Pixel
                int[] rgb = getRGBfromBufferImage(frame, i, j);

                //Grayscale Said Pixel
                int grayscaled = RGBtoGrayscale(rgb);
                rgb[0] = grayscaled;
                rgb[1] = grayscaled;
                rgb[2] = grayscaled;

                //"Standardize" Value of Pixel so range is 0-1
                double[] rgb_standardized = {rgb[0] / 255.0, rgb[0] / 255.0, rgb[0] / 255.0};

                //Add Contrast
                double CONSTRAST_CONSTANT = 1;
                rgb_standardized[0] = Math.pow(rgb_standardized[0], CONSTRAST_CONSTANT);
                rgb_standardized[1] = Math.pow(rgb_standardized[1], CONSTRAST_CONSTANT);
                rgb_standardized[2] = Math.pow(rgb_standardized[2], CONSTRAST_CONSTANT);

                //Convert Back to Normal Values
                rgb[0] = (int)(rgb_standardized[0] * 255);
                rgb[1] = (int)(rgb_standardized[1] * 255);
                rgb[2] = (int)(rgb_standardized[2] * 255);

                //only set to one value since all RGB values are the same
                imagePixels[i][j] = rgb[0];

                //System.out.println("Red: " + rgb[0] + "  Green: " + rgb[1] + "  Blue: " + rgb[2]);
                //frame.setRGB(i, j, toARGB(255, rgb));

            }
        }

        App.display(frame);

    }

    //grid size can only be an odd number
    private static int[][] blur(int[][] pixels, int grid_size) {

        //make sure grid size is not even and not nonesense (must be odd so that middle pixel is in center
        if (grid_size <= 0) grid_size = 1;
        else if (grid_size % 2 == 0) grid_size += 1;

        //create a copy
        int[][] pixels_copy = new int[pixels.length][pixels[0].length];
        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {
                pixels_copy[j][i] = pixels[j][i];
            }
        }

        //blur
        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {

                int average = 0;
                for (int y = j - ((grid_size-1)/2); y < j + ((grid_size-1)/2); y++) {
                    if (y < 0) y = 0;
                    else if (y >= pixels.length) y = pixels.length - 1;

                    for (int x = i - ((grid_size-1)/2); x < i + ((grid_size-1)/2); x++) {
                        if (x < 0) x = 0;
                        else if (x >= pixels[0].length) x = pixels[0].length - 1;

                        average += pixels_copy[y][x];

                    }

                }
                average /= (int)Math.pow(grid_size, 2);

                pixels_copy[j][i] = average;

            }
        }

        return pixels_copy;
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
