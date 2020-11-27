package com.business.carscan.model;

import lombok.*;

/**
 * Created by vikas on 23-11-2020.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WebServiceResponse {

    private int responseCode;
    private String responseMessage;

}
