package com.business.carscan.iservice;

import com.business.carscan.model.MobileModel;

import java.io.IOException;

/**
 * Created by vikas on 23-11-2020.
 */
public interface IAwsUploadService {
    String uploadFileToS3Bucket(MobileModel mobileModel) throws IOException;
}
