package com.example;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoFrameConversion {

    public static int generateFramesFromVideo(String videoAddress, String frameStorageFolder) throws FrameGrabber.Exception, IOException {
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(videoAddress);
        g.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();

        System.out.println(g.getLengthInFrames());

        int length = g.getLengthInFrames();
        int lastFrameNumber = 0;
        for (int i = 0 ; i < length; i++) {
            g.setVideoFrameNumber(i);
            BufferedImage frame = converter.convert(g.grab());
            if (frame != null) {
                ImageIO.write(frame, "jpg", new File(frameStorageFolder + "\\frame-" + i + ".jpg"));
                lastFrameNumber = i;
            }
        }

        g.stop();
        return lastFrameNumber;
    }

    public static double getFrameRate(String videoAddress) throws FrameGrabber.Exception {
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(videoAddress);
        g.start();
        double fps = g.getFrameRate();
        g.stop();
        System.out.println(fps);
        return fps;
    }

    public static void convertJPGtoMovie(String vidPath, String frameStorageFolder, double fps, int numOfFrames) throws IOException {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(new File(vidPath), (int)Math.round(fps));
        int i = 0;
        while (i < numOfFrames)
        {
            enc.encodeImage(ImageIO.read(new File(frameStorageFolder + "\\frame-" + i + ".jpg")));
            System.out.println("Loaded Frame " + i);
            i++;
        }
        enc.finish();
    }

}
