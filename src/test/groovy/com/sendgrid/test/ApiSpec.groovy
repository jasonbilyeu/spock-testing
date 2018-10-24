package com.sendgrid.test

import com.sendgrid.test.api.qatest.client.Client
import com.sendgrid.test.api.qatest.client.exceptions.BadRequestException
import com.sendgrid.test.api.qatest.client.exceptions.ForbiddenException
import com.sendgrid.test.api.qatest.client.exceptions.NotFoundException
import com.sendgrid.test.api.qatest.client.exceptions.PayloadTooLargeException
import com.sendgrid.test.api.qatest.models.User
import com.sendgrid.test.api.qatest.models.UserCreateResponse
import spock.lang.Specification

import static com.sendgrid.test.api.qatest.util.ARandom.aRandom

class ApiSpec extends Specification {

    def "should create a user"() {
        given:
        User user = aRandom.user().build()

        when:
        UserCreateResponse createResponse = Client.post("api/users", createUserMap(user), UserCreateResponse)

        then:
        assert createResponse.message == 'User Created'
        assert createResponse.result == 'success'
        assert createResponse.userid

        when:
        User retrievedUser = Client.get("api/users/${createResponse.userid}", User)

        then:
        assert retrievedUser.username == user.username
        assert retrievedUser.email == user.email
        assert retrievedUser.password == user.password
    }

    def "should fail to retrieve non-existent userId"() {
        when:
        Client.get("api/users/${aRandom.getNumberBetween(10000, 100000)}", User)

        then:
        thrown(NotFoundException)
    }

    def "should fail when username is empty"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().username(null).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when username is too short"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().username(aRandom.getRandomChars(1)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when username is too long"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().username(aRandom.getRandomChars(13)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when username is all numeric"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().username(aRandom.getNumberText(8)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when password is empty"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().password(null).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when password is too short"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().password(aRandom.getRandomChars(6)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when password is too long"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().password(aRandom.getRandomChars(25)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when password is only alpha characters"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().password(aRandom.getRandomText(8, 20)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when password is only numeric characters"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().password(aRandom.getNumberText(aRandom.getNumberBetween(8, 20))).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when email is empty"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().email(null).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when email is invalid"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().email(aRandom.getRandomChars(10)).build()), UserCreateResponse)

        then:
        thrown(BadRequestException)
    }

    def "should fail when trying to send a duplicate email"() {
        given:
        User user = aRandom.user().build()
        Client.post("api/users", createUserMap(user), UserCreateResponse)

        when:
        Client.post("api/users", createUserMap(user), UserCreateResponse)

        then:
        thrown(ForbiddenException)
    }

    def "should fail when trying to send too large of an email"() {
        when:
        Client.post("api/users", createUserMap(aRandom.user().email(aRandom.getRandomText(1000000)).build()), UserCreateResponse)

        then:
        thrown(PayloadTooLargeException)
    }

    def createUserMap(User user) {
        [username: user.username,
         email: user.email, password: user.password]
    }
}
