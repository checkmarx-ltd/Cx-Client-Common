package com.cx.restclient.httpClient;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

/**
 * Custom HostnameVerifier with multi-level fallback:
 * 1. Delegates to Apache's DefaultHostnameVerifier (RFC 2818: CN, SAN, wildcard matching)
 * 2. Falls back to allowedHostname from config property (CLI only, null for other plugins)
 * 3. Falls back to CX_ALLOWED_HOSTS environment variable (comma-separated patterns, all plugins)
 * 4. If all fail, returns false (connection rejected)
 */
public class CxHostnameVerifier implements HostnameVerifier {

    private static final Logger log = LoggerFactory.getLogger(CxHostnameVerifier.class);
    private static final String ENV_ALLOWED_HOSTS = "CX_ALLOWED_HOSTS";

    private final DefaultHostnameVerifier defaultVerifier = new DefaultHostnameVerifier();
    private final String allowedHostname;
    private final String envAllowedHostname;

    public CxHostnameVerifier(String allowedHostname) {
        this.allowedHostname = allowedHostname;
        this.envAllowedHostname = System.getenv(ENV_ALLOWED_HOSTS);
        log.info("[HostnameVerifier] Initialized. Config ssl.allowed.hosts='{}', Env CX_ALLOWED_HOSTS='{}'",
                allowedHostname != null ? allowedHostname : "<not set>",
                envAllowedHostname != null ? envAllowedHostname : "<not set>");
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        log.info("[HostnameVerifier] ===== Starting hostname verification for '{}' =====", hostname);

        // Log certificate details for debugging
        logCertificateDetails(session);

        // Step 1: Try standard CN/SAN verification (RFC 2818)
        log.info("[HostnameVerifier] Step 1: Checking CN/SAN in certificate against hostname '{}'...", hostname);
        if (defaultVerifier.verify(hostname, session)) {
            log.info("[HostnameVerifier] Step 1 PASSED: Hostname '{}' matched CN/SAN in certificate. Connection allowed.", hostname);
            return true;
        }
        log.warn("[HostnameVerifier] Step 1 FAILED: Hostname '{}' did NOT match any CN or SAN entry in certificate.", hostname);

        // Step 2: Check allowedHostname from config property (ssl.allowed.hosts)
        log.info("[HostnameVerifier] Step 2: Checking config property 'ssl.allowed.hosts'...");
        if (allowedHostname != null && !allowedHostname.trim().isEmpty()) {
            String[] patterns = allowedHostname.split(",");
            log.info("[HostnameVerifier] Step 2: Config property has {} pattern(s): '{}'", patterns.length, allowedHostname);
            for (String pattern : patterns) {
                String trimmed = pattern.trim();
                log.info("[HostnameVerifier] Step 2: Comparing hostname '{}' against config pattern '{}'...", hostname, trimmed);
                if (matchesPattern(hostname, trimmed)) {
                    log.info("[HostnameVerifier] Step 2 PASSED: Hostname '{}' matched allowed hostname pattern '{}' from config property. Connection allowed.", hostname, trimmed);
                    return true;
                }
                log.info("[HostnameVerifier] Step 2: No match for pattern '{}'.", trimmed);
            }
            log.warn("[HostnameVerifier] Step 2 FAILED: Hostname '{}' did not match any config pattern.", hostname);
        } else {
            log.info("[HostnameVerifier] Step 2 SKIPPED: Config property 'ssl.allowed.hosts' is not set or empty.");
        }

        // Step 3: Check CX_ALLOWED_HOSTS environment variable (read once at construction)
        log.info("[HostnameVerifier] Step 3: Checking environment variable 'CX_ALLOWED_HOSTS'...");
        if (envAllowedHostname != null && !envAllowedHostname.trim().isEmpty()) {
            String[] patterns = envAllowedHostname.split(",");
            log.info("[HostnameVerifier] Step 3: Env variable has {} pattern(s): '{}'", patterns.length, envAllowedHostname);
            for (String pattern : patterns) {
                String trimmed = pattern.trim();
                log.info("[HostnameVerifier] Step 3: Comparing hostname '{}' against env pattern '{}'...", hostname, trimmed);
                if (matchesPattern(hostname, trimmed)) {
                    log.info("[HostnameVerifier] Step 3 PASSED: Hostname '{}' matched allowed hostname pattern '{}' from environment variable. Connection allowed.", hostname, trimmed);
                    return true;
                }
                log.info("[HostnameVerifier] Step 3: No match for pattern '{}'.", trimmed);
            }
            log.warn("[HostnameVerifier] Step 3 FAILED: Hostname '{}' did not match any env variable pattern.", hostname);
        } else {
            log.info("[HostnameVerifier] Step 3 SKIPPED: Environment variable 'CX_ALLOWED_HOSTS' is not set or empty.");
        }

        // Step 4: All checks failed
        log.error("[HostnameVerifier] Step 4: ALL CHECKS FAILED for hostname '{}'. Connection REJECTED.", hostname);
        log.error("[HostnameVerifier] To resolve: set 'ssl.allowed.hosts={}' in cx_console.properties OR set environment variable CX_ALLOWED_HOSTS={}", hostname, hostname);
        return false;
    }

    /**
     * Logs the certificate CN and SAN details from the SSL session.
     */
    private void logCertificateDetails(SSLSession session) {
        try {
            Certificate[] certs = session.getPeerCertificates();
            if (certs == null || certs.length == 0) {
                log.warn("[HostnameVerifier] No peer certificates found in SSL session.");
                return;
            }
            X509Certificate x509 = (X509Certificate) certs[0];

            // Log CN
            String subjectDN = x509.getSubjectX500Principal().getName();
            log.info("[HostnameVerifier] Certificate Subject DN: {}", subjectDN);

            // Extract and log CN
            String cn = extractCN(subjectDN);
            log.info("[HostnameVerifier] Certificate CN (Common Name): {}", cn != null ? cn : "<not present>");

            // Log SAN
            Collection<List<?>> sans = x509.getSubjectAlternativeNames();
            if (sans != null && !sans.isEmpty()) {
                StringBuilder sanList = new StringBuilder();
                for (List<?> san : sans) {
                    Integer type = (Integer) san.get(0);
                    Object value = san.get(1);
                    String typeLabel;
                    switch (type) {
                        case 2: typeLabel = "DNS"; break;
                        case 7: typeLabel = "IP"; break;
                        default: typeLabel = "Type-" + type; break;
                    }
                    if (sanList.length() > 0) sanList.append(", ");
                    sanList.append(typeLabel).append(":").append(value);
                }
                log.info("[HostnameVerifier] Certificate SAN (Subject Alternative Names): [{}]", sanList.toString());
            } else {
                log.info("[HostnameVerifier] Certificate SAN (Subject Alternative Names): <not present>");
            }

            // Log validity
            log.info("[HostnameVerifier] Certificate valid from {} to {}", x509.getNotBefore(), x509.getNotAfter());
            log.info("[HostnameVerifier] Certificate issuer: {}", x509.getIssuerX500Principal().getName());

        } catch (Exception e) {
            log.warn("[HostnameVerifier] Could not extract certificate details: {}", e.getMessage());
        }
    }

    /**
     * Extracts CN from a subject DN string like "CN=*.cxquality.com, OU=..., O=..."
     */
    private String extractCN(String subjectDN) {
        if (subjectDN == null) return null;
        for (String part : subjectDN.split(",")) {
            String trimmed = part.trim();
            if (trimmed.toUpperCase().startsWith("CN=")) {
                return trimmed.substring(3);
            }
        }
        return null;
    }

    /**
     * Supports exact match and wildcard patterns like *.domain.com.
     */
    static boolean matchesPattern(String hostname, String pattern) {
        if (hostname == null || pattern == null || hostname.isEmpty() || pattern.isEmpty()) {
            return false;
        }

        // Exact match (case-insensitive)
        if (hostname.equalsIgnoreCase(pattern)) {
            log.info("[HostnameVerifier]   -> Exact match: '{}' equals '{}' (case-insensitive)", hostname, pattern);
            return true;
        }

        // Wildcard match: *.domain.com should match sub.domain.com
        // but not domain.com or a.b.domain.com (RFC 6125 Section 6.4.3)
        // Wildcards do not apply to IP addresses.
        if (pattern.startsWith("*.") && !isIpAddress(hostname)) {
            String suffix = pattern.substring(1).toLowerCase(); // e.g., ".domain.com"
            String lower = hostname.toLowerCase();
            boolean endsWith = lower.endsWith(suffix);
            boolean singleLevel = !lower.substring(0, Math.max(0, lower.length() - suffix.length())).contains(".");
            log.info("[HostnameVerifier]   -> Wildcard check: hostname='{}', pattern='{}', endsWith('{}')={}, singleLevelSubdomain={}",
                    hostname, pattern, suffix, endsWith, singleLevel);
            return endsWith && singleLevel;
        }

        if (pattern.startsWith("*.") && isIpAddress(hostname)) {
            log.info("[HostnameVerifier]   -> Wildcard pattern '{}' skipped: hostname '{}' is an IP address (wildcards not applicable to IPs per RFC 6125)", pattern, hostname);
        }

        return false;
    }

    private static boolean isIpAddress(String hostname) {
        // Simple check: an IP address contains only digits and dots (IPv4)
        // or contains a colon (IPv6)
        return hostname.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") || hostname.contains(":");
    }
}
