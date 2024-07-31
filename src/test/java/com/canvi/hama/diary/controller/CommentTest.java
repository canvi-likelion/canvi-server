package com.canvi.hama.diary.controller;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.canvi.hama.domain.auth.dto.request.LoginRequest;
import com.canvi.hama.domain.auth.dto.request.SignupRequest;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import com.canvi.hama.domain.diary.dto.response.DiaryGetResponse;
import com.canvi.hama.domain.diary.dto.request.CommentSaveRequest;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.exception.UserException;
import com.canvi.hama.domain.user.exception.UserResponseStatus;
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
public class CommentTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailAuthService emailAuthService;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;
    private Long userId;
    private Long diaryId;

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;

        // 테스트 환경에서는 모든 이메일이 인증된 것으로 처리
        Mockito.when(emailAuthService.isEmailVerified(Mockito.anyString())).thenReturn(true);

        // 테스트용 계정 생성
        String testEmail = "test@example.com";
        String testPassword = "password123";
        SignupRequest signupRequest = new SignupRequest("testuser", testEmail, testPassword);

        given()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(HttpStatus.OK.value());

        // 로그인 후 액세스 토큰을 받아옵니다
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        accessToken = loginResponse.jsonPath().getString("result.accessToken");

        // userId 받아오기
        String email = loginResponse.jsonPath().getString("result.username");
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserException(UserResponseStatus.NOT_FOUND));

        userId = user.getId();

        // 일기 저장
        DiaryRequest diaryRequest = new DiaryRequest("Test Title", "Test Content", LocalDate.now());

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diaries")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 저장된 일기 확인
        Response diaryResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diaries")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();


        List<DiaryGetResponse> diaries = diaryResponse.jsonPath().getList("result.diaryGetResponseList", DiaryGetResponse.class);
        diaryId = diaries.get(0).id();
    }

    @Test
    public void saveComment() {
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("Test Comment");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(commentSaveRequest)
                .when()
                .post("/diaries/" + diaryId + "/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void getCommentByDiaryId() {
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("Test Comment");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(commentSaveRequest)
                .when()
                .post("/diaries/" + diaryId + "/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 저장된 코멘트 확인
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diaries/" + diaryId + "/comments")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        assertThat(response.jsonPath().getString("result.comment")).isEqualTo("Test Comment");
    }
}