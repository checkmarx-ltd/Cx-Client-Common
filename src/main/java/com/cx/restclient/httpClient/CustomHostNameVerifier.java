package com.cx.restclient.httpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class CustomHostNameVerifier implements HostnameVerifier {


    @Override
    public boolean verify(String hostname, SSLSession session) {
        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify(hostname, session);
    }
}
