package com.sendgrid.test.api.qatest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private String result;
    private String message;
    private Long userid;
}
