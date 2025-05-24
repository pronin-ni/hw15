package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
public class ReqresTests {

    private static final String API_KEY = "reqres-free-v1";
    private static final String BASE_URL = "https://reqres.in/api";
    private static RequestSpecification spec;

    @BeforeAll
    static void setUp() {
        spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(JSON)
                .addHeader("x-api-key", API_KEY)
                .build();
    }

    @DisplayName("Успешный логин пользователя")
    @Test
    void successLoginTest() {
        given()
                .spec(spec)
                .body("{\"email\": \"eve.holt@reqres.in\",\"password\": \"cityslicka\"}")
                .log().all()
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .log().all()
                .body("token", not(blankOrNullString()));
    }

    @DisplayName("Неуспешный логин без тела")
    @Test
    void unsuccessfulLoginTest() {
        given()
                .spec(spec)
                .body("{\"email\": \"eve.holt@reqres.in\"}")
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @DisplayName("Получение пользователей второй страницы")
    @Test
    void getUsersTest() {
        given()
                .spec(spec)
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("data[0].first_name", is("Michael"));
    }

    @DisplayName("Создание пользователя")
    @Test
    void createUserTest() {
        given()
                .spec(spec)
                .body("{\"name\": \"nik\",\"job\": \"programmer\"}")
                .log().all()
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", is("nik"));

    }
    @DisplayName("Получение пользователя по id")
    @Test
    void getUserByIdTest() {
        given()
                .spec(spec)
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("data.first_name", is("Janet"));

    }

    @DisplayName("Удаление пользователя по id")
    @Test
    void deleteUserByIdTest() {
        given()
                .spec(spec)
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);
    }

    @DisplayName("Обновление пользователя по id через PUT")
    @Test
    void updateUserByIdTest() {
        given()
                .spec(spec)
                .body("{\"name\": \"nik\",\"job\": \"programmer\"}")
                .when()
                .put("/users/2")
                .then()
                .body("updatedAt", not(blankOrNullString()));

    }

    @DisplayName("Обновление пользователя по id через PATCH")
    @Test
    void updateUserByIdPatchTest() {
        given()
                .spec(spec)
                .body("{\"name\": \"nik\",\"job\": \"programmer\"}")
                .when()
                .patch("/users/2")
                .then()
                .statusCode(200)
                .body("updatedAt", not(blankOrNullString()))
                .body("name", is("nik"))
                .body("job", is("programmer"));
    }
}