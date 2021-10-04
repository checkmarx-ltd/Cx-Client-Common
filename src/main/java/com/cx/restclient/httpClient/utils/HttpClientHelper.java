package com.cx.restclient.httpClient.utils;


import com.cx.restclient.common.ErrorMessage;
import com.cx.restclient.common.ErrorUtil;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Galn on 06/02/2018.
 */
public abstract class HttpClientHelper {
    public static <T> T convertToObject(HttpResponse response, Class<T> responseType, boolean isCollection) throws IOException, CxClientException {
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (response.getEntity() == null) {
                return null;
            }
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            return mapper.readValue(json, valueType);

        } catch (IOException e) {
            throw new CxClientException("Failed to parse json response: " + e.getMessage());
        }
    }

    public static String convertToJson(Object o) throws CxClientException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new CxClientException("Failed convert object to json: " + e.getMessage());
        }
    }

    private static <T> T convertToCollectionObject(HttpResponse response, JavaType javaType) throws CxClientException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new CxClientException("Failed to parse json response: " + e.getMessage(), e);
        }
    }

    public static void validateResponse(HttpResponse response, int status, String message) throws CxClientException {
        if (response.getStatusLine().getStatusCode() != status) {
            if (ErrorUtil.isServerErrorCodes(response.getStatusLine().getStatusCode())) {
                throw new CxClientException(ErrorMessage.SERVICE_UNAVAILABLE.getErrorMessage());
            }
            String responseBody = extractResponseBody(response);
            responseBody = responseBody.replace("{", "").replace("}", "").replace(System.getProperty("line.separator"), " ").replace("  ", "");
            throw new CxHTTPClientException(response.getStatusLine().getStatusCode(), message + ": " + responseBody);
        }

    }

    public static String extractResponseBody(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent());
        } catch (Exception e) {
            return "";
        }
    }
}
