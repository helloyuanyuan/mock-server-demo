package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Error;
import com.example.mockserverdemo.beans.Pet;
import com.example.mockserverdemo.common.MockServerBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerOpenApiTests extends MockServerBase {

  @BeforeEach
  void beforeEach() {
    mockServerUtils.reset();
  }

  @Test
  void testSomePath() throws Exception {
    int statusCode = 200;
    Pet pet = new Pet(1, "Cat", "CAT");

    mockServerUtils.createOpenApiExpectation("somePath", statusCode, pet);

    Pet actualResult = given().log().all()
        .queryParam("limit", "10")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get(URL + "/some/path").as(Pet.class);
    Assertions.assertThat(actualResult).isEqualTo(pet);
  }

  @Test
  void testShowPetById() throws Exception {
    Pet pet = new Pet(1, "Cat", "CAT");
    final String petString = new ObjectMapper().writeValueAsString(pet);

    Error error400 = new Error(400, "Bad Request");
    final String errorString400 = new ObjectMapper().writeValueAsString(error400);

    Error error500 = new Error(500, "Internal Server Error");
    final String errorString500 = new ObjectMapper().writeValueAsString(error500);

    client
        .when(openAPI(OPEN_API_URL, "showPetById"))
        .respond(
            httpRequest -> {
              if (httpRequest.getPathParameters().getValues("petId").get(0).startsWith("200")) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(petString);
              }
              if (httpRequest.getPathParameters().getValues("petId").get(0).startsWith("400")) {
                return response()
                    .withStatusCode(400)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(errorString400);
              } else {
                return response()
                    .withStatusCode(500)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(errorString500);
              }
            });

    Pet actualResult = given().log().all()
        .pathParam("petId", "200123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(200)
        .when().get(URL + "/pets/{petId}").as(Pet.class);
    Assertions.assertThat(actualResult).isEqualTo(pet);

    Error actualResultError400 = given().log().all()
        .pathParam("petId", "400123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(400)
        .when().get(URL + "/pets/{petId}").as(Error.class);
    Assertions.assertThat(actualResultError400).isEqualTo(error400);

    Error actualResultError500 = given().log().all()
        .pathParam("petId", "123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(500)
        .when().get(URL + "/pets/{petId}").as(Error.class);
    Assertions.assertThat(actualResultError500).isEqualTo(error500);
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
        .when().get(URL + "/pets").jsonPath().getList(".", Pet.class);

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
        .when().get(URL + "/pets").as(Error.class);

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
        .when().post(URL + "/pets");
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
        .when().post(URL + "/pets").as(Error.class);

    Assertions.assertThat(actualResult).isEqualTo(error);
  }

}
