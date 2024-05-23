package com.perrif.m295_lb;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JerseyApiTest {
    private static final String SERVICE_URL = "http://localhost:8080/Project/resources";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";

    private void addAuthorizationHeader(HttpUriRequest request) {
        String auth = USERNAME + ":" + PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    @Test
    public void getAllModules_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/module");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void getGetOneModule_thenOk() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/module/295");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void getGetNotExistingModule_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL + "/module/999999");
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_NOT_FOUND,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    public void makeAssert(HttpUriRequest request, int status) throws IOException {
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                status,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    public void makePost(HttpPost request, String json, int status) throws IOException {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    public void makePut(HttpPut request, String json, int status) throws IOException {
        addAuthorizationHeader(request);
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        makeAssert(request, status);
    }

    @Test
    public void addModule_thenOk() throws IOException {
        HttpUriRequest deleteRequest = new HttpDelete(SERVICE_URL + "/module/999");
        addAuthorizationHeader(deleteRequest);
        HttpClientBuilder.create().build().execute(deleteRequest);

        HttpPost request = new HttpPost(SERVICE_URL + "/module");
        addAuthorizationHeader(request);
        String json = "{\"moduleNr\":999,\"designation\":\"Backend Applikation realisieren\",\"cost\":200.0}";
        makePost(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void addModule_thenConflict() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL + "/module");
        addAuthorizationHeader(request);
        String json = "{\"moduleNr\":295,\"designation\":\"Backend Applikation realisieren\",\"cost\":200.0}";
        makePost(request, json, HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addInvalidModule_thenBadRequest() throws IOException {
        HttpPost request = new HttpPost(SERVICE_URL + "/module");
        addAuthorizationHeader(request);
        String json = "{\"badkey\":999,\"badkey\":\"Backend Applikation realisieren\",\"badkey\":200.0}";
        makePost(request, json, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void updateExistingModule_thenOk() throws IOException {
        HttpPut request = new HttpPut(SERVICE_URL + "/module");
        addAuthorizationHeader(request);
        String json = "{\"moduleNr\":295,\"designation\":\"Backend Applikation realisieren\",\"cost\":215.0}";
        makePut(request, json, HttpStatus.SC_OK);
    }

    @Test
    public void deleteExistingModule_thenOk() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/module/999");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_OK,
                httpResponse.getStatusLine().getStatusCode()
        );
    }

    @Test
    public void deleteNotExistingModule_thenNotFound() throws IOException {
        HttpUriRequest request = new HttpDelete(SERVICE_URL + "/module/999999");
        addAuthorizationHeader(request);
        HttpResponse httpResponse = HttpClientBuilder
                .create()
                .build()
                .execute(request);

        assertEquals(
                HttpStatus.SC_NOT_FOUND,
                httpResponse.getStatusLine().getStatusCode()
        );
    }
}
