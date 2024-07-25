package com.canvi.hama.domain.auth.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
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
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailAuthService emailAuthService;

    private String testUsername;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testUsername = "testuser";
        testEmail = "test@example.com";
        testPassword = "password123";

        // 테스트 환경에서는 모든 이메일이 인증된 것으로 처리
        Mockito.when(emailAuthService.isEmailVerified(Mockito.anyString())).thenReturn(true);

        // 테스트용 계정 생성
        SignupRequest signupRequest = new SignupRequest(testUsername, testEmail, testPassword);
        given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup");
    }

    @Test
    public void whenValidSignup_thenReturnsSuccess() {
        SignupRequest signupRequest = new SignupRequest("newuser", "new@example.com", "newpassword");

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
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);

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
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);
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
    public void whenLogout_thenReturnsSuccess() {
        // 먼저 로그인
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/auth/login");
        String accessToken = loginResponse.jsonPath().getString("result.accessToken");

        // 로그아웃
        Response logoutResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/api/auth/logout")
                .then()
                .extract().response();

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        // 로그아웃 후 리프레시 토큰으로 새 액세스 토큰 요청 시 실패해야 함
        String refreshToken = loginResponse.jsonPath().getString("result.refreshToken");
        Response refreshResponse = given()
                .header("Authorization", "Bearer " + refreshToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .extract().response();

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenFindUsernameByValidEmail_thenReturnsUsername() {
        Response response = given()
                .contentType(ContentType.URLENC)
                .param("email", testEmail)
                .when()
                .get("/api/auth/find-username")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("result")).isEqualTo(testUsername);
    }

    @Test
    public void whenFindUsernameByInvalidEmail_thenReturnsError() {
        String invalidEmail = "nonexistent@example.com";
        Response response = given()
                .contentType(ContentType.URLENC)
                .param("email", invalidEmail)
                .when()
                .get("/api/auth/find-username")
                .then()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
