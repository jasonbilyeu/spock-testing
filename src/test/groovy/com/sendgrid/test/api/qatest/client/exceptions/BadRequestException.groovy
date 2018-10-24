package com.sendgrid.test.api.qatest.client.exceptions

class BadRequestException extends RuntimeException {
    BadRequestException(def response) {
        super("BAD_REQUEST: 400 Response from server:  ${response}")
    }
}
