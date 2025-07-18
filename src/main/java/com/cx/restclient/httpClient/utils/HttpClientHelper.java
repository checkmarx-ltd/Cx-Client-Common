package com.cx.restclient.httpClient.utils;


import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Galn on 06/02/2018.
 */
public abstract class HttpClientHelper {
    private HttpClientHelper() {
    }

    public static <T> T convertToObject(HttpResponse response, Class<T> responseType, boolean isCollection) throws IOException, CxClientException {

        if (responseType != null && responseType.isInstance(response)) {
            return (T) response;
        }

        // If the caller is asking for the whole response, return the response (instead of just its entity),
        // no matter if the entity is empty.
        if (responseType != null && responseType.isAssignableFrom(response.getClass())) {
            return (T) response;
        }

        //No content
        if (responseType == null || response.getEntity() == null || response.getEntity().getContentLength() == 0) {
            return null;
        }
        ///convert to byte[]
        if (responseType.equals(byte[].class)) {
            return (T) IOUtils.toByteArray(response.getEntity().getContent());
        }
        //convert to List<T>
        if (isCollection) {
            return convertToCollectionObject(response, TypeFactory.defaultInstance().constructCollectionType(List.class, responseType));
        }

        //convert to T
        return convertToStrObject(response, responseType);
    }

    private static <T> T convertToStrObject(HttpResponse response, Class<T> valueType) throws CxClientException {
        ObjectMapper mapper = getObjectMapper();
        try {
            if (response.getEntity() == null) {
                return null;
            }
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            if (valueType.equals(String.class)) {
                return (T) json;
            }
            return mapper.readValue(json, valueType);

        } catch (IOException e) {
            throw new CxClientException("Failed to parse json response: " + e.getMessage());
        }
    }

    public static String convertToJson(Object o) throws CxClientException {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new CxClientException("Failed convert object to json: " + e.getMessage());
        }
    }

    public static StringEntity convertToStringEntity(Object o) throws CxClientException, UnsupportedEncodingException {
        return new StringEntity(convertToJson(o));
    }

    private static <T> T convertToCollectionObject(HttpResponse response, JavaType javaType) throws CxClientException {
        ObjectMapper mapper = getObjectMapper();
        try {
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new CxClientException("Failed to parse json response: " + e.getMessage(), e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper result = new ObjectMapper();

        // Prevent UnrecognizedPropertyException if additional fields are added to API responses.
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return result;
    }

    public static void validateResponse(HttpResponse response, int expectedStatus, String message) throws CxClientException {
        int actualStatusCode = response.getStatusLine().getStatusCode();
        if (actualStatusCode != expectedStatus) {
            String responseBody = extractResponseBody(response);
            String readableBody = responseBody.replace("{", "")
                    .replace("}", "")
                    .replace(System.getProperty("line.separator"), " ")
                    .replace("  ", "");

            String exceptionMessage = String.format("Status code: %d, message: '%s', response body: %s",
                    actualStatusCode, message, readableBody);

            throw new CxHTTPClientException(actualStatusCode, exceptionMessage, responseBody);
        }
    }

    public static String extractResponseBody(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent());
        } catch (Exception e) {
            return "";
        }
    }

    public static byte[] getSBOMReport(String fileUrl) throws IOException {
        HttpGet get = new HttpGet(fileUrl);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toByteArray(response.getEntity());
            } else {
                throw new IOException("Failed to fetch file. Status code: " + statusCode);
            }
        }
    }
    
}
