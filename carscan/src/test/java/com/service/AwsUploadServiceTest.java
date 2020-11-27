package com.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.business.carscan.model.MobileModel;
import com.business.carscan.service.AwsUploadService;
import com.business.carscan.util.FileUtil;
import com.common.BaseTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vikas on 27-11-2020.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtil.class)
public class AwsUploadServiceTest extends BaseTest{



    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private AwsUploadService awsUploadService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor;


    @Test
    public void uploadFileToS3BucketShouldSucceed() throws IOException{
        //given
        MultipartFile multipartFile = new MockMultipartFile("dummy",new FileInputStream(getDummyFile()));
        MobileModel mobileModel = MobileModel.builder().awsBucketName(DUMMY_BUCKET_NAME).imageFile(multipartFile).build();
        PowerMockito.mockStatic(FileUtil.class);
        when(amazonS3.putObject(putObjectRequestArgumentCaptor.capture())).thenReturn(mock(PutObjectResult.class));
        PowerMockito.when(FileUtil.convertMultipartFileToFileFormat(multipartFile)).thenReturn(getDummyFile());
        PowerMockito.when(FileUtil.generateFileName(Mockito.any())).thenReturn("custom-file-name");
        //when
        String actualMessage = awsUploadService.uploadFileToS3Bucket(mobileModel);

        //then
        assertThat(actualMessage,equalTo(SUCCESS_MESSAGE));
        assertThat(putObjectRequestArgumentCaptor.getValue().getBucketName(),equalTo(DUMMY_BUCKET_NAME));

    }


    @Test
    public void uploadFileToS3BucketShouldGiveAmazonServiceExceptionWhenBucketNameIsEmpty() throws IOException{

        //given
        MultipartFile multipartFile = new MockMultipartFile("dummy",new FileInputStream(getDummyFile()));
        MobileModel mobileModel = MobileModel.builder().awsBucketName("").imageFile(multipartFile).build();
        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.convertMultipartFileToFileFormat(multipartFile)).thenReturn(getDummyFile());
        PowerMockito.when(FileUtil.generateFileName(Mockito.any())).thenReturn("custom-file-name");
        Mockito.doThrow(AmazonServiceException.class).when(amazonS3).putObject(putObjectRequestArgumentCaptor.capture());

        //when
        String actualMessage = awsUploadService.uploadFileToS3Bucket(mobileModel);

        //then
        assertThat(actualMessage,containsString(AWS_SERVICE_EXCEPTION));
    }

    @Test
    public void uploadFileToS3BucketShouldGiveAmazonClientExceptionWhenBucketNameIsEmpty() throws IOException{

        //given
        MultipartFile multipartFile = new MockMultipartFile("dummy",new FileInputStream(getDummyFile()));
        MobileModel mobileModel = MobileModel.builder().awsBucketName("").imageFile(multipartFile).build();
        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.convertMultipartFileToFileFormat(multipartFile)).thenReturn(getDummyFile());
        PowerMockito.when(FileUtil.generateFileName(Mockito.any())).thenReturn("custom-file-name");
        Mockito.doThrow(AmazonClientException.class).when(amazonS3).putObject(putObjectRequestArgumentCaptor.capture());

        //when
        String actualMessage = awsUploadService.uploadFileToS3Bucket(mobileModel);

        //then
        assertThat(actualMessage,containsString(AWS_CLIENT_EXCEPTION));
    }



}
