package com.cx.restclient.common.summary;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.Policy;

import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.scansummary.ScanSummary;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.sca.dto.SCAResults;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SummaryUtils {

    public static String generateSummary(SASTResults sastResults, OSAResults osaResults, SCAResults scaResults, CxScanConfig config) throws IOException, TemplateException {

        Configuration cfg = new Configuration(new Version("2.3.23"));
        cfg.setClassForTemplateLoading(SummaryUtils.class, "/com/cx/report");
        Template template = cfg.getTemplate("report.ftl");

        Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("config", config);
        templateData.put("sast", sastResults);

        // TODO: null value for "osa" should be handled inside the template.
        templateData.put("osa", osaResults != null ? osaResults : new OSAResults());
        templateData.put("sca", scaResults != null ? scaResults : new SCAResults());

        DependencyResult dependencyResult = resolveDependencyResult(osaResults,scaResults);

        templateData.put("dependencyResult", dependencyResult !=null ? dependencyResult : new DependencyResult());


        ScanSummary scanSummary = new ScanSummary(config, sastResults, osaResults, scaResults);

        //calculated params:

        boolean buildFailed = false;
        boolean policyViolated = false;
        int policyViolatedCount = 0;
        //sast:
        if (config.getSastEnabled()) {
            if (sastResults.isSastResultsReady()) {
                boolean sastThresholdExceeded = scanSummary.isSastThresholdExceeded();
                boolean sastNewResultsExceeded = scanSummary.isSastThresholdForNewResultsExceeded();
                templateData.put("sastThresholdExceeded", sastThresholdExceeded);
                templateData.put("sastNewResultsExceeded", sastNewResultsExceeded);
                buildFailed = sastThresholdExceeded || sastNewResultsExceeded;
                //calculate sast bars:
                float maxCount = Math.max(sastResults.getHigh(), Math.max(sastResults.getMedium(), sastResults.getLow()));
                float sastBarNorm = maxCount * 10f / 9f;

                //sast high bars
                float sastHighTotalHeight = (float) sastResults.getHigh() / sastBarNorm * 238f;
                float sastHighNewHeight = calculateNewBarHeight(sastResults.getNewHigh(), sastResults.getHigh(), sastHighTotalHeight);
                float sastHighRecurrentHeight = sastHighTotalHeight - sastHighNewHeight;
                templateData.put("sastHighTotalHeight", sastHighTotalHeight);
                templateData.put("sastHighNewHeight", sastHighNewHeight);
                templateData.put("sastHighRecurrentHeight", sastHighRecurrentHeight);

                //sast medium bars
                float sastMediumTotalHeight = (float) sastResults.getMedium() / sastBarNorm * 238f;
                float sastMediumNewHeight = calculateNewBarHeight(sastResults.getNewMedium(), sastResults.getMedium(), sastMediumTotalHeight);
                float sastMediumRecurrentHeight = sastMediumTotalHeight - sastMediumNewHeight;
                templateData.put("sastMediumTotalHeight", sastMediumTotalHeight);
                templateData.put("sastMediumNewHeight", sastMediumNewHeight);
                templateData.put("sastMediumRecurrentHeight", sastMediumRecurrentHeight);

                //sast low bars
                float sastLowTotalHeight = (float) sastResults.getLow() / sastBarNorm * 238f;
                float sastLowNewHeight = calculateNewBarHeight(sastResults.getNewLow(), sastResults.getLow(), sastLowTotalHeight);
                float sastLowRecurrentHeight = sastLowTotalHeight - sastLowNewHeight;
                templateData.put("sastLowTotalHeight", sastLowTotalHeight);
                templateData.put("sastLowNewHeight", sastLowNewHeight);
                templateData.put("sastLowRecurrentHeight", sastLowRecurrentHeight);
            } else {
                buildFailed = true;
            }
        }

/*
        //osa:
        if (config.getDependencyScannerType() == DependencyScannerType.OSA) {
            if (osaResults!=null && osaResults.isOsaResultsReady()) {
                boolean thresholdExceeded = scanSummary.isOsaThresholdExceeded();
                templateData.put("osaThresholdExceeded", thresholdExceeded);
                buildFailed |= thresholdExceeded;

                //calculate osa bars:
                OSASummaryResults osaSummaryResults = osaResults.getResults();
                int osaHigh = osaSummaryResults.getTotalHighVulnerabilities();
                int osaMedium = osaSummaryResults.getTotalMediumVulnerabilities();
                int osaLow = osaSummaryResults.getTotalLowVulnerabilities();
                float osaMaxCount = Math.max(osaHigh, Math.max(osaMedium, osaLow));
                float osaBarNorm = osaMaxCount * 10f / 9f;

                float osaHighTotalHeight = (float) osaHigh / osaBarNorm * 238f;
                float osaMediumTotalHeight = (float) osaMedium / osaBarNorm * 238f;
                float osaLowTotalHeight = (float) osaLow / osaBarNorm * 238f;

                templateData.put("osaHighTotalHeight", osaHighTotalHeight);
                templateData.put("osaMediumTotalHeight", osaMediumTotalHeight);
                templateData.put("osaLowTotalHeight", osaLowTotalHeight);
                } else {
                buildFailed = true;
            }
            } else if (config.getDependencyScannerType() == DependencyScannerType.SCA){
                boolean thresholdExceeded = scanSummary.isOsaThresholdExceeded();
                templateData.put("scaThresholdExceeded", thresholdExceeded);
                buildFailed |= thresholdExceeded;

                //calculate sca bars:
                SCASummaryResults scaSummaryResults = scaResults.getSummary();
                int scaHigh = scaSummaryResults.getHighVulnerabilityCount();
                int scaMedium = scaSummaryResults.getMediumVulnerabilityCount();
                int scaLow = scaSummaryResults.getLowVulnerabilityCount();
                float scaMaxCount = Math.max(scaHigh, Math.max(scaMedium, scaLow));
                float scaBarNorm = scaMaxCount * 10f / 9f;

                float scaHighTotalHeight = (float) scaHigh / scaBarNorm * 238f;
                float scaMediumTotalHeight = (float) scaMedium / scaBarNorm * 238f;
                float scaLowTotalHeight = (float) scaLow / scaBarNorm * 238f;

                templateData.put("scaHighTotalHeight", scaHighTotalHeight);
                templateData.put("scaMediumTotalHeight", scaMediumTotalHeight);
                templateData.put("scaLowTotalHeight", scaLowTotalHeight);
            }else{
                buildFailed = true;
            }
*/

        if (config.getScannerType() == ScannerType.OSA || config.getScannerType() == ScannerType
        .SCA) {
            if (dependencyResult!=null && dependencyResult.isResultReady()) {
                boolean thresholdExceeded = scanSummary.isOsaThresholdExceeded();
                templateData.put("dependencyThresholdExceeded", thresholdExceeded);
                if(config.getSastEnabled()){
                    buildFailed |= thresholdExceeded || buildFailed;
                }else{
                    buildFailed |= thresholdExceeded;
                }

                //calculate dependency results bars:
                //DependencyResult dependencyResult1 = dependencyResult;
                int dependencyHigh = dependencyResult.getHighVulnerability();
                int dependencyMedium = dependencyResult.getMediumVulnerability();
                int dependencyLow = dependencyResult.getLowVulnerability();
                float dependencyMaxCount = Math.max(dependencyHigh, Math.max(dependencyMedium,dependencyLow));
                float dependencyBarNorm = dependencyMaxCount * 10f / 9f;


                float dependencyHighTotalHeight = (float) dependencyHigh / dependencyBarNorm * 238f;
                float dependencyMediumTotalHeight = (float) dependencyMedium / dependencyBarNorm * 238f;
                float dependencyLowTotalHeight = (float) dependencyLow / dependencyBarNorm * 238f;

                templateData.put("dependencyHighTotalHeight",   dependencyHighTotalHeight);
                templateData.put("dependencyMediumTotalHeight", dependencyMediumTotalHeight);
                templateData.put("dependencyLowTotalHeight",    dependencyLowTotalHeight);
            }else{
                buildFailed = true;
            }
        }


        if (config.getEnablePolicyViolations()) {
            Map<String, String> policies = new HashMap<String, String>();

            if (config.getSastEnabled() && sastResults.getSastPolicies().size() > 0) {
                policyViolated = true;
                policies = sastResults.getSastPolicies().stream().collect(
                        Collectors.toMap(Policy::getPolicyName,
                                Policy::getRuleName,
                                (left, right) -> {
                                    return left;
                                }
                        ));
            }

            if (config.getScannerType() == ScannerType.OSA &&
                    osaResults.getOsaPolicies().size() > 0) {
                policyViolated = true;
                policies.putAll(osaResults.getOsaPolicies().stream().collect(
                        Collectors.toMap(Policy::getPolicyName, Policy::getRuleName,
                                (left, right) -> {
                                    return left;
                                })));
            }

            policyViolatedCount = policies.size();
            String policyLabel = policyViolatedCount == 1 ? "Policy" : "Policies";
            templateData.put("policyLabel", policyLabel);
            templateData.put("policyViolatedCount", policyViolatedCount);
        }


        templateData.put("policyViolated", policyViolated);
        buildFailed |= policyViolated;
        templateData.put("buildFailed", buildFailed);

        //generate the report:
        StringWriter writer = new StringWriter();
        template.process(templateData, writer);
        return writer.toString();
    }

    private static DependencyResult resolveDependencyResult(OSAResults osaResults, SCAResults scaResults){
        DependencyResult dependencyResult;
        if(osaResults!=null){
            dependencyResult = new  DependencyResult(osaResults);
        }else if(scaResults!=null){
            dependencyResult = new DependencyResult(scaResults);
        }else{
            dependencyResult = null;
        }
        return dependencyResult;
    }

    private static float calculateNewBarHeight(int newCount, int count, float totalHeight) {
        int minimalVisibilityHeight = 5;
        //new high
        float highNewHeightPx = (float) newCount / (float) count * totalHeight;
        //if new height is between 1 and 9 - give it a minimum height and if theres enough spce in total height
        if (isNewNeedChange(totalHeight, highNewHeightPx, minimalVisibilityHeight)) {
            highNewHeightPx = minimalVisibilityHeight;
        }

        return highNewHeightPx;
    }

    private static boolean isNewNeedChange(float highTotalHeightPx, float highNewHeightPx, int minimalVisibilityHeight) {
        return highNewHeightPx > 0 && highNewHeightPx < minimalVisibilityHeight && highTotalHeightPx > minimalVisibilityHeight * 2;
    }
}