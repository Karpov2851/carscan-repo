package com.imageresizing;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.imageresizing.constants.ImageTypes;
import com.imageresizing.util.ImageResizerHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vikas on 25-11-2020.
 */
public class ImageResizing implements RequestHandler<S3Event,String>{

    private static final float MAX_WIDTH = 400;
    private static final float MAX_HEIGHT = 400;
    private static Properties properties;
    private LambdaLogger logger;
    static {
        try {
            properties = ImageResizerHelper.loadProperties("imageresize.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        try{
            logger = context.getLogger();
            logger.log("Handler request got invoked");

            BasicAWSCredentials creds = new BasicAWSCredentials(String.valueOf(properties.get("awsAccessKey")),
                    String.valueOf(properties.get("awsSecretKey")));
            S3EventNotificationRecord record = s3Event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();
            String srcKey = record.getS3().getObject().getUrlDecodedKey();
            String destBucket = srcBucket + "-resized";
            String dstKey = "resized-" + srcKey + "-" + new Date().getTime();

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(Regions.AP_SOUTH_1).build();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    srcBucket, srcKey));
            if (ImageResizerHelper.isSameBucket(srcBucket,destBucket)) {
                logger.log("Destination bucket must not match source bucket.");
                return "";
            }
            Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(srcKey);
            if (!matcher.matches()) {
                logger.log("Unable to infer image type for key " + srcKey);
                return "";
            }
            String imageType = matcher.group(1);
            if (ImageResizerHelper.isFileAnImage(imageType)) {
                logger.log("Skipping non-image " + srcKey);
                return "";
            }
            // Download the image from S3 into a stream

            InputStream objectData = s3Object.getObjectContent();


            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageResizerHelper.scaledImageByteOutputStream(imageType,os,objectData,MAX_WIDTH,MAX_HEIGHT);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            // Set Content-Length and Content-Type
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(os.size());
            if (ImageTypes.JPG.getMimeTypeDesc().equals(imageType)) {
                meta.setContentType(ImageTypes.JPEG_MIME.getMimeTypeDesc());
            }
            if (ImageTypes.PNG.getMimeTypeDesc().equals(imageType)) {
                meta.setContentType(ImageTypes.PNG_MIME.getMimeTypeDesc());
            }

            // Uploading to S3 destination bucket
            logger.log("Writing to: " + destBucket + "/" + dstKey);
            try {
                s3Client.putObject(destBucket, dstKey, is, meta);
                s3Client.deleteObject(new DeleteObjectRequest(srcBucket, srcKey));
            }
            catch(AmazonServiceException e)
            {
                logger.log(String.format("AmazonServiceException with %s",e.getMessage()));
                System.exit(1);
            }

            return "Ok";
        }catch(IOException e){
            logger.log(String.format("IOException with %s",e.getMessage()));
            throw new RuntimeException();
        }

    }
}
