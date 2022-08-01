package com.solera.global.qa.mockservertest.cases.mockserveropenapi;

import static io.restassured.RestAssured.given;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.beans.Error;
import com.solera.global.qa.mockservertest.beans.Pet;
import com.solera.global.qa.mockservertest.common.junitAnnotation.Duration;
import com.solera.global.qa.mockservertest.common.junitAnnotation.OpenApiPetstore;
import com.solera.global.qa.mockservertest.common.junitLogger.LifecycleLogger;
import io.restassured.http.ContentType;

@SpringBootTest
@DisplayName("MockServerOpenApiTests")
@Duration
@OpenApiPetstore
class MockServerOpenApiTests extends MockServerTestBase implements LifecycleLogger {

  Pet pet = new Pet(200, "Cat", "CAT");
  Error e400 = new Error(400, "Bad Request");
  Error e500 = new Error(500, "Internal Server Error");

  @Test
  void testSomePathStatusCode200() throws Exception {
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
  void testSomePathStatusCode404() throws Exception {
    given().log().all()
        .queryParam("limit", "404")
        .header(new io.restassured.http.Header("X-Request-ID",
            UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(404)
        .when().get(MOCKSERVERURL + "/some/path");
  }

  @Test
  void testShowPetByIdStatusCode200400500() throws Exception {
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
  void testShowPetByIdStatusCode404() throws Exception {
    given().log().all()
        .pathParam("petId", "404123")
        .header(new io.restassured.http.Header("X-Request-ID",
            UUID.randomUUID().toString()))
        .then().log().all()
        .expect().statusCode(404)
        .when().get(MOCKSERVERURL + "/pets/{petId}");
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
  void testlistPetsStatusCode404() throws Exception {
    given().log().all()
        .queryParam("limit", "404")
        .then().log().all()
        .expect().statusCode(404)
        .when().get(MOCKSERVERURL + "/pets");
  }

  @Test
  void testCreatePetsStatusCode201() throws Exception {
    Pet pet = new Pet(201, "Cat", "CAT");
    given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .then().log().all()
        .expect().statusCode(201)
        .when().post(MOCKSERVERURL + "/pets");
  }

  @Test
  void testCreatePetsStatusCode500() throws Exception {
    Pet pet = new Pet(500, "Cat", "CAT");
    Error actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .then().log().all()
        .expect().statusCode(500)
        .when().post(MOCKSERVERURL + "/pets").as(Error.class);
    Assertions.assertThat(actualResult).isEqualTo(e500);
  }

  @Test
  void testCreatePetsStatusCode404() throws Exception {
    Pet pet = new Pet(404, "Cat", "CAT");
    given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .then().log().all()
        .expect().statusCode(404)
        .when().post(MOCKSERVERURL + "/pets");
  }

}
