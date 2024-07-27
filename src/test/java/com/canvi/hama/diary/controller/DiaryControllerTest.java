package com.canvi.hama.diary.controller;

import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.request.DiaryRequest;
import com.canvi.hama.domain.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.user.domain.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiaryControllerTest {

    @LocalServerPort
    private int port;

    private String accessToken;
    private Long userId;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public DiaryControllerTest(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;

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
        String userName = jwtTokenProvider.getUsernameFromJWT(accessToken);
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        userId = user.getId();

    }

    // 일기 저장 확인
    @Test
    public void saveDiary() {
        DiaryRequest diaryRequest = new DiaryRequest(userId, "Test Title", "Test Content", LocalDate.now());

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diary/save")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("일기 저장 완료"));

    }

    // 일기 불러오기 확인
    @Test
    public void getDiariesByUserId() {
        // 일기 저장
        DiaryRequest diaryRequest = new DiaryRequest(userId, "Test Title", "Test Content", LocalDate.now());

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diary/save")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 저장된 일기 확인
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diary/user/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        List<Diary> diaries = response.jsonPath().getList(".", Diary.class);
        assertThat(diaries).isNotEmpty();
        assertThat(diaries.get(0).getTitle()).isEqualTo("Test Title");
        assertThat(diaries.get(0).getContent()).isEqualTo("Test Content");
    }

}
