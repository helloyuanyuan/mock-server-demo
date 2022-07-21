package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import com.example.mockserverdemo.beans.Org;
import io.restassured.http.ContentType;

@SpringBootTest
class RestApiTests {

  @Test
  void testGet() {
    given()
        .when().get("https://httpbin.org/get")
        .then().log().all().assertThat().statusCode(200);
  }

  @Test
  void testGetWithParam() {
    given().queryParam("orgName", "solera")
        .when().get("https://httpbin.org/get")
        .then().log().all().assertThat().statusCode(200);
  }

  @Test
  void testPostWithForm() {
    given()
        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
        .formParam("org", "solera")
        .when().post("https://httpbin.org/post")
        .then().log().all().assertThat().statusCode(200);
  }

  @Test
  void testPostWithMultiPart() throws FileNotFoundException {
    given()
        .multiPart(ResourceUtils.getFile(this.getClass().getResource("/test.file")))
        .when().post("https://httpbin.org/post")
        .then().log().all().assertThat().statusCode(200);
  }

  @Test
  void testPostWithXml() throws FileNotFoundException {
    given()
        .contentType(ContentType.XML)
        .body(ResourceUtils.getFile(this.getClass().getResource("/test.xml")))
        .when().post("https://httpbin.org/post")
        .then().log().all()
        .assertThat().statusCode(200);
  }

  @Test
  void testPostWithJsonFromHashMap() throws FileNotFoundException {
    HashMap<String, String> map = new HashMap<>();
    map.put("id", "1");
    map.put("orgName", "solera");
    given()
        .contentType(ContentType.JSON)
        .body(map)
        .when().post("https://httpbin.org/post")
        .then().log().all()
        .assertThat()
        .statusCode(200)
        .header("Content-Type", "application/json");
  }

  @Test
  void testPostWithJsonFromObject() throws FileNotFoundException {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");
    given()
        .contentType(ContentType.JSON)
        .body(org)
        .when().post("https://httpbin.org/post")
        .then().log().all()
        .assertThat()
        .statusCode(200)
        .header("Content-Type", "application/json");
  }

}
