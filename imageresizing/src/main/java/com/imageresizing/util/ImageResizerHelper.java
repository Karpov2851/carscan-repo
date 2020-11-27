package com.imageresizing.util;

import com.imageresizing.constants.ImageTypes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vikas on 26-11-2020.
 */
public class ImageResizerHelper {

    private ImageResizerHelper(){

    }

    public static boolean isSameBucket(String srcBucket,String destinationBucket){
        return srcBucket.equals(destinationBucket);
    }

    public static boolean isFileAnImage(String imageType){
        return !(ImageTypes.JPG.getMimeTypeDesc().equals(imageType)) && !(ImageTypes.PNG.getMimeTypeDesc().equals(imageType));
    }

    public static Properties loadProperties(String resourceFileName) throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = ImageResizerHelper.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);
        configuration.load(inputStream);
        inputStream.close();
        return configuration;
    }

    public static void scaledImageByteOutputStream(String imageType,ByteArrayOutputStream os,InputStream objectContent,float originalWidth,float originalHeight)throws IOException{
        BufferedImage srcImage = ImageIO.read(objectContent);
        int srcHeight = srcImage.getHeight();
        int srcWidth = srcImage.getWidth();
        // Infer the scaling factor to avoid stretching the image
        // unnaturally
        float scalingFactor = Math.min(originalWidth / srcWidth, originalHeight
                / srcHeight);
        int width = (int) (scalingFactor * srcWidth);
        int height = (int) (scalingFactor * srcHeight);

        BufferedImage resizedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        // Fill with white before applying semi-transparent (alpha) images
        g.setPaint(Color.white);
        g.fillRect(0, 0, width, height);
        // Simple bilinear resize
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(srcImage, 0, 0, width, height, null);
        g.dispose();

        ImageIO.write(resizedImage, imageType, os);
    }



}
