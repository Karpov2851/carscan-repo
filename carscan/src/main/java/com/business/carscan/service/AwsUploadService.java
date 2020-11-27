package com.business.carscan.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.business.carscan.iservice.IAwsUploadService;
import com.business.carscan.model.MobileModel;
import com.business.carscan.util.FileUtil;
import com.business.carscan.ws.CarScanWebService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Created by vikas on 23-11-2020.
 */
@Service
public class AwsUploadService implements IAwsUploadService{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(AwsUploadService.class);

    @Inject
    private AmazonS3 customS3Client;

    @Override
    public String uploadFileToS3Bucket(MobileModel mobileModel) throws IOException {
        StringBuilder uploadS3MessageBuilder = new StringBuilder();
        try {

            File imageFile = FileUtil.convertMultipartFileToFileFormat(mobileModel.getImageFile());
            String fileNameS3 = FileUtil.generateFileName(mobileModel.getImageFile());
            customS3Client.putObject(new PutObjectRequest(mobileModel.getAwsBucketName(),fileNameS3, imageFile));
            uploadS3MessageBuilder.append("File uploaded successfully in s3 bucket");
        } catch (AmazonServiceException ase) {

            uploadS3MessageBuilder.append("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            uploadS3MessageBuilder.append("\n");
            uploadS3MessageBuilder
                    .append(String.format("Error message is :%s ",ase.getMessage()));
            uploadS3MessageBuilder
                    .append(String.format("HTTP Status Code: %d",ase.getStatusCode()));
            uploadS3MessageBuilder
                    .append(String.format("HTTP Status Code: %d",ase.getStatusCode()));
            uploadS3MessageBuilder
                    .append(String.format("AWS Error Code: %d",ase.getStatusCode()));
            uploadS3MessageBuilder
                    .append(String.format("AWS Error Code: %d",ase.getStatusCode()));

        } catch (AmazonClientException ace) {
            uploadS3MessageBuilder.append("Caught an AmazonClientException: ");
            uploadS3MessageBuilder.append(String.format("Error Message: %s",ace.getMessage()));
        } catch (IOException e) {
            logger.error("IOException caused by ",e.getMessage());
            throw new IOException("IOException while converting multipartFile");
        }
        logger.error(uploadS3MessageBuilder.toString());
        return uploadS3MessageBuilder.toString();
    }
}
