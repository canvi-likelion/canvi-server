package com.canvi.hama.diary.controller;

import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.request.ImageSaveRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.user.domain.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
        ImageSaveRequest imageSaveRequest = new ImageSaveRequest(diaryId, imageUrl);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(imageSaveRequest)
                .when()
                .post("/diary/image/save")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("이미지 저장 완료"));
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

        ImageSaveRequest imageSaveRequest = new ImageSaveRequest(diaryId, imageUrl);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(imageSaveRequest)
                .when()
                .post("/diary/image/save")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo("이미지 저장 완료"));


        // 이미지 불러오기
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/diary/image/" + diaryId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
