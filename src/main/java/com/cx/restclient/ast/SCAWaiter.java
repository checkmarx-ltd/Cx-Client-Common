package com.cx.restclient.ast;

import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.ast.dto.sca.ScanInfoResponse;
import com.cx.restclient.ast.dto.sca.ScanStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
public class SCAWaiter {
    private final CxHttpClient httpClient;
    private final CxScanConfig config;
    private long startTimestampSec;

    public void waitForScanToFinish(String scanId) {
        startTimestampSec = System.currentTimeMillis() / 1000;
        Duration timeout = getTimeout(config);
        Duration pollInterval = getPollInterval(config);

        int maxErrorCount = getMaxErrorCount(config);
        AtomicInteger errorCounter = new AtomicInteger();

        try {
            String urlPath = String.format(UrlPaths.GET_SCAN,
                    URLEncoder.encode(scanId, AstScaClient.ENCODING));

            Awaitility.await()
                    .atMost(timeout)
                    .pollDelay(Duration.ZERO)
                    .pollInterval(pollInterval)
                    .until(() -> scanIsCompleted(urlPath, errorCounter, maxErrorCount));

        } catch (ConditionTimeoutException e) {
            String message = String.format(
                    "Failed to perform CxSCA scan. The scan has been automatically aborted: " +
                            "reached the user-specified timeout (%d minutes).", timeout.toMinutes());
            throw new CxClientException(message);
        } catch (UnsupportedEncodingException e) {
            log.error("Unexpected error.", e);
        }
    }

    private static Duration getTimeout(CxScanConfig config) {
        Integer rawTimeout = config.getOsaScanTimeoutInMinutes();
        final int DEFAULT_TIMEOUT = 30;
        rawTimeout = rawTimeout != null && rawTimeout > 0 ? rawTimeout : DEFAULT_TIMEOUT;
        return Duration.ofMinutes(rawTimeout);
    }

    private static Duration getPollInterval(CxScanConfig config) {
        int rawPollInterval = ObjectUtils.defaultIfNull(config.getOsaProgressInterval(), 20);
        return Duration.ofSeconds(rawPollInterval);
    }

    private static int getMaxErrorCount(CxScanConfig config) {
        return ObjectUtils.defaultIfNull(config.getConnectionRetries(), 3);
    }

    private boolean scanIsCompleted(String path, AtomicInteger errorCounter, int maxErrorCount) {
        ScanInfoResponse response = null;
        String errorMessage = null;
        try {
            response = httpClient.getRequest(path, ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ScanInfoResponse.class, HttpStatus.SC_OK, "CxSCA scan", false);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        boolean completedSuccessfully = false;
        if (response == null) {
            // A network error is likely to have occurred -> retry.
            countError(errorCounter, maxErrorCount, errorMessage);
        } else {
            ScanStatus status = extractScanStatusFrom(response);
            completedSuccessfully = handleScanStatus(status);
        }

        return completedSuccessfully;
    }

    private boolean handleScanStatus(ScanStatus status) {
        boolean completedSuccessfully = false;
        if (status == ScanStatus.COMPLETED) {
            completedSuccessfully = true;
        } else if (status == ScanStatus.FAILED) {
            // Scan has failed on the back end, no need to retry.
            throw new CxClientException(String.format("Scan status is %s, aborting.", status));
        }
        else if (status == null) {
            log.warn("Unknown status.");
        }
        return completedSuccessfully;
    }

    private void countError(AtomicInteger errorCounter, int maxErrorCount, String message) {
        int currentErrorCount = errorCounter.incrementAndGet();
        int triesLeft = maxErrorCount - currentErrorCount;
        if (triesLeft < 0) {
            String fullMessage = String.format("Maximum number of errors was reached (%d), aborting.", maxErrorCount);
            throw new CxClientException(fullMessage);
        } else {
            String note = (triesLeft == 0 ? "last attempt" : String.format("tries left: %d", triesLeft));
            log.info(String.format("Failed to get status from CxSCA with the message: %s. Retrying (%s)", message, note));
        }
    }

    private ScanStatus extractScanStatusFrom(ScanInfoResponse response) {
        String rawStatus = response.getStatus();
        String elapsedTimestamp = ShragaUtils.getTimestampSince(startTimestampSec);
        log.info(String.format("Waiting for CxSCA scan results. Elapsed time: %s. Status: %s.", elapsedTimestamp, rawStatus));
        ScanStatus status = EnumUtils.getEnumIgnoreCase(ScanStatus.class, rawStatus);
        if (status == null) {
            log.warn(String.format("Unknown status: '%s'", rawStatus));
        }
        return status;
    }
}
