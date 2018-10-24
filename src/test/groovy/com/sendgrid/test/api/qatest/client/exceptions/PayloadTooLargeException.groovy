package com.sendgrid.test.api.qatest.client.exceptions

class PayloadTooLargeException extends RuntimeException {
    PayloadTooLargeException(def response) {
        super("PAYLOAD_TOO_LARGE: 413 Response from server:  ${response}")
    }
}
