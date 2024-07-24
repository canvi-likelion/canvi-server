package com.canvi.hama.diary;

import com.canvi.hama.ai.request.DalleRequest;
import com.canvi.hama.ai.service.GptService;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.entity.Image;
import com.canvi.hama.diary.repository.DiaryRepository;
import com.canvi.hama.diary.repository.ImageRepository;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import com.canvi.hama.diary.service.DiaryService;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.ai.service.GptService;
import com.canvi.hama.domain.auth.dto.SignupRequest;
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
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiaryServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private GptService gptService;

    @Autowired
    private DiaryService diaryService;

    private Long diaryId;

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;
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


        // Test diary creation
        DiaryRequest diaryRequest = new DiaryRequest(0L, "Test Title", "Test Content", LocalDate.now());
        Response response = given()
                .contentType(ContentType.JSON)
                .body(diaryRequest)
                .when()
                .post("/diary/save")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        diaryId = response.jsonPath().getLong("id");
    }

    @Test
    public void testDiaryAndImageWorkflow() {
        DalleRequest dalleRequest = new DalleRequest(diaryId, "Male", "25", "Short", "Casual", "Test Content");
        String imageUrl = gptService.getDallEResponse(dalleRequest);
        assertThat(imageUrl).isNotBlank();

        // Step 2: Save diary
        DiaryRequest diaryRequest = new DiaryRequest(0L, "Test Title", "Test Content", LocalDate.now());
        diaryService.saveDiary(diaryRequest);

        Diary savedDiary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
        assertThat(savedDiary).isNotNull();
        assertThat(savedDiary.getTitle()).isEqualTo("Test Title");
        assertThat(savedDiary.getContent()).isEqualTo("Test Content");

        // Step 3: Save image from URL
        saveImageFromUrl(diaryId, imageUrl);

        Image savedImage = imageRepository.findByDiaryId(diaryId).orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getUrl()).contains("image_" + diaryId + ".png");
    }

    public void saveImageFromUrl(Long diaryId, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            File file = new File("./image_" + diaryId + ".png"); // Adjust the path as necessary
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // Create directories if not exists
            }
            FileOutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();

            Diary diary = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

            Image image = Image.builder()
                    .diary(diary)
                    .url(file.getAbsolutePath())
                    .build();

            imageRepository.save(image);
        } catch (Exception e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
