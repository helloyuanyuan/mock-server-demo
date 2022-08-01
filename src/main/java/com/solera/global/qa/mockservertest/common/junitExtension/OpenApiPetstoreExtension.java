package com.solera.global.qa.mockservertest.common.junitExtension;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.beans.Error;
import com.solera.global.qa.mockservertest.beans.Pet;
import java.util.Arrays;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.Header;

public class OpenApiPetstoreExtension extends MockServerTestBase
    implements BeforeTestExecutionCallback {

  private static final Pet pet = new Pet(200, "Cat", "CAT");
  private static final Error e400 = new Error(400, "Bad Request");
  private static final Error e500 = new Error(500, "Internal Server Error");

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();

    Header header = new Header("Content-Type", "application/json");
    String petString = new ObjectMapper().writeValueAsString(pet);
    String petsString = new ObjectMapper().writeValueAsString(Arrays.asList(pet));
    String eString400 = new ObjectMapper().writeValueAsString(e400);
    String eString500 = new ObjectMapper().writeValueAsString(e500);

    client
        .when(openAPI(OPENAPIURL, "somePath"))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getQueryStringParameters()
                  .getValues("limit")
                  .get(0)
                  .startsWith("200")) {
                return response().withStatusCode(200).withHeaders(header).withBody(petString);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "showPetById"))
        .respond(
            httpRequest -> {
              if (httpRequest.getPathParameters().getValues("petId").get(0).startsWith("200")) {
                return response().withStatusCode(200).withHeaders(header).withBody(petString);
              }
              if (httpRequest.getPathParameters().getValues("petId").get(0).startsWith("400")) {
                return response().withStatusCode(400).withHeaders(header).withBody(eString400);
              }
              if (httpRequest.getPathParameters().getValues("petId").get(0).startsWith("500")) {
                return response().withStatusCode(500).withHeaders(header).withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "listPets"))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getQueryStringParameters()
                  .getValues("limit")
                  .get(0)
                  .startsWith("200")) {
                return response().withStatusCode(200).withHeaders(header).withBody(petsString);
              }
              if (httpRequest
                  .getQueryStringParameters()
                  .getValues("limit")
                  .get(0)
                  .startsWith("500")) {
                return response().withStatusCode(500).withHeaders(header).withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });

    client
        .when(openAPI(OPENAPIURL, "createPets"))
        .respond(
            httpRequest -> {
              if (httpRequest.getBodyAsString().contains("201")) {
                return response().withStatusCode(201).withHeaders(header);
              }
              if (httpRequest.getBody().toString().contains("500")) {
                return response().withStatusCode(500).withHeaders(header).withBody(eString500);
              } else {
                return notFoundResponse();
              }
            });
  }
}
