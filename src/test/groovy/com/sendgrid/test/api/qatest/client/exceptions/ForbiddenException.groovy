package com.sendgrid.test.api.qatest.client.exceptions

class ForbiddenException extends RuntimeException {
    ForbiddenException(def response) {
        super("FORBIDDEN: 403 Response from server:  ${response}")
    }
}