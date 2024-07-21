package com.canvi.hama.domain.auth.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    private String testUsername;
    private String testPassword;

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;

        // 테스트용 계정 생성
        testUsername = "testuser";
        testPassword = "password123";
        SignupRequest signupRequest = new SignupRequest(testUsername, "test@example.com", testPassword);

        given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup");
    }

    @Test
    public void whenValidSignup_thenReturnsSuccess() {
        SignupRequest signupRequest = new SignupRequest("newuser", "new@example.com", "password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenValidLogin_thenReturnsToken() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("result.accessToken")).isNotBlank();
        assertThat(response.jsonPath().getString("result.refreshToken")).isNotBlank();
    }

    @Test
    public void whenValidRefreshToken_thenReturnsNewAccessToken() {
        // 로그인 후 리프레시 토큰 받기
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/auth/login");
        String refreshToken = loginResponse.jsonPath().getString("result.refreshToken");

        // 리프레시 토큰을 가지고 새로운 액세스 토큰 받기
        Response refreshResponse = given()
                .header("Authorization", "Bearer " + refreshToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .extract().response();

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(refreshResponse.jsonPath().getString("result.accessToken")).isNotBlank();
    }

    @Test
    public void whenInvalidLogin_thenReturnsError() {
        LoginRequest loginRequest = new LoginRequest("invaliduser", "invalidpassword");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenDuplicateSignup_thenReturnsError() {
        SignupRequest signupRequest = new SignupRequest("existuser", "existuser@example.com", "password123");

        // 회원가입
        given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .post("/api/auth/signup");

        // 동일한 정보로 회원가입
        Response response = given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
