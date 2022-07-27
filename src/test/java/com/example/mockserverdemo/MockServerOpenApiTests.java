package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Pet;
import com.example.mockserverdemo.utils.MockServerUtils;
import io.restassured.http.ContentType;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerOpenApiTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @BeforeAll
  void tearDown() {
    mockServerUtils.reset();
    mockServerUtils.createOpenApiExpectation();
  }

  @Test
  void testSomePath() {
    Pet actualResult = given().log().all()
        .queryParam("limit", "10")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/some/path").as(Pet.class);

    Assertions.assertThat(actualResult.getId()).isNotNull();
    Assertions.assertThat(actualResult.getName()).isNotNull();
    Assertions.assertThat(actualResult.getTag()).isNotNull();
  }

  @Test
  void testShowPetById() {
    Pet actualResult = given().log().all()
        .pathParam("petId", "123")
        .header(new io.restassured.http.Header("X-Request-ID", UUID.randomUUID().toString()))
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/pets/{petId}").as(Pet.class);

    Assertions.assertThat(actualResult.getId()).isNotNull();
    Assertions.assertThat(actualResult.getName()).isNotNull();
    Assertions.assertThat(actualResult.getTag()).isNotNull();
  }

  @Test
  void testlistPets() {
    List<Pet> actualResult = given().log().all()
        .queryParam("limit", "10")
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/pets").jsonPath().getList(".", Pet.class);

    Assertions.assertThat(actualResult.get(0).getId()).isNotNull();
    Assertions.assertThat(actualResult.get(0).getName()).isNotNull();
    Assertions.assertThat(actualResult.get(0).getTag()).isNotNull();
  }

  @Test
  void testCreatePets() {
    Pet pet = new Pet();
    pet.setId(1);
    pet.setName("Dog");
    pet.setTag("Cat");

    given().log().all()
        .contentType(ContentType.JSON)
        .body(pet)
        .when().post("http://localhost:1080/pets")
        .then().log().all().statusCode(201);
  }

}
