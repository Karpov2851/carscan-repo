package com.business.carscan.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by vikas on 23-11-2020.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MobileModel{

    private MultipartFile imageFile;
    private String awsBucketName;


}
