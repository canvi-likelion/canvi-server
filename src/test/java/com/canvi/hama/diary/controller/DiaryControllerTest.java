package com.canvi.hama.diary.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.enums.DiaryResponseStatus;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiaryControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailAuthService emailAuthService;

    private String accessToken;
    private Long userId;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;

        // 테스트 환경에서는 모든 이메일이 인증된 것으로 처리
        Mockito.when(emailAuthService.isEmailVerified(Mockito.anyString())).thenReturn(true);

        // 테스트용 계정 생성
        String testUsername = "testuser";
        String testPassword = "password123";
        SignupRequest signupRequest = new SignupRequest(testUsername, "test@example.com", testPassword);

        given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(HttpStatus.OK.value());

        // 로그인 후 액세스 토큰을 받아옵니다
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        accessToken = loginResponse.jsonPath().getString("result.accessToken");

        // userId 받아오기
        String userName = loginResponse.jsonPath().getString("result.username");
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        userId = user.getId();
    }

    // 일기 저장 확인
    @Test
    public void saveDiary() {
        DiaryRequest diaryRequest = new DiaryRequest("Test Title", "Test Content", LocalDate.now());

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diaries")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    // 일기 불러오기 확인
    @Test
    public void getDiariesByUserId() {
        // 일기 저장
        DiaryRequest diaryRequest = new DiaryRequest("Test Title", "Test Content", LocalDate.now());

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diaries" )
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 저장된 일기 확인
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diaries")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<Diary> diaries = response.jsonPath().getList(".", Diary.class);
        assertThat(diaries).isNotEmpty();
        assertThat(diaries.get(0).getTitle()).isEqualTo("Test Title");
        assertThat(diaries.get(0).getContent()).isEqualTo("Test Content");
    }
}
