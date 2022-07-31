package com.solera.global.qa.mockservertest.cases.mockserveropenapi;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.beans.Error;
import com.solera.global.qa.mockservertest.beans.Pet;
import com.solera.global.qa.mockservertest.common.junitExtension.TimingExtension;
import com.solera.global.qa.mockservertest.common.junitLogger.LifecycleLogger;
import io.restassured.http.ContentType;

@SpringBootTest
@DisplayName("MockServerOpenApiTests")
@ExtendWith(TimingExtension.class)
class MockServerOpenApiTests extends MockServerTestBase implements LifecycleLogger {

  Pet pet = new Pet(1, "Cat", "CAT");
  Error e400 = new Error(400, "Bad Request");
  Error e500 = new Error(500, "Internal Server Error");

  @BeforeAll
  void beforeEach() throws Exception {
    mockServerUtils.reset();

    String petString = new ObjectMapper().writeValueAsString(pet);
    String petsString = new ObjectMapper().writeValueAsString(Arrays.asList(pet));
    String eString400 = new ObjectMapper().writeValueAsString(e400);
    String eString500 = new ObjectMapper().writeValueAsString(e500);

    client
        .when(openAPI(OPENAPIURL, "somePath"))
        .respond(
            httpRequest -> {
              if (httpRequest.getQueryStringParameters().getValues("limit").get(0)
                  .startsWith("200")) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(petString);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "showPetById"))
        .respond(
            httpRequest -> {
              if (httpRequest.getPathParameters().getValues("petId").get(0)
                  .startsWith("200")) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(petString);
              }
              if (httpRequest.getPathParameters().getValues("petId").get(0)
                  .startsWith("400")) {
                return response()
                    .withStatusCode(400)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(eString400);
              }
              if (httpRequest.getPathParameters().getValues("petId").get(0)
                  .startsWith("500")) {
                return response()
                    .withStatusCode(500)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "listPets"))
        .respond(
            httpRequest -> {
              if (httpRequest.getQueryStringParameters().getValues("limit").get(0)
                  .startsWith("200")) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(petsString);
              }
              if (httpRequest.getQueryStringParameters().getValues("limit").get(0)
                  .startsWith("500")) {
                return response()
                    .withStatusCode(500)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "createPets"))
        .respond(
            httpRequest -> {
              if (httpRequest.getBodyAsString().contains("201")) {
                return response()
                    .withStatusCode(201)
                    .withHeaders(new Header("Content-Type", "application/json"));
              }
              if (httpRequest.getBody().toString().contains("500")) {
                return response()
                    .withStatusCode(500)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });

  }

  @Test
  void testSomePath() throws Exception {
    Pet actualResult = given().log().all()
        .queryParam("limit", "200")
        .header(new io.restassured.http.Header("X-Request-ID",
            UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(200)
        .when().get(MOCKSERVERURL + "/some/path").as(Pet.class);
    Assertions.assertThat(actualResult).isEqualTo(pet);
  }

  @Test
  void testShowPetById() throws Exception {
    testShowPetById("200123", 200, pet);
    testShowPetById("400123", 400, e400);
    testShowPetById("500123", 500, e500);
  }

  void testShowPetById(String value, int statusCode, Object object) {
    Object actualResult = given().log().all()
        .pathParam("petId", value)
        .header(new io.restassured.http.Header("X-Request-ID",
            UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(statusCode)
        .when().get(MOCKSERVERURL + "/pets/{petId}").as(object.getClass());
    Assertions.assertThat(actualResult).isEqualTo(object);

  }

  @Test
  void testlistPetsStatusCode200() throws Exception {
    List<Pet> actualResult = given().log().all()
        .queryParam("limit", "200")
        .then().log().all()
        .expect().statusCode(200)
        .when().get(MOCKSERVERURL + "/pets").jsonPath().getList(".", Pet.class);
    Assertions.assertThat(actualResult).isEqualTo(Arrays.asList(pet));
  }

  @Test
  void testlistPetsStatusCode500() throws Exception {
    Error actualResult = given().log().all()
        .queryParam("limit", "500")
        .then().log().all()
        .expect().statusCode(500)
        .when().get(MOCKSERVERURL + "/pets").as(Error.class);
    Assertions.assertThat(actualResult).isEqualTo(e500);
  }

  @Test
  void testCreatePetsStatusCode201() throws Exception {
    given().log().all()
        .contentType(ContentType.JSON)
        .body(new Pet(201, "Cat", "CAT"))
        .then().log().all()
        .expect().statusCode(201)
        .when().post(MOCKSERVERURL + "/pets");
  }

  @Test
  void testCreatePetsStatusCode500() throws Exception {
    Error actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .body(new Pet(500, "Dog", "DOG"))
        .then().log().all()
        .expect().statusCode(500)
        .when().post(MOCKSERVERURL + "/pets").as(Error.class);
    Assertions.assertThat(actualResult).isEqualTo(e500);
  }

}
