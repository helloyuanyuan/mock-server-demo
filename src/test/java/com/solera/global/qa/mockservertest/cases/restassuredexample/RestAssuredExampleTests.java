package com.solera.global.qa.mockservertest.cases.restassuredexample;

import static io.restassured.RestAssured.given;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import com.solera.global.qa.mockservertest.beans.Org;
import io.restassured.http.ContentType;

@SpringBootTest
@DisplayName("RestAssuredExampleTests")
class RestAssuredExampleTests {

    private final String URL = "https://httpbin.org";

    @Test
    void testGet() {
        given().log().all()
                .when().get(URL + "/get")
                .then().log().all().assertThat().statusCode(200);
    }

    @Test
    void testGetWithParam() {
        given().log().all()
                .queryParam("orgName", "solera")
                .when().get(URL + "/get")
                .then().log().all().assertThat().statusCode(200);
    }

    @Test
    void testPostWithForm() {
        given().log().all()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("org", "solera")
                .when().post(URL + "/post")
                .then().log().all().assertThat().statusCode(200);
    }

    @Test
    void testPostWithMultiPart() throws FileNotFoundException {
        given().log().all()
                .multiPart(ResourceUtils.getFile(this.getClass().getResource("/test.file")))
                .when().post(URL + "/post")
                .then().log().all().assertThat().statusCode(200);
    }

    @Test
    void testPostWithXml() throws FileNotFoundException {
        given().log().all()
                .contentType(ContentType.XML)
                .body(ResourceUtils.getFile(this.getClass().getResource("/test.xml")))
                .when().post(URL + "/post")
                .then().log().all()
                .assertThat().statusCode(200);
    }

    @Test
    void testPostWithJsonFromHashMap() throws FileNotFoundException {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("orgName", "solera");
        given().log().all()
                .contentType(ContentType.JSON)
                .body(map)
                .when().post(URL + "/post")
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
        given().log().all()
                .contentType(ContentType.JSON)
                .body(org)
                .when().post(URL + "/post")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .header("Content-Type", "application/json");
    }

}
