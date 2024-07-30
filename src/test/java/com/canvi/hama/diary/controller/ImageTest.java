package com.canvi.hama.diary.controller;

import static io.restassured.RestAssured.given;

import com.canvi.hama.domain.auth.dto.request.LoginRequest;
import com.canvi.hama.domain.auth.dto.request.SignupRequest;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.dto.request.ImageSaveRequest;
import com.canvi.hama.domain.diary.enums.DiaryResponseStatus;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ImageTest {

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


        List<Diary> diaries = diaryResponse.jsonPath().getList(".", Diary.class);
        diaryId = diaries.get(0).getId();
    }

    @Test
    public void saveImage() {
        Map<String, String> body = new HashMap<>();

        body.put("gender", "man");
        body.put("age", "24");
        body.put("hair style", "neat hair");
        body.put("clothes", "Hoodie");
        body.put("prompt", "점심시간에는 동료들과 함께 회사 근처 식당에서 비빔밥을 먹었다. 식사를 하면서 일상적인 대화를 나누며 잠시나마 업무에서 벗어나 휴식을 취했다. 오후에는 보고서를 작성하고, 다음 주에 있을 프레젠테이션 준비에 집중했다. 프로젝트 관련 자료를 수집하고 분석하는 데 많은 시간을 쏟았다");

        // 이미지 url 가져오기
        Response dalleResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/dalle")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();
        JsonPath jsonPath = dalleResponse.jsonPath();
        String imageUrl = jsonPath.getString("data.url");

        // 이미지 저장
        ImageSaveRequest imageSaveRequest = new ImageSaveRequest(imageUrl);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(imageSaveRequest)
                .when()
                .post("/diaries/" + diaryId + "/images")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void getImageByDiaryId() {
        Map<String, String> body = new HashMap<>();

        body.put("gender", "man");
        body.put("age", "24");
        body.put("hair style", "neat hair");
        body.put("clothes", "Hoodie");
        body.put("prompt", "점심시간에는 동료들과 함께 회사 근처 식당에서 비빔밥을 먹었다. 식사를 하면서 일상적인 대화를 나누며 잠시나마 업무에서 벗어나 휴식을 취했다. 오후에는 보고서를 작성하고, 다음 주에 있을 프레젠테이션 준비에 집중했다. 프로젝트 관련 자료를 수집하고 분석하는 데 많은 시간을 쏟았다");

        // 이미지 url 가져오기
        Response dalleResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/dalle")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();
        JsonPath jsonPath = dalleResponse.jsonPath();
        String imageUrl = jsonPath.getString("data.url");

        ImageSaveRequest imageSaveRequest = new ImageSaveRequest(imageUrl);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(imageSaveRequest)
                .when()
                .post("/diaries/" + diaryId + "/images")
                .then()
                .statusCode(HttpStatus.CREATED.value());


        // 이미지 불러오기
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diaries/" + diaryId + "/images")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}