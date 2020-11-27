package com.ws;

import com.business.carscan.iservice.IAwsUploadService;
import com.business.carscan.model.MobileModel;
import com.business.carscan.model.WebServiceResponse;
import com.business.carscan.ws.CarScanWebService;
import com.common.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vikas on 27-11-2020.
 */
@RunWith(MockitoJUnitRunner.class)
public class CarScanWebServiceTest extends BaseTest{

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private IAwsUploadService awsUploadService;

    @InjectMocks
    private CarScanWebService carScanWebService;

    @Captor
    private ArgumentCaptor<MobileModel> mobileModelArgumentCaptor;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(carScanWebService).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void uploadImageShouldSucceed() throws Exception {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("imageFile",new FileInputStream(getDummyFile()));
        when(awsUploadService.uploadFileToS3Bucket(mobileModelArgumentCaptor.capture())).thenReturn(SUCCESS_MESSAGE);
        //when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload("/cs-ws/upload-image").file(multipartFile);
        MvcResult result = mockMvc.perform(builder).andReturn();
        WebServiceResponse webServiceResponse = objectMapper.readValue(result.getResponse().getContentAsString(),WebServiceResponse.class);

        //then
        assertNotNull(webServiceResponse);
        assertThat(webServiceResponse.getResponseCode(),equalTo(HttpStatus.SC_OK));
        assertThat(webServiceResponse.getResponseMessage(),equalTo(SUCCESS_MESSAGE));
    }

    @Test
    public void uploadImageShouldGiveInternalExceptionError() throws Exception {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("imageFile",new FileInputStream(getDummyFile()));
        doThrow(IOException.class).when(awsUploadService).uploadFileToS3Bucket(mobileModelArgumentCaptor.capture());
        //when
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload("/cs-ws/upload-image").file(multipartFile);
        MvcResult result = mockMvc.perform(builder).andReturn();
        WebServiceResponse webServiceResponse = objectMapper.readValue(result.getResponse().getContentAsString(),WebServiceResponse.class);
        assertNotNull(webServiceResponse);
        assertThat(webServiceResponse.getResponseCode(),equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }
}
