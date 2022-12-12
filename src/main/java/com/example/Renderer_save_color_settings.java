package com.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Renderer_save_color_settings {

    public static void main(String[] args) throws IOException {
        Renderer_save_color_settings r = new Renderer_save_color_settings();
    }

    public Renderer_save_color_settings() throws IOException {

        File file = new File("C:\\Users\\Alex Kranias\\Downloads\\IMG_5274.png");
        BufferedImage frame = ImageIO.read(file);

        frame = resize(frame, 1000, 1000);

        renderFrame(frame, RENDER_COLOR_TYPE.Color);

    }

    /* Color = original image colors unaltered, BW = renders the image as a Black and White image (removes all color)

    This effects how the algorithm determines the contrast between two pixels. By making the image in black and white,
    the only form of contrast detection available to the algorithm is the difference in brightness between two pixels;
    however, by rendering the image in color, the algorithim compares both the color and brightness differences between
    two pixels. */
    static enum RENDER_COLOR_TYPE {Color, BW};
    static RENDER_COLOR_TYPE COLOR_TYPE = RENDER_COLOR_TYPE.BW; //default is BW

    public void renderFrame(BufferedImage frame, RENDER_COLOR_TYPE color_type) throws IOException {

        COLOR_TYPE = color_type;

        if (COLOR_TYPE == RENDER_COLOR_TYPE.BW) {

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
                    double[] rgb_standardized = {rgb[0] / 255.0, rgb[1] / 255.0, rgb[2] / 255.0};

                    //Add Contrast
                    double CONSTRAST_CONSTANT = 1.5;
                    rgb_standardized[0] = Math.pow(rgb_standardized[0], CONSTRAST_CONSTANT);
                    rgb_standardized[1] = Math.pow(rgb_standardized[1], CONSTRAST_CONSTANT);
                    rgb_standardized[2] = Math.pow(rgb_standardized[2], CONSTRAST_CONSTANT);

                    //Convert Back to Normal Values
                    rgb[0] = (int) (rgb_standardized[0] * 255);
                    rgb[1] = (int) (rgb_standardized[1] * 255);
                    rgb[2] = (int) (rgb_standardized[2] * 255);

                    //only set to one value since all RGB values are the same
                    imagePixels[j][i] = rgb[0];

                    //System.out.println("Red: " + rgb[0] + "  Green: " + rgb[1] + "  Blue: " + rgb[2]);
                    //frame.setRGB(i, j, toARGB(255, rgb));

                }
            }

            imagePixels = blur(imagePixels, 5);

            int[][][] derivatives = getDerivativeBrightness(imagePixels);

            for (int j = 0; j < imagePixels.length; j++) {
                for (int i = 0; i < imagePixels[0].length; i++) {
                    if (derivatives[j][i][0] >= 20 || derivatives[j][i][1] >= 20) imagePixels[j][i] = 0;
                    else imagePixels[j][i] = 255;
                }
            }

            for (int j = 0; j < imagePixels.length; j++) {
                for (int i = 0; i < imagePixels[0].length; i++) {
                    frame.setRGB(i, j, toARGB(255, imagePixels[j][i]));
                }
            }

        }
        else if (COLOR_TYPE == RENDER_COLOR_TYPE.Color) {

            int[][][] imagePixels = new int[frame.getHeight()][frame.getWidth()][3]; //technically, if I bitshift like what's done in the Color class I could make the RGB value a single int instead of needing 3 seperate elements for each color and that would likely save memory at the possible expensive of computing speed?

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
                    double[] rgb_standardized = {rgb[0] / 255.0, rgb[1] / 255.0, rgb[2] / 255.0};

                    //Add Contrast
                    double CONSTRAST_CONSTANT = 1.5;
                    rgb_standardized[0] = Math.pow(rgb_standardized[0], CONSTRAST_CONSTANT);
                    rgb_standardized[1] = Math.pow(rgb_standardized[1], CONSTRAST_CONSTANT);
                    rgb_standardized[2] = Math.pow(rgb_standardized[2], CONSTRAST_CONSTANT);

                    //Convert Back to Normal Values
                    rgb[0] = (int) (rgb_standardized[0] * 255);
                    rgb[1] = (int) (rgb_standardized[1] * 255);
                    rgb[2] = (int) (rgb_standardized[2] * 255);

                    //give each pixel it's respective RGB values
                    imagePixels[j][i][0] = rgb[0];
                    imagePixels[j][i][1] = rgb[1];
                    imagePixels[j][i][2] = rgb[2];

                    //System.out.println("Red: " + rgb[0] + "  Green: " + rgb[1] + "  Blue: " + rgb[2]);
                    //frame.setRGB(i, j, toARGB(255, rgb));

                }
            }

            imagePixels = blur(imagePixels, 5);

            int[][][][] derivatives = getDerivativeBrightness(imagePixels);

            for (int j = 0; j < imagePixels.length; j++) {
                for (int i = 0; i < imagePixels[0].length; i++) {
                    double y_derivative = Math.sqrt(Math.pow(derivatives[j][i][0][0], 2) + Math.pow(derivatives[j][i][0][1], 2) + Math.pow(derivatives[j][i][0][2], 2));
                    double x_derivative = Math.sqrt(Math.pow(derivatives[j][i][1][0], 2) + Math.pow(derivatives[j][i][1][1], 2) + Math.pow(derivatives[j][i][1][2], 2));

                    if (y_derivative >= 20 || x_derivative >= 20) {
                        imagePixels[j][i][0] = 0;
                        imagePixels[j][i][1] = 0;
                        imagePixels[j][i][2] = 0;
                    }
                    else {
                        imagePixels[j][i][0] = 255;
                        imagePixels[j][i][1] = 255;
                        imagePixels[j][i][2] = 255;
                    }
                }
            }


            for (int j = 0; j < imagePixels.length; j++) {
                for (int i = 0; i < imagePixels[0].length; i++) {
                    frame.setRGB(i, j, toARGB(255, imagePixels[j][i]));
                }
            }

        }

        frame = resize(frame, 1000, 1000);

        App.display(frame);

    }

    /**
     * Returns the derivative brightness values in the x and y direction for every pixel in an image.
     * @param pixels The black and white RGB values of every pixel in an image
     * @return A 3-dimensional array where the first two arrays represent each pixel in an image and the third array is composed of two values: the 1st element is d(brightness)/dy and the 2nd element is d(brightness)/dx
     */
    private int[][][] getDerivativeBrightness(int[][] pixels) {

        int db_dy, db_dx;
        int[][][] derivatives = new int[pixels.length][pixels[0].length][2];

        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {

                int top, bottom, left, right; //values of pixels above, below, and to the sides of the center pixel

                if (j == 0) top = pixels[j][i];
                else top = pixels[j-1][i];
                if (j == pixels.length - 1) bottom = pixels[j][i];
                else bottom = pixels[j+1][i];
                if (i == 0) left = pixels[j][i];
                else left = pixels[j][i-1];
                if (i == pixels[0].length-1) right = pixels[j][i];
                else right = pixels[j][i+1];

                db_dy = top - bottom;
                db_dx = right - left;

                derivatives[j][i][0] = db_dy;
                derivatives[j][i][1] = db_dx;

                //System.out.print(derivatives[j][i][0] + "," + derivatives[j][i][1] + "\t\t\t");
            }
            System.out.println("\n");
        }
        return derivatives;
    }

    /**
     * Returns the derivative brightness values in the x and y direction for every pixel in an image.
     * @param pixels The R, G, and B values of every pixel in an image
     * @return A 4-dimensional array where the first two arrays represent each pixel in an image and the third array is composed of two values: the 1st array is a change in R, G, and B for a change in y and the 2nd element is a change in R, G, and B for a change in x
     */
    private int[][][][] getDerivativeBrightness(int[][][] pixels) {

        int[] dRGB_dy, dRGB_dx;
        int[][][][] derivatives = new int[pixels.length][pixels[0].length][2][3];

        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {

                int[] top, bottom, left, right; //values of pixels above, below, and to the sides of the center pixel

                if (j == 0) top = pixels[j][i];
                else top = pixels[j-1][i];
                if (j == pixels.length - 1) bottom = pixels[j][i];
                else bottom = pixels[j+1][i];
                if (i == 0) left = pixels[j][i];
                else left = pixels[j][i-1];
                if (i == pixels[0].length-1) right = pixels[j][i];
                else right = pixels[j][i+1];

                dRGB_dy = getDifferenceRGB(top, bottom);
                dRGB_dx = getDifferenceRGB(right, left);

                derivatives[j][i][0] = dRGB_dy;
                derivatives[j][i][1] = dRGB_dx;

                //System.out.print(derivatives[j][i][0] + "," + derivatives[j][i][1] + "\t\t\t");
            }
            //System.out.println("\n");
        }
        return derivatives;
    }

    /**
     * Outputs the "difference" in RGB values between two pixels by using the distance formula in 3 dimensions
     * @param pixel_1 The R, G, and B value of a pixel
     * @param pixel_2 The R, G, and B value of a pixel
     * @return The calculated difference between R, G, and B values of pixel_1 and pixel_2
     */
    private static int[] getDifferenceRGB(int[] pixel_1, int[] pixel_2) {
        int[] RGB = {pixel_1[0] - pixel_2[0], pixel_1[1] - pixel_2[1], pixel_1[2] - pixel_2[2]};
        return RGB;
    }

    /**
     * The following method takes a 2-dimensional array of integers that represents the black and white
     * RGB value of every pixel in an image and this method applies a guassian blur by taking the average
     * rgb value of a n x n grid around each pixel and assigning that value to the center pixel.
     * @param pixels The black and white RGB values of every pixel in an image
     * @param grid_size The side-length number of pixels that compose the pixel grid surrounding each pixel being blurred. The greater this value the more blurred the image is.
     * @return The black and white RGB values of every pixel in the blurred image.
     */
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
                int count = 0;
                for (int y = j - ((grid_size-1)/2); y <= j + ((grid_size-1)/2); y++) {

                    int y_cut = y;

                    if (y < 0) y_cut = 0;
                    else if (y >= pixels.length) y_cut = pixels.length - 1;

                    for (int x = i - ((grid_size-1)/2); x <= i + ((grid_size-1)/2); x++) {

                        int x_cut = x;

                        if (x < 0) x_cut = 0;
                        else if (x >= pixels[0].length) x_cut = pixels[0].length - 1;

                        average += pixels_copy[y_cut][x_cut];
                        count++;

                    }

                }
                average /= count;

                pixels_copy[j][i] = average;

            }

        }

        return pixels_copy;
    }

    /**
     * The following method takes a 3-dimensional array of integers that contains
     * R, G, and B values of every pixel in an image and this method applies a guassian blur by taking the average
     * rgb value of a n x n grid around each pixel and assigning that value to the center pixel.
     * @param pixels The RGB values of every pixel in an image
     * @param grid_size The side-length number of pixels that compose the pixel grid surrounding each pixel being blurred. The greater this value the more blurred the image is.
     * @return The black and white RGB values of every pixel in the blurred image.
     */
    private static int[][][] blur(int[][][] pixels, int grid_size) {

        //make sure grid size is not even and not nonesense (must be odd so that middle pixel is in center
        if (grid_size <= 0) grid_size = 1;
        else if (grid_size % 2 == 0) grid_size += 1;

        //create a copy
        int[][][] pixels_copy = new int[pixels.length][pixels[0].length][3];
        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {
                pixels_copy[j][i][0] = pixels[j][i][0];
                pixels_copy[j][i][1] = pixels[j][i][1];
                pixels_copy[j][i][2] = pixels[j][i][2];
            }
        }

        //blur
        for (int j = 0; j < pixels.length; j++) {
            for (int i = 0; i < pixels[0].length; i++) {

                int[] average = {0, 0 ,0};
                int count = 0;
                for (int y = j - ((grid_size-1)/2); y <= j + ((grid_size-1)/2); y++) {

                    int y_cut = y;

                    if (y < 0) y_cut = 0;
                    else if (y >= pixels.length) y_cut = pixels.length - 1;

                    for (int x = i - ((grid_size-1)/2); x <= i + ((grid_size-1)/2); x++) {

                        int x_cut = x;

                        if (x < 0) x_cut = 0;
                        else if (x >= pixels[0].length) x_cut = pixels[0].length - 1;

                        average[0] += pixels_copy[y_cut][x_cut][0];
                        average[1] += pixels_copy[y_cut][x_cut][1];
                        average[2] += pixels_copy[y_cut][x_cut][2];
                        count++;

                    }

                }
                average[0] /= count;
                average[1] /= count;
                average[2] /= count;

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
        return (int)((.5 * rgb[0]) + (.9 * rgb[1]) + (0 * rgb[2]));
    }
/*
    private static int RGBtoGrayscale(int[] rgb) {
        return (int)((0.3 * rgb[0]) + (0.59 * rgb[1]) + (0.11 * rgb[2]));
    }

 */

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

    private static int toARGB(int alpha, int grayscale) {

        int argb = 0;

        argb += alpha;
        argb = argb << 8;
        argb += grayscale;
        argb = argb << 8;
        argb += grayscale;
        argb = argb << 8;
        argb += grayscale;

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

    /**
     * Found @ https://stackoverflow.com/questions/9417356/bufferedimage-resize
     * @param img
     * @param newW
     * @param newH
     * @return
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
