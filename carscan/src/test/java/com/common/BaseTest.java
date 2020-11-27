package com.common;

import java.io.File;

/**
 * Created by vikas on 27-11-2020.
 */
public abstract class BaseTest {

    protected final String DUMMY_BUCKET_NAME = "sample-bucket-name";
    protected final String SUCCESS_MESSAGE = "File uploaded successfully in s3 bucket";
    protected final String AWS_SERVICE_EXCEPTION = "Caught an AmazonServiceException from PUT requests, rejected reasons:";
    protected final String AWS_CLIENT_EXCEPTION = "Caught an AmazonClientException: ";

    protected File getDummyFile(){
        File file = new File(
                getClass().getClassLoader().getResource("images/dummy.jpg").getFile()
        );
        return file;
    }
}
