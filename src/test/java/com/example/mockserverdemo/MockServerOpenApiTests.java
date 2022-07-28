package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Error;
import com.example.mockserverdemo.beans.Pet;
import com.example.mockserverdemo.utils.MockServerUtils;
import io.restassured.http.ContentType;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerOpenApiTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @BeforeEach
  void tearDown() {
    mockServerUtils.reset();
  }

  @Test
  void testSomePathStatusCode200() throws Exception {
    int statusCode = 200;
    Pet pet = new Pet(1, "Cat", "CAT");

    mockServerUtils.createOpenApiExpectation("somePath", statusCode, pet);

    Pet actualResult = given().log().all()
        .queryParam("limit", "10")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/some/path").as(Pet.class);

    Assertions.assertThat(actualResult).isEqualTo(pet);
  }

  @Test
  void testShowPetByIdStatusCode200() throws Exception {
    int statusCode = 200;
    Pet pet = new Pet(1, "Cat", "CAT");

    mockServerUtils.createOpenApiExpectation("showPetById", statusCode, pet);

    Pet actualResult = given().log().all()
        .pathParam("petId", "123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/pets/{petId}").as(Pet.class);

    Assertions.assertThat(actualResult).isEqualTo(pet);
  }

  @Test
  void testShowPetByIdStatusCode400() throws Exception {
    int statusCode = 400;
    Error error = new Error(statusCode, "Bad Request");

    mockServerUtils.createOpenApiExpectation("showPetById", statusCode, error);

    Error actualResult = given().log().all()
        .pathParam("petId", "123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/pets/{petId}").as(Error.class);

    Assertions.assertThat(actualResult).isEqualTo(error);
  }

  @Test
  void testShowPetByIdStatusCode500() throws Exception {
    int statusCode = 500;
    Error error = new Error(statusCode, "Internal Server Error");

    mockServerUtils.createOpenApiExpectation("showPetById", statusCode, error);

    Error actualResult = given().log().all()
        .pathParam("petId", "123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/pets/{petId}").as(Error.class);

    Assertions.assertThat(actualResult).isEqualTo(error);
  }

  @Test
  void testlistPetsStatusCode200() throws Exception {
    int statusCode = 200;
    List<Pet> pets = Arrays.asList(new Pet(1, "Cat", "CAT"));

    mockServerUtils.createOpenApiExpectation("listPets", statusCode, pets);

    List<Pet> actualResult = given().log().all()
        .queryParam("limit", "10")
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/pets").jsonPath().getList(".", Pet.class);

    Assertions.assertThat(actualResult).isEqualTo(pets);
  }

  @Test
  void testlistPetsStatusCode500() throws Exception {
    int statusCode = 500;
    Error error = new Error(statusCode, "Internal Server Error");

    mockServerUtils.createOpenApiExpectation("listPets", statusCode, error);

    Error actualResult = given().log().all()
        .queryParam("limit", "10")
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get("http://localhost:1080/pets").as(Error.class);

    Assertions.assertThat(actualResult).isEqualTo(error);
  }

  @Test
  void testCreatePetsStatusCode201() throws Exception {
    int statusCode = 201;
    Pet pet = new Pet(1, "Cat", "CAT");

    mockServerUtils.createOpenApiExpectation("createPets", statusCode, null);

    given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().post("http://localhost:1080/pets");
  }

  @Test
  void testCreatePetsStatusCode500() throws Exception {
    int statusCode = 500;
    Pet pet = new Pet(1, "Cat", "CAT");
    Error error = new Error(statusCode, "Internal Server Error");

    mockServerUtils.createOpenApiExpectation("createPets", statusCode, error);

    Error actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().post("http://localhost:1080/pets").as(Error.class);

    Assertions.assertThat(actualResult).isEqualTo(error);
  }

}
