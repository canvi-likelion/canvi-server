package com.canvi.hama.domain.auth.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.canvi.hama.domain.auth.service.AuthService;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailAuthControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailAuthService emailAuthService;

    @MockBean
    private AuthService authService;

    private String testEmail;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        testEmail = "test@example.com";

        // 테스트 환경에서는 모든 이메일이 사용 가능한 것으로 처리
        Mockito.when(authService.isEmailAvailable(Mockito.anyString())).thenReturn(true);
    }

    @Test
    public void whenSendAuthCode_thenReturnsSuccess() {
        Response response = given()
                .contentType(ContentType.URLENC)
                .param("email", testEmail)
                .when()
                .post("/api/email-auth/send-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenVerifyAuthCode_thenReturnsSuccess() {
        String authCode = "123456";
        Mockito.when(emailAuthService.verifyAuthCode(testEmail, authCode)).thenReturn(true);

        Response response = given()
                .contentType(ContentType.URLENC)
                .param("email", testEmail)
                .param("code", authCode)
                .when()
                .post("/api/email-auth/verify-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenVerifyAuthCode_withInvalidCode_thenReturnsBadRequest() {
        String authCode = "invalid";
        Mockito.when(emailAuthService.verifyAuthCode(testEmail, authCode)).thenReturn(false);

        Response response = given()
                .contentType(ContentType.URLENC)
                .param("email", testEmail)
                .param("code", authCode)
                .when()
                .post("/api/email-auth/verify-code")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
