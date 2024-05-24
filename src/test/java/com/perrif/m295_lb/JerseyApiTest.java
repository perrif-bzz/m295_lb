package com.perrif.m295_lb;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JerseyApiTest
{
    private static final String SERVICE_URL = "http://localhost:8080/App/resources/car";
    private static String USERNAME = "admin";
    private static String PASSWORD = "1234";

    private void addAuthorizationHeader(HttpUriRequest request)
    {
        String auth = USERNAME + ":" + PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    @Test
    @Order(1)
    public void getPing_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/ping");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void getAllCars_thenNoContent() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void getNotPresentCarById_thenNotFound() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/isPresent/999999");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void getNotExistingCarById_thenNotFound() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(5)
    public void getCarByProductionDate_thenBadRequest() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/productionDate/invalid-date");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(6)
    public void getCarByMake_thenNoContent() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/make/NonExistentMake");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(7)
    public void getCarByInvalidId() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/invalid-id");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(8)
    public void getCarByInvalidProductionDate_thenBadRequest() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/productionDate/invalid-date");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(9)
    public void getCarByProductionDate_thenNoContent() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/productionDate/1900-01-01");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(10)
    public void getEmptyCarCount_thenNoContent() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/count");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
    }

    private void makeAssert(HttpUriRequest request, int status) throws IOException
    {
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(status, httpResponse.getStatusLine().getStatusCode());
    }

    private void makePost(HttpPost request, String json, int status) throws IOException
    {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    private void makePut(HttpPut request, String json, int status) throws IOException
    {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    @Test
    @Order(11)
    public void addCarAsAdmin_thenOk() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(12)
    public void addCarAsAdmin_thenConflict() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    @Order(13)
    public void addInvalidCarAsAdmin_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"badKey\":999,\"badKey\":\"Toyota\",\"badKey\":20000.0}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(14)
    public void addCarWithId_thenOk() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"id\": 2,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(15)
    public void addCarWithInvalidMake_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        addAuthorizationHeader(request);
        String json = "{\"make\":\"\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(16)
    public void addCarWithInvalidModel_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        addAuthorizationHeader(request);
        String json = "{\"make\":\"Toyota\",\"model\":\"\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(17)
    public void addCarWithInvalidProductionDate_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        addAuthorizationHeader(request);
        String json = "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"invalid-date\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(18)
    public void addCarWithInvalidPrice_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL);
        addAuthorizationHeader(request);
        String json = "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":-20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(19)
    public void addMultipleCarsAsAdmin_thenOk() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL + "/multiple");
        String json = "[{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}]";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(20)
    public void addMultipleOnlyDuplicateCarsAsAdmin_thenNoContent() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL + "/multiple");
        String json = "[]";
        makePost(request, json, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Order(21)
    public void addMultipleInvalidCarsAsAdmin_thenBadRequest() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL + "/multiple");
        String json = "[" + "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}," + "{\"make\":\"\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}," + "{\"make\":\"Toyota\",\"model\":\"\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}," + "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"invalid-date\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}," + "{\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":-20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}" + "]";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(22)
    public void addMultipleCarsWithIdAsAdmin_thenOk() throws IOException
    {
        HttpPost request = new HttpPost(SERVICE_URL + "/multiple");
        String json = "[" + "{\"id\": 1, \"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}," + "{\"id\": 2, \"make\":\"Honda\",\"model\":\"Civic\",\"productionDate\":\"2021-01-01\",\"approved\":true,\"price\":22000.0,\"owner\":{\"ahvNr\":\"1234567890123457\"}}," + "{\"make\":\"Ford\",\"model\":\"Focus\",\"productionDate\":\"2019-01-01\",\"approved\":true,\"price\":18000.0,\"owner\":{\"ahvNr\":\"1234567890123458\"}}" + "]";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(23)
    public void getAllCars_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(24)
    public void getCarById_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(25)
    public void getCarCount_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/count");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(26)
    public void getPresentCarById_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/isPresent/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(27)
    public void getCarByMake_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/make/Toyota");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(28)
    public void getCarByProductionDate_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/productionDate/2020-01-01");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(29)
    public void updateExistingCarAsAdmin_thenOk() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":21500.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(30)
    public void updateNonExistingCarAsAdmin_thenNotFound() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":999999,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":21500.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Order(31)
    public void updateCarWithInvalidMake_thenBadRequest() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(32)
    public void updateCarWithInvalidModel_thenBadRequest() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Toyota\",\"model\":\"\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(33)
    public void updateCarWithInvalidProductionDate_thenBadRequest() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-32\",\"approved\":true,\"price\":20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(34)
    public void updateCarWithInvalidPrice_thenBadRequest() throws IOException
    {
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Toyota\",\"model\":\"Corolla\",\"productionDate\":\"2020-01-01\",\"approved\":true,\"price\":-20000.0,\"owner\":{\"ahvNr\":\"1234567890123456\"}}";
        makePut(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(35)
    public void deleteExistingCarAsAdmin_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/1");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(36)
    public void deleteNotExistingCarAsAdmin_thenNotFound() throws IOException
    {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/999999");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(37)
    public void deleteAllCarsAsAdmin_thenOk() throws IOException
    {
        HttpUriRequest request = new HttpDelete(SERVICE_URL);
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(38)
    public void addCarAsTenant_thenOk() throws IOException
    {
        USERNAME = "tenant";
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"make\":\"Honda\",\"model\":\"Civic\",\"productionDate\":\"2021-01-01\",\"approved\":true,\"price\":22000.0,\"owner\":{\"ahvNr\":\"1234567890123457\"}}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(39)
    public void updateCarAsTenant_thenOk() throws IOException
    {
        USERNAME = "tenant";
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":5,\"make\":\"Honda\",\"model\":\"Civic\",\"productionDate\":\"2021-01-01\",\"approved\":true,\"price\":22500.0,\"owner\":{\"ahvNr\":\"1234567890123457\"}}";
        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    @Order(40)
    public void deleteCarAsTenant_thenUnauthorized() throws IOException
    {
        USERNAME = "tenant";
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(41)
    public void addCarAsPublic_thenUnauthorized() throws IOException
    {
        USERNAME = "";
        PASSWORD = "";
        HttpPost request = new HttpPost(SERVICE_URL);
        String json = "{\"make\":\"Nissan\",\"model\":\"Altima\",\"productionDate\":\"2022-01-01\",\"approved\":true,\"price\":23000.0,\"owner\":{\"ahvNr\":\"1234567890123458\"}}";
        makePost(request, json, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Order(42)
    public void updateCarAsPublic_thenUnauthorized() throws IOException
    {
        USERNAME = "";
        PASSWORD = "";
        HttpPut request = new HttpPut(SERVICE_URL);
        String json = "{\"id\":1,\"make\":\"Nissan\",\"model\":\"Altima\",\"productionDate\":\"2022-01-01\",\"approved\":true,\"price\":23500.0,\"owner\":{\"ahvNr\":\"1234567890123458\"}}";
        makePut(request, json, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Order(43)
    public void deleteCarAsPublic_thenUnauthorized() throws IOException
    {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
    }
}
