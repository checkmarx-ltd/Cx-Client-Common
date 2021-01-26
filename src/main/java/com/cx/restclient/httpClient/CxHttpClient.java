package com.cx.restclient.httpClient;

import com.cx.restclient.common.ErrorMessage;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.dto.TokenLoginResponse;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.exception.CxTokenExpiredException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static com.cx.restclient.common.CxPARAM.AUTHENTICATION;
import static com.cx.restclient.common.CxPARAM.ORIGIN_HEADER;
import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON;
import static com.cx.restclient.httpClient.utils.HttpClientHelper.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * Created by Galn on 05/02/2018.
 */
public class CxHttpClient {

    private static String HTTP_NO_HOST = System.getProperty("http.nonProxyHosts");
    private static String HTTPS_NO_HOST = System.getProperty("https.nonProxyHosts");

    private static String HTTP_HOST = System.getProperty("http.proxyHost");
    private static String HTTP_PORT = System.getProperty("http.proxyPort");
    private static String HTTP_USERNAME = System.getProperty("http.proxyUser");
    private static String HTTP_PASSWORD = System.getProperty("http.proxyPassword");

    private static String HTTPS_HOST = System.getProperty("https.proxyHost");
    private static String HTTPS_PORT = System.getProperty("https.proxyPort");
    private static String HTTPS_USERNAME = System.getProperty("https.proxyUser");
    private static String HTTPS_PASSWORD = System.getProperty("https.proxyPassword");

    private static HttpClient apacheClient;

    private Logger logi;
    private TokenLoginResponse token;
    private String rootUri;
    private final String username;
    private final String password;
    private String cxOrigin;

    public CxHttpClient(String hostname, String username, String password, String origin,
                        boolean disableSSLValidation, Logger logi, boolean isProxy, String proxyHost, int proxyPort,
                        String proxyUser, String proxyPassword) throws MalformedURLException, CxClientException {
        this.logi = logi;
        this.username = username;
        this.password = password;
        this.rootUri = UrlUtils.parseURLToString(hostname, "CxRestAPI/");
        this.cxOrigin = origin;
        //create httpclient
        HttpClientBuilder cb = HttpClients.custom();
        cb.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        setSSLTls(cb, "TLSv1.2", logi);
        if (disableSSLValidation) {
            try {
                cb.setSSLSocketFactory(getSSLSF());
                cb.setConnectionManager(getHttpConnManager(true));
            } catch (CxClientException e) {
                logi.warn("Failed to disable certificate verification: " + e.getMessage());
            }
        } else {
            cb.setConnectionManager(getHttpConnManager(false));
        }
        cb.setConnectionManagerShared(true);
        if (isProxy) {
            if (proxyHost != null) {
                setCustomProxy(cb, proxyHost, proxyPort, proxyUser, proxyPassword, logi);
            } else {
                setProxy(cb, logi);
            }
        }

        cb.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        cb.setDefaultAuthSchemeRegistry(getAuthSchemeProviderRegistry());
        apacheClient = cb.build();
    }

    public CxHttpClient(String hostname, String username, String password, String origin, boolean disableSSLValidation, boolean isProxy, Logger logi) throws MalformedURLException, CxClientException {
        this(hostname, username, password, origin, disableSSLValidation, logi, isProxy, null, 0, null, null);
    }

    private static void setCustomProxy(HttpClientBuilder cb, String proxyHost, int proxyPort, String proxyUser, String proxyPassword, Logger logi) {
        HttpHost proxy = null;
        if (!isEmpty(proxyHost)) {
            proxy = new HttpHost(proxyHost, proxyPort, "http");
            if (!isEmpty(proxyUser) && !isEmpty(proxyPassword)) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(proxyUser, proxyPassword));
                cb.setDefaultCredentialsProvider(credsProvider);
            }
        }
        if (proxy != null) {
            logi.info("Setting proxy for Checkmarx http client");
            cb.setProxy(proxy);
            cb.setRoutePlanner(getRoutePlanner(proxy, logi));
            cb.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        }
    }

    private static void setProxy(HttpClientBuilder cb, Logger logi) {
        HttpHost proxyHost = null;
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        if (!isEmpty(HTTPS_HOST) && !isEmpty(HTTPS_PORT)) {
            proxyHost = new HttpHost(HTTPS_HOST, Integer.parseInt(HTTPS_PORT), "https");
            if (!isEmpty(HTTPS_USERNAME) && !isEmpty(HTTPS_PASSWORD)) {
                credsProvider.setCredentials(new AuthScope(HTTPS_HOST, Integer.parseInt(HTTPS_PORT)), new UsernamePasswordCredentials(HTTPS_USERNAME, HTTPS_PASSWORD));
                cb.setDefaultCredentialsProvider(credsProvider);
            }
        } else if (!isEmpty(HTTP_HOST) && !isEmpty(HTTP_PORT)) {
            proxyHost = new HttpHost(HTTP_HOST, Integer.parseInt(HTTP_PORT), "http");
            if (!isEmpty(HTTP_USERNAME) && !isEmpty(HTTP_PASSWORD)) {
                credsProvider.setCredentials(new AuthScope(HTTP_HOST, Integer.parseInt(HTTP_PORT)), new UsernamePasswordCredentials(HTTP_USERNAME, HTTP_PASSWORD));
                cb.setDefaultCredentialsProvider(credsProvider);
            }
        }
        if (proxyHost != null) {
            logi.info("Setting proxy for Checkmarx http client");
            cb.setRoutePlanner(getRoutePlanner(proxyHost, logi));
            cb.setProxy(proxyHost);
            cb.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        }
    }

    private static DefaultProxyRoutePlanner getRoutePlanner(HttpHost proxyHost, Logger logi) {
        return new DefaultProxyRoutePlanner(proxyHost) {
            public HttpRoute determineRoute(
                    final HttpHost host,
                    final HttpRequest request,
                    final HttpContext context) throws HttpException {
                String hostname = host.getHostName();

                String noHost = StringUtils.isNotEmpty(HTTP_NO_HOST) ? HTTP_NO_HOST : HTTPS_NO_HOST;
                if (StringUtils.isNotEmpty(noHost)) {
                    String[] hosts = noHost.split("\\|");
                    for (String nonHost : hosts) {
                        try {
                            if (hostname.matches(nonHost)) {
                                System.out.println("Host matched: " + nonHost);
                            }
                        } catch (PatternSyntaxException e) {
                            logi.warn("Wrong nonProxyHost param: " + nonHost);
                        }
                    }
                }
                return super.determineRoute(host, request, context);
            }
        };
    }

    private static SSLConnectionSocketFactory getSSLSF() throws CxClientException {
        TrustStrategy acceptingTrustStrategy = new TrustAllStrategy();
        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new CxClientException("Fail to set trust all certificate, 'SSLConnectionSocketFactory'", e);
        }
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    private static PoolingHttpClientConnectionManager getHttpConnManager(boolean disableSSLValidation) throws CxClientException {
        ConnectionSocketFactory sslConnectionFactory = disableSSLValidation ? getSSLSF()
                : new SSLConnectionSocketFactory(SSLContexts.createDefault());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setMaxTotal(50);
        connManager.setDefaultMaxPerRoute(5);
        return connManager;
    }

    private static Registry<AuthSchemeProvider> getAuthSchemeProviderRegistry() {
        return RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .build();
    }

    public void login() throws IOException, CxClientException {
        UrlEncodedFormEntity requestEntity = generateUrlEncodedFormEntity(" cxarm_api");
        HttpPost post = new HttpPost(rootUri + AUTHENTICATION);
        try {
            token = request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity, TokenLoginResponse.class, HttpStatus.SC_OK, "authenticate", false, false);
        } catch (CxClientException e) {
            if (!e.getMessage().contains("invalid_scope")) {
                throw e;
            }
            requestEntity = generateUrlEncodedFormEntity("");
            token = request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity, TokenLoginResponse.class, HttpStatus.SC_OK, "authenticate", false, false);
        }
    }


    private UrlEncodedFormEntity generateUrlEncodedFormEntity(String armScope) throws UnsupportedEncodingException {
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        parameters.add(new BasicNameValuePair("grant_type", "password"));
        parameters.add(new BasicNameValuePair("scope", "sast_rest_api" + armScope));
        parameters.add(new BasicNameValuePair("client_id", "resource_owner_client"));
        parameters.add(new BasicNameValuePair("client_secret", "014DF517-39D1-4453-B7B3-9930C563627C"));

        return new UrlEncodedFormEntity(parameters, "utf-8");
    }

    //GET REQUEST
    public <T> T getRequest(String relPath, String contentType, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection) throws IOException, CxClientException {
        return getRequest(rootUri, relPath, CONTENT_TYPE_APPLICATION_JSON, contentType, responseType, expectStatus, failedMsg, isCollection);
    }

    public <T> T getRequest(String rootURL, String relPath, String acceptHeader, String contentType, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection) throws IOException, CxClientException {
        HttpGet get = new HttpGet(rootURL + relPath);
        get.addHeader(HttpHeaders.ACCEPT, acceptHeader);
        return request(get, contentType, null, responseType, expectStatus, "get " + failedMsg, isCollection, true);
    }

    //POST REQUEST
    public <T> T postRequest(String relPath, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg) throws IOException, CxClientException {
        HttpPost post = new HttpPost(rootUri + relPath);
        return request(post, contentType, entity, responseType, expectStatus, failedMsg, false, true);
    }

    //PUT REQUEST
    public <T> T putRequest(String relPath, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg) throws IOException, CxClientException {
        HttpPut put = new HttpPut(rootUri + relPath);
        return request(put, contentType, entity, responseType, expectStatus, failedMsg, false, true);
    }

    //PATCH REQUEST
    public void patchRequest(String relPath, String contentType, HttpEntity entity, int expectStatus, String failedMsg) throws IOException, CxClientException {
        HttpPatch patch = new HttpPatch(rootUri + relPath);
        request(patch, contentType, entity, null, expectStatus, failedMsg, false, true);
    }

    private <T> T request(HttpRequestBase httpMethod, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection, boolean retry) throws IOException, CxClientException {
        if (contentType != null) {
            httpMethod.addHeader("Content-type", contentType);
        }
        if (entity != null && httpMethod instanceof HttpEntityEnclosingRequestBase) { //Entity for Post methods
            ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(entity);
        }
        HttpResponse response = null;
        int statusCode = 0;

        try {
            httpMethod.addHeader(ORIGIN_HEADER, cxOrigin);
            if (token != null) {
                httpMethod.addHeader(HttpHeaders.AUTHORIZATION, token.getToken_type() + " " + token.getAccess_token());
            }

            response = apacheClient.execute(httpMethod);
            statusCode = response.getStatusLine().getStatusCode();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) { //Token expired
                throw new CxTokenExpiredException(extractResponseBody(response));
            }

            validateResponse(response, expectStatus, "Failed to " + failedMsg);

            //extract response as object and return the link
            return convertToObject(response, responseType, isCollection);
        } catch (UnknownHostException e) {
            throw new CxHTTPClientException(ErrorMessage.CHECKMARX_SERVER_CONNECTION_FAILED.getErrorMessage());
        } catch (CxTokenExpiredException ex) {
            if (retry) {
                logi.warn("Access token expired for request: " + httpMethod.getURI() + ", Status code:" + statusCode + "requesting a new token. message: " + ex.getMessage());
                login();
                return request(httpMethod, contentType, entity, responseType, expectStatus, failedMsg, isCollection, false);
            }
            throw ex;
        } finally {
            httpMethod.releaseConnection();
            HttpClientUtils.closeQuietly(response);
        }
    }

    public void close() {
        HttpClientUtils.closeQuietly(apacheClient);
    }

    private void setSSLTls(HttpClientBuilder builder, String protocol, Logger log) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().useProtocol(protocol).build();
            builder.setSSLContext(sslContext);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn("Failed to set SSL TLS : " + e.getMessage());
        }
    }

}
