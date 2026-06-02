package com.cx.restclient.httpClient;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class CxHostnameVerifier implements HostnameVerifier {

    private static final Logger log = LoggerFactory.getLogger(CxHostnameVerifier.class);
    private static final String ENV_ALLOWED_HOSTS = "CX_ALLOWED_HOSTS";

    private final DefaultHostnameVerifier defaultVerifier = new DefaultHostnameVerifier();
    private final String allowedHostname;
    private final String envAllowedHostname;

    public CxHostnameVerifier(String allowedHostname) {
        this.allowedHostname = allowedHostname;
        this.envAllowedHostname = System.getenv(ENV_ALLOWED_HOSTS);
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (defaultVerifier.verify(hostname, session)) {
            return true;
        }

        if (allowedHostname != null && !allowedHostname.trim().isEmpty()) {
            for (String pattern : allowedHostname.split(",")) {
                if (matchesPattern(hostname, pattern.trim())) {
                    return true;
                }
            }
        }

        if (envAllowedHostname != null && !envAllowedHostname.trim().isEmpty()) {
            for (String pattern : envAllowedHostname.split(",")) {
                if (matchesPattern(hostname, pattern.trim())) {
                    return true;
                }
            }
        }

        log.error("Hostname verification failed for '{}'. Certificate CN/SAN did not match and no allowed-hosts entry covered it. To allow this host, set 'ssl.allowed.hosts={}' in cx_console.properties or environment variable CX_ALLOWED_HOSTS={}.", hostname, hostname, hostname);
        return false;
    }

    /**
     * Supports exact match and wildcard patterns like *.domain.com.
     */
    static boolean matchesPattern(String hostname, String pattern) {
        if (hostname == null || pattern == null || hostname.isEmpty() || pattern.isEmpty()) {
            return false;
        }

        if (hostname.equalsIgnoreCase(pattern)) {
            return true;
        }

        // Wildcard match: *.domain.com matches sub.domain.com but not domain.com or a.b.domain.com (RFC 6125 Section 6.4.3).
        // Wildcards do not apply to IP addresses.
        if (pattern.startsWith("*.") && !isIpAddress(hostname)) {
            String suffix = pattern.substring(1).toLowerCase();
            String lower = hostname.toLowerCase();
            boolean endsWith = lower.endsWith(suffix);
            boolean singleLevel = !lower.substring(0, Math.max(0, lower.length() - suffix.length())).contains(".");
            return endsWith && singleLevel;
        }

        return false;
    }

    private static boolean isIpAddress(String hostname) {
        return hostname.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
                || hostname.matches(".*:.*:.*");
    }
}
