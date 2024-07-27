package com.canvi.hama.domain.auth.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.auth.dto.SendCodeRequest;
import com.canvi.hama.domain.auth.dto.VerifyCodeRequest;
import com.canvi.hama.domain.auth.service.AuthService;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailAuthControllerTest {

    @LocalServerPort
    private int port;

    private String testEmail;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailAuthService emailAuthService;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        testEmail = "test@example.com";
    }

    @Test
    public void whenSendAuthCode_thenReturnsSuccess() {
        when(authService.isEmailAvailable(testEmail)).thenReturn(true);
        doNothing().when(emailAuthService).sendAuthCode(testEmail);

        SendCodeRequest request = new SendCodeRequest(testEmail);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenSendInvalidEmailForm_thenReturnsBadRequest() {
        SendCodeRequest request = new SendCodeRequest("invalid.com");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenSendAuthCodeToExistingEmail_thenReturnsConflict() {
        when(authService.isEmailAvailable(testEmail)).thenReturn(false);

        SendCodeRequest request = new SendCodeRequest(testEmail);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void whenVerifyAuthCode_thenReturnsSuccess() {
        when(emailAuthService.verifyAuthCode(testEmail, "123456")).thenReturn(true);

        VerifyCodeRequest request = new VerifyCodeRequest(testEmail, "123456");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/verify-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenVerifyInvalidAuthCode_thenReturnsBadRequest() {
        when(emailAuthService.verifyAuthCode(testEmail, "123456")).thenReturn(false);

        VerifyCodeRequest request = new VerifyCodeRequest(testEmail, "123456");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/verify-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenResendAuthCodeTooSoon_thenReturnsTooManyRequests() {
        when(authService.isEmailAvailable(testEmail)).thenReturn(true);
        doThrow(new BaseException(BaseResponseStatus.EMAIL_RESEND_TOO_SOON))
                .when(emailAuthService).sendAuthCode(testEmail);

        SendCodeRequest request = new SendCodeRequest(testEmail);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    public void whenResendAuthCodeAfterLimit_thenReturnsSuccess() {
        when(authService.isEmailAvailable(testEmail)).thenReturn(true);
        doNothing().when(emailAuthService).sendAuthCode(testEmail);

        SendCodeRequest request = new SendCodeRequest(testEmail);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        verify(emailAuthService, times(1)).sendAuthCode(testEmail);
    }
}
