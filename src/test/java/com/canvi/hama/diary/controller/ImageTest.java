package com.canvi.hama.diary.controller;

import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageTest {

    @LocalServerPort
    private int port;

    private String accessToken;
    private Long userId;
    private Long diaryId;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public ImageTest(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
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
        Response diaryResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diary/user/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();


        List<Diary> diaries = diaryResponse.jsonPath().getList(".", Diary.class);
        diaryId = diaries.get(0).getId();
    }

    @Test
    public void saveImage() {
        // 이미지 url 가져오기


    }

    @Test
    public void getImageByDiaryId() {

    }
}
