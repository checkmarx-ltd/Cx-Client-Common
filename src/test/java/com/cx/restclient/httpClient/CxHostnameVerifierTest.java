package com.cx.restclient.httpClient;

import org.junit.Test;

import static org.junit.Assert.*;

public class CxHostnameVerifierTest {

    // --- Exact match ---

    @Test
    public void exactMatch_sameCase() {
        assertTrue(CxHostnameVerifier.matchesPattern("server.checkmarx.com", "server.checkmarx.com"));
    }

    @Test
    public void exactMatch_caseInsensitive() {
        assertTrue(CxHostnameVerifier.matchesPattern("Server.Checkmarx.COM", "server.checkmarx.com"));
    }

    @Test
    public void exactMatch_noMatch() {
        assertFalse(CxHostnameVerifier.matchesPattern("other.checkmarx.com", "server.checkmarx.com"));
    }

    // --- Wildcard match (single level per RFC 6125) ---

    @Test
    public void wildcard_matchesSingleSubdomain() {
        assertTrue(CxHostnameVerifier.matchesPattern("sast.checkmarx.com", "*.checkmarx.com"));
    }

    @Test
    public void wildcard_caseInsensitive() {
        assertTrue(CxHostnameVerifier.matchesPattern("SAST.Checkmarx.COM", "*.checkmarx.com"));
    }

    @Test
    public void wildcard_doesNotMatchBareDomain() {
        // *.checkmarx.com should NOT match checkmarx.com itself
        assertFalse(CxHostnameVerifier.matchesPattern("checkmarx.com", "*.checkmarx.com"));
    }

    @Test
    public void wildcard_doesNotMatchMultiLevelSubdomain() {
        // RFC 6125: *.domain.com must not match a.b.domain.com
        assertFalse(CxHostnameVerifier.matchesPattern("a.b.checkmarx.com", "*.checkmarx.com"));
    }

    @Test
    public void wildcard_doesNotMatchDeeplyNested() {
        assertFalse(CxHostnameVerifier.matchesPattern("x.y.z.checkmarx.com", "*.checkmarx.com"));
    }

    @Test
    public void wildcard_doesNotMatchUnrelatedDomain() {
        assertFalse(CxHostnameVerifier.matchesPattern("sast.otherdomain.com", "*.checkmarx.com"));
    }

    // --- IP address exact match ---

    @Test
    public void ipAddress_exactMatch() {
        assertTrue(CxHostnameVerifier.matchesPattern("192.168.1.100", "192.168.1.100"));
    }

    @Test
    public void ipAddress_noMatch() {
        assertFalse(CxHostnameVerifier.matchesPattern("192.168.1.101", "192.168.1.100"));
    }

    @Test
    public void ipAddress_wildcardDoesNotApply() {
        // Wildcard patterns should not work for IP addresses
        assertFalse(CxHostnameVerifier.matchesPattern("192.168.1.100", "*.168.1.100"));
    }

    // --- Null and empty inputs ---

    @Test
    public void nullHostname_returnsFalse() {
        assertFalse(CxHostnameVerifier.matchesPattern(null, "*.checkmarx.com"));
    }

    @Test
    public void nullPattern_returnsFalse() {
        assertFalse(CxHostnameVerifier.matchesPattern("sast.checkmarx.com", null));
    }

    @Test
    public void bothNull_returnsFalse() {
        assertFalse(CxHostnameVerifier.matchesPattern(null, null));
    }

    @Test
    public void emptyHostname_returnsFalse() {
        assertFalse(CxHostnameVerifier.matchesPattern("", "*.checkmarx.com"));
    }

    @Test
    public void emptyPattern_returnsFalse() {
        assertFalse(CxHostnameVerifier.matchesPattern("sast.checkmarx.com", ""));
    }

    // --- Edge cases ---

    @Test
    public void wildcardOnly_doesNotMatchAnything() {
        // Pattern "*" (without dot) doesn't start with "*." so no wildcard match
        assertFalse(CxHostnameVerifier.matchesPattern("anything.com", "*"));
    }

    @Test
    public void wildcardDotOnly_doesNotMatchSingleLabel() {
        // "*." should not match a hostname with no further labels
        assertFalse(CxHostnameVerifier.matchesPattern("com", "*."));
    }

    @Test
    public void singleLabelHostname_noWildcardMatch() {
        assertFalse(CxHostnameVerifier.matchesPattern("localhost", "*.localhost"));
    }

    // --- verify() integration with config-based fallback ---

    @Test
    public void verify_configPatternMatches() {
        CxHostnameVerifier verifier = new CxHostnameVerifier("sast.checkmarx.com,*.internal.com");
        // DefaultHostnameVerifier will fail (no real SSL session), so it falls through to config
        // We can't easily mock SSLSession, but we can test that matchesPattern logic is correct
        // through the static method tests above. This test validates construction doesn't throw.
        assertNotNull(verifier);
    }

    @Test
    public void verify_nullConfig_constructsSuccessfully() {
        CxHostnameVerifier verifier = new CxHostnameVerifier(null);
        assertNotNull(verifier);
    }

    @Test
    public void verify_emptyConfig_constructsSuccessfully() {
        CxHostnameVerifier verifier = new CxHostnameVerifier("");
        assertNotNull(verifier);
    }
}
