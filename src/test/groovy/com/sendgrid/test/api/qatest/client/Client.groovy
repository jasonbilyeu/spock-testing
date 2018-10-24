package com.sendgrid.test.api.qatest.client

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.sendgrid.test.api.qatest.client.exceptions.ForbiddenException
import com.sendgrid.test.api.qatest.client.exceptions.NotFoundException
import com.sendgrid.test.api.qatest.client.exceptions.BadRequestException
import com.sendgrid.test.api.qatest.client.exceptions.PayloadTooLargeException
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException

import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

class Client {
    static final String BASE_URL = 'https://qa-eval.herokuapp.com/'
    static final HTTPBuilder http = new HTTPBuilder(BASE_URL)
    static final ObjectMapper mapper = new ObjectMapper()

    def static get(String path, Class clazz) {
        def responseObject

        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        http.parser.'application/json' = http.parser.'text/plain'

        try {
            http.get(path: path, contentType: JSON) { resp, json ->
                responseObject = mapper.readValue(json, clazz)
            }
        } catch (HttpResponseException responseException) {
            throwAppropriateException(responseException.response);
        }

        responseObject
    }

    static def post(String path, Map bodyToSend, Class clazz) {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        http.parser.'application/json' = http.parser.'text/plain'

        def responseObject = null

        try {
            http.request(POST, JSON) { req ->
                requestContentType = JSON
                body = bodyToSend
                uri.path = path

                response.success = { resp, json ->
                    println(json)
                    responseObject = mapper.readValue(json, clazz)
                }

                response.failure = { resp ->
                    throwAppropriateException(resp)
                }
            }
        } catch (HttpResponseException responseException) {
            throwAppropriateException(responseException.response);
        }

        responseObject
    }

    static def throwAppropriateException(def response) {
        switch(response.status) {
            case 400:
                throw new BadRequestException(response)
            case 403:
                throw new ForbiddenException(response)
            case 404:
                throw new NotFoundException(response)
            case 413:
                throw new PayloadTooLargeException(response)
            default:
                throw new RuntimeException("Could not find an appropriate exception for status code ${response.status}")
        }
    }
}
