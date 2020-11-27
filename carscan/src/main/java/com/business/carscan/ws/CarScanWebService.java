package com.business.carscan.ws;

import com.business.carscan.iservice.IAwsUploadService;
import com.business.carscan.model.MobileModel;
import com.business.carscan.model.WebServiceResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by vikas on 23-11-2020.
 */
@RestController
@RequestMapping(value = "/cs-ws")
public class CarScanWebService {

        private org.slf4j.Logger logger = LoggerFactory.getLogger(CarScanWebService.class);

        @Value("${carscan.aws.bucket}")
        private String awsBucketName;

        @Inject
        private IAwsUploadService awsUploadService;

        @PostMapping("/upload-image")
        public ResponseEntity<?> uploadImage(@RequestParam("imageFile") MultipartFile imageFile){
            WebServiceResponse webServiceResponse = null;
            String uploadS3Message = "";
            try{
                MobileModel mobileModel = MobileModel.builder().awsBucketName(awsBucketName).imageFile(imageFile).build();
                uploadS3Message = awsUploadService.uploadFileToS3Bucket(mobileModel);
                webServiceResponse =
                        WebServiceResponse.builder().responseCode(HttpStatus.OK.value()).responseMessage(uploadS3Message).build();
                logger.info(webServiceResponse.getResponseMessage());
                return new ResponseEntity<>(webServiceResponse,HttpStatus.INTERNAL_SERVER_ERROR);
            }catch(Exception e){
                logger.error("Exception with ",e.getMessage());
                webServiceResponse =
                        WebServiceResponse.builder().responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).responseMessage(e.getMessage()).build();
                return new ResponseEntity<>(webServiceResponse,HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
}
