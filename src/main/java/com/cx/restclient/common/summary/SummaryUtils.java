package com.cx.restclient.common.summary;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.scansummary.ScanSummary;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sca.AstScaResults;


import freemarker.core.ParseException;

import com.cx.restclient.ast.dto.sca.report.PolicyEvaluation;


import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;
import com.cx.restclient.dto.DummyResults;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SummaryUtils {
    private SummaryUtils() {
    }

	public static String generateSummary(SASTResults sastResults, OSAResults osaResults, AstScaResults scaResults,
			CxScanConfig config) throws IOException, TemplateException {

        return generateSummaryHelper(sastResults, osaResults, scaResults, null, config);
    }

	private static String generateSummaryHelper(SASTResults sastResults, OSAResults osaResults,
			AstScaResults scaResults, AstSastResults cxOneSastResults, CxScanConfig config) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Configuration cfg = new Configuration(new Version("2.3.23"));
        cfg.setClassForTemplateLoading(SummaryUtils.class, "/com/cx/report");
        Template template = cfg.getTemplate("report.ftl");
        
        Map<String, Object> templateData = new HashMap<>();
        // Below condition is to check if pipeline is migrated to AST.Setting the boolean to true for further use in report ftl file
        // Based on the flag set, the respective sections in report.ftl are being displayed
        if(cxOneSastResults != null) {
        	config.setIsAstMigrate(true);
        }
        templateData.put("config", config);
        if(sastResults != null){
        	templateData.put("sast", sastResults);
        } else if(cxOneSastResults != null) {
        	templateData.put("cxOnesast", cxOneSastResults);
        }
        if(osaResults != null && config.isOsaEnabled()){
        	templateData.put("osa", osaResults);
        } else if(scaResults != null) {
        	templateData.put("sca", scaResults);
        }
        DependencyScanResult dependencyScanResult = resolveDependencyResult(osaResults, scaResults, config);

        if(dependencyScanResult == null) {
        	dependencyScanResult = new DependencyScanResult();
        	if(config.isAstScaEnabled())
        		dependencyScanResult.setScannerType(ScannerType.AST_SCA);
        	else if(config.isOsaEnabled())
        		dependencyScanResult.setScannerType(ScannerType.OSA);
        }
        templateData.put("dependencyResult", dependencyScanResult);
        // Following dummy result object is being put in the template data has to be used in report.ftl in scenarios when encodeXSS is called in a scenario, 
        // when the type of result is unknown
        DummyResults dummyResults = new DummyResults();
        templateData.put("dummyResults", dummyResults);
        ScanSummary scanSummary = null; 
		if (cxOneSastResults != null) {
			scanSummary = new ScanSummary(config, sastResults, osaResults, scaResults, cxOneSastResults);
		} else {
			scanSummary = new ScanSummary(config, sastResults, osaResults, scaResults);
		}
        //calculated params:

        boolean buildFailed = false;
        boolean policyViolated = false;
        int policyViolatedCount;
        //sast:
        if (config.isSastEnabled()) {
            if (sastResults != null && sastResults.isSastResultsReady() && cxOneSastResults==null) {
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
            } else if(cxOneSastResults != null) {
            	if (cxOneSastResults.isCxoneSastResultsReady()) {
                    boolean cxOnesastThresholdExceeded = scanSummary.isCxOneSastThresholdExceeded();
                    boolean cxOneNewResultsExceeded = scanSummary.isCxOneSastThresholdForNewResultsExceeded();
                    templateData.put("cxonesastThresholdExceeded", cxOnesastThresholdExceeded);
                    templateData.put("cxonesastNewResultsExceeded", cxOneNewResultsExceeded);
                    buildFailed = cxOnesastThresholdExceeded || cxOneNewResultsExceeded;
                    //calculate sast bars:
                    float maxCount = Math.max(cxOneSastResults.getHigh(), Math.max(cxOneSastResults.getMedium(), cxOneSastResults.getLow()));
                    float sastBarNorm = maxCount * 10f / 9f;

                    //sast high bars
                    float cxonesastHighTotalHeight = (float) cxOneSastResults.getHigh() / sastBarNorm * 238f;
                    float cxonesastHighNewHeight = calculateNewBarHeight(cxOneSastResults.getNewHigh(), cxOneSastResults.getHigh(), cxonesastHighTotalHeight);
                    float cxonesastHighRecurrentHeight = cxonesastHighTotalHeight - cxonesastHighNewHeight;
                    templateData.put("cxonesastHighTotalHeight", cxonesastHighTotalHeight);
                    templateData.put("cxonesastHighNewHeight", cxonesastHighNewHeight);
                    templateData.put("cxonesastHighRecurrentHeight", cxonesastHighRecurrentHeight);

                    //sast medium bars
                    float cxonesastMediumTotalHeight = (float) cxOneSastResults.getMedium() / sastBarNorm * 238f;
                    float cxonesastMediumNewHeight = calculateNewBarHeight(cxOneSastResults.getNewMedium(), cxOneSastResults.getMedium(), cxonesastMediumTotalHeight);
                    float cxonesastMediumRecurrentHeight = cxonesastMediumTotalHeight - cxonesastMediumNewHeight;
                    templateData.put("cxonesastMediumTotalHeight", cxonesastMediumTotalHeight);
                    templateData.put("cxonesastMediumNewHeight", cxonesastMediumNewHeight);
                    templateData.put("cxonesastMediumRecurrentHeight", cxonesastMediumRecurrentHeight);

                    //sast low bars
                    float cxonesastLowTotalHeight = (float) cxOneSastResults.getLow() / sastBarNorm * 238f;
                    float cxonesastLowNewHeight = calculateNewBarHeight(cxOneSastResults.getNewLow(), cxOneSastResults.getLow(), cxonesastLowTotalHeight);
                    float cxonesastLowRecurrentHeight = cxonesastLowTotalHeight - cxonesastLowNewHeight;
                    templateData.put("cxonesastLowTotalHeight", cxonesastLowTotalHeight);
                    templateData.put("cxonesastLowNewHeight", cxonesastLowNewHeight);
                    templateData.put("cxonesastLowRecurrentHeight", cxonesastLowRecurrentHeight);
            		
            	}else {
                buildFailed = true;
            	}
            }
        } 

        if (config.isOsaEnabled() || config.isAstScaEnabled()) {
            if (dependencyScanResult != null && dependencyScanResult.isResultReady()) {
                boolean thresholdExceeded = scanSummary.isOsaThresholdExceeded();
                templateData.put("dependencyThresholdExceeded", thresholdExceeded);
                if (config.isSastEnabled()) {
                    buildFailed |= thresholdExceeded || buildFailed;
                } else {
                    buildFailed |= thresholdExceeded;
                }

                //calculate dependency results bars:
                int dependencyHigh = dependencyScanResult.getHighVulnerability();
                int dependencyMedium = dependencyScanResult.getMediumVulnerability();
                int dependencyLow = dependencyScanResult.getLowVulnerability();
                float dependencyMaxCount = Math.max(dependencyHigh, Math.max(dependencyMedium, dependencyLow));
                float dependencyBarNorm = dependencyMaxCount * 10f / 9f;


                float dependencyHighTotalHeight = (float) dependencyHigh / dependencyBarNorm * 238f;
                float dependencyMediumTotalHeight = (float) dependencyMedium / dependencyBarNorm * 238f;
                float dependencyLowTotalHeight = (float) dependencyLow / dependencyBarNorm * 238f;

                templateData.put("dependencyHighTotalHeight", dependencyHighTotalHeight);
                templateData.put("dependencyMediumTotalHeight", dependencyMediumTotalHeight);
                templateData.put("dependencyLowTotalHeight", dependencyLowTotalHeight);
            } else {
                buildFailed = true;
            }
        }


        if (config.getEnablePolicyViolations()) {
            Map<String, String> policies = new HashMap<>();


            if (Boolean.TRUE.equals(config.isSastEnabled())
                    && sastResults != null
                    && sastResults.getSastPolicies() != null
                    && !sastResults.getSastPolicies().isEmpty()) {
                policyViolated = true;
                policies.putAll(sastResults.getSastPolicies().stream().collect(
                        Collectors.toMap(Policy::getPolicyName, Policy::getRuleName,
                                (left, right) -> left)));
            }

            if (Boolean.TRUE.equals(config.isOsaEnabled())
                    && osaResults != null
                    && osaResults.getOsaPolicies() != null
                    && !osaResults.getOsaPolicies().isEmpty()) {
                policyViolated = true;
                policies.putAll(osaResults.getOsaPolicies().stream().collect(
                        Collectors.toMap(Policy::getPolicyName, Policy::getRuleName,
                                (left, right) -> left)));
            }
           
            if(Boolean.TRUE.equals(config.isAstScaEnabled())
                    && scaResults != null && scaResults.getPolicyEvaluations() != null
                            && !scaResults.getPolicyEvaluations().isEmpty())
            {
            	policyViolated = true;
            	
            	policies.putAll(scaResults.getPolicyEvaluations().stream().filter(policy -> policy.getIsViolated()).collect(
                        Collectors.toMap(PolicyEvaluation::getName, PolicyEvaluation::getId,
                                (left, right) -> left)));
            	if(policies.size()==0)
            	{
            		policyViolated = false;
            	}
            }
            
            
            if(scanSummary.isPolicyViolated()) {
            	buildFailed = true;
            	policyViolated = true;
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
	public static String generateSummary(SASTResults sastResults, OSAResults osaResults, AstScaResults scaResults,
			AstSastResults astSastResults, CxScanConfig config) throws IOException, TemplateException {

        return generateSummaryHelper(sastResults, osaResults, scaResults, astSastResults, config);
    }
    private static DependencyScanResult resolveDependencyResult(OSAResults osaResults, AstScaResults scaResults, CxScanConfig config) {
        DependencyScanResult dependencyScanResult;
        if (osaResults != null && config.isOsaEnabled()) {
            dependencyScanResult = new DependencyScanResult(osaResults);
        } else if (scaResults != null) {
            dependencyScanResult = new DependencyScanResult(scaResults);
        } else {
            dependencyScanResult = null;
        }
        return dependencyScanResult;
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