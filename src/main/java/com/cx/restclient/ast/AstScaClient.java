package com.cx.restclient.ast;
import static com.cx.restclient.sast.utils.SASTParam.MAX_ZIP_SIZE_BYTES;
import static com.cx.restclient.sast.utils.SASTParam.SAST_CREATE_REPORT;
import static com.cx.restclient.sast.utils.SASTParam.SCA_RESOLVER_RESULT_FILE_NAME;
import static com.cx.restclient.sast.utils.SASTParam.TEMP_FILE_NAME_TO_SCA_RESOLVER_RESULTS_ZIP;
import static com.cx.restclient.sast.utils.SASTParam.TEMP_FILE_NAME_TO_ZIP;
import static com.cx.restclient.common.CxPARAM.CX_REPORT_LOCATION;
import static com.cx.restclient.httpClient.utils.ContentType.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.velocity.runtime.parser.node.SetExecutor;
import org.awaitility.core.ConditionTimeoutException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.cx.restclient.ast.dto.common.HandlerRef;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.common.ScanConfig;
import com.cx.restclient.ast.dto.common.ScanConfigValue;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.ast.dto.sca.CreateProjectRequest;
import com.cx.restclient.ast.dto.sca.Project;
import com.cx.restclient.ast.dto.sca.ScaScanConfigValue;
import com.cx.restclient.ast.dto.sca.Team;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.ast.dto.sca.report.Package;
import com.cx.restclient.ast.dto.sca.report.PolicyEvaluation;
import com.cx.restclient.common.CxPARAM;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.LoginSettings;
import com.cx.restclient.dto.PathFilter;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.httpClient.utils.HttpClientHelper;
import com.cx.restclient.osa.dto.ClientType;
import com.cx.restclient.osa.utils.OSAUtils;
import com.cx.restclient.sast.utils.SASTParam;
import com.cx.restclient.sast.utils.SASTUtils;
import com.cx.restclient.sast.utils.State;
import com.cx.restclient.sast.utils.zip.CxZipUtils;
import com.cx.restclient.sast.utils.zip.NewCxZipFile;
import com.cx.restclient.sast.utils.zip.Zipper;

import com.cx.restclient.sca.dto.CxSCAResolvingConfiguration;

import com.cx.restclient.sca.utils.CxSCAFileSystemUtils;
import com.cx.restclient.sca.utils.fingerprints.CxSCAScanFingerprints;
import com.cx.restclient.sca.utils.fingerprints.FingerprintCollector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * SCA - Software Composition Analysis - is the successor of OSA.
 */
public class AstScaClient extends AstClient implements Scanner {
    private static final String RISK_MANAGEMENT_API = properties.get("astSca.riskManagementApi");
    private static final String PROJECTS = RISK_MANAGEMENT_API + properties.get("astSca.projects");
    private static final String SUMMARY_REPORT = RISK_MANAGEMENT_API + properties.get("astSca.summaryReport");
    private static final String FINDINGS = RISK_MANAGEMENT_API + properties.get("astSca.findings");
    private static final String PACKAGES = RISK_MANAGEMENT_API + properties.get("astSca.packages");
    private static final String LATEST_SCAN = RISK_MANAGEMENT_API + properties.get("astSca.latestScan");
    private static final String WEB_REPORT = properties.get("astSca.webReport");
    private static final String RESOLVING_CONFIGURATION_API = properties.get("astSca.resolvingConfigurationApi");
    private static final String REPORTID_API = RISK_MANAGEMENT_API + properties.get("astSca.reportId");
    private static final String POLICY_MANAGEMENT_API = properties.get("astSca.policyManagementApi");
    private static final String POLICY_MANAGEMENT_EVALUATION_API = POLICY_MANAGEMENT_API + properties.get("astSca.policyManagementEvaliation");
    private static final String TEAMBYID = properties.get("astSca.teamById");
    private static final String REPORT_SCA_PACKAGES = "cxSCAPackages";
    private static final String REPORT_SCA_FINDINGS = "cxSCAVulnerabilities";
    private static final String REPORT_SCA_SUMMARY = "cxSCASummary";
    private static final String JSON_EXTENSION = ".json";

    private static final String ENGINE_TYPE_FOR_API = "sca";

    private static final String TENANT_HEADER_NAME = "Account-Name";

    private static final ObjectMapper caseInsensitiveObjectMapper = new ObjectMapper()
            // Ignore any fields that can be added to SCA API in the future.
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // We need this feature to properly deserialize finding severity,
            // e.g. "High" (in JSON) -> Severity.HIGH (in Java).
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    private final AstScaConfig astScaConfig;


    private String projectId;
    private String scanId;
    private String reportId;
    private File tempUploadFile;
    private final FingerprintCollector fingerprintCollector;
    private CxSCAResolvingConfiguration resolvingConfiguration;
    private static final String FINGERPRINT_FILE_NAME = ".cxsca.sig";
    private static final String SCA_CONFIG_FOLDER_NAME = ".cxsca.configurations";

    public AstScaClient(CxScanConfig config, Logger log) {
        super(config, log);

        this.astScaConfig = config.getAstScaConfig();
        validate(astScaConfig);
        

        httpClient = createHttpClient(astScaConfig.getApiUrl());
        this.resolvingConfiguration = null;
        fingerprintCollector = new FingerprintCollector(log);
        // Pass tenant name in a custom header. This will allow to get token from on-premise access control server
        // and then use this token for SCA authentication in cloud.
        httpClient.addCustomHeader(TENANT_HEADER_NAME, config.getAstScaConfig().getTenant());
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SCA.getDisplayName();
    }

    @Override
    protected ScanConfig getScanConfig() {

        String sastProjectId = config.getAstScaConfig().getSastProjectId();
        String sastServerUrl = config.getAstScaConfig().getSastServerUrl();
        String sastUsername = config.getAstScaConfig().getSastUsername();
        String sastPassword = config.getAstScaConfig().getSastPassword();
        String sastProjectName = config.getAstScaConfig().getSastProjectName();

        Map<String, String> envVariables = config.getAstScaConfig().getEnvVariables();
        JSONObject envJsonString = new JSONObject(envVariables);

        ScanConfigValue configValue = ScaScanConfigValue.builder()
                .environmentVariables(envJsonString.toString())
                .sastProjectId(sastProjectId)
                .sastServerUrl(sastServerUrl)
                .sastUsername(sastUsername)
                .sastPassword(sastPassword)
                .sastProjectName(sastProjectName)
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();

    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        if (StringUtils.isNotEmpty(repoInfo.getBranch())) {
            // If we pass the branch to start scan API, the API will return an error:
            // "Git references (branch, commit ID, etc.) are not yet supported."
            //
            // We can't just ignore the branch, because it will lead to confusion.
            String message = String.format("Branch specification is not yet supported by %s.", getScannerDisplayName());
            throw new CxClientException(message);
        }
        return null;
    }

    /**
     * Transforms the repo URL if credentials are specified in repoInfo.
     */
    @Override
    protected URL getEffectiveRepoUrl(RemoteRepositoryInfo repoInfo) {
    	
        URL result;
        URL initialUrl = repoInfo.getUrl();
    

        // Otherwise we may get something like "https://mytoken:null@github.com".
        String username = StringUtils.defaultString(repoInfo.getUsername());
        String password = StringUtils.defaultString(repoInfo.getPassword());

        try {
            if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(password)) {
                log.info("Adding credentials as the userinfo part of the URL, because {} only supports this kind of authentication.",
                        getScannerDisplayName());

                result = new URIBuilder(initialUrl.toURI())
                        .setUserInfo(username, password)
                        .build()
                        .toURL();
            } else {
                result = repoInfo.getUrl();
            }
        } catch (Exception e) {
            throw new CxClientException("Error getting effective repo URL.");
        }
        return result;
    }

    @Override
    public Results init() {
        log.debug("Initializing {} client.", getScannerDisplayName());
        AstScaResults scaResults = new AstScaResults();
        
        try {
        	log.debug("ASC init before login.");
        	System.out.println("ASC init before login.");
            login();
            log.debug("ASC init after login.");
            System.out.println("ASC init after login.");
        } catch (Exception e) {
        	log.error("ASC init exception:"+e.getMessage());
        	System.out.println("ASC init exception:"+e.getMessage());
        	e.printStackTrace();
            super.handleInitError(e, scaResults);
            log.debug("ASC init after super handleInitError: scaResults:"+scaResults);
            System.out.println("ASC init after super handleInitError: scaResults:"+scaResults);
        }
        log.debug("ASC init completed successfully");
        System.out.println("ASC init completed successfully");
        return scaResults;
    }

    public CxSCAResolvingConfiguration getCxSCAResolvingConfigurationForProject(String projectId) throws IOException {
        log.info("Resolving configuration for project: {}", projectId);
        String path = String.format(RESOLVING_CONFIGURATION_API, URLEncoder.encode(projectId, ENCODING));

        return httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                CxSCAResolvingConfiguration.class,
                HttpStatus.SC_OK,
                "get CxSCA resolving configuration",
                false);
    }

    
	private byte[] getReport(String scanId, String contentType) throws IOException {
		String SCA_GET_REPORT = "/risk-management/risk-reports/{scan_id}/export?format={file_type}";

		return httpClient.getRequest(SCA_GET_REPORT.replace("{scan_id}", scanId).replace("{file_type}", contentType),
				contentType, byte[].class, 200, " scan report: " + reportId, false);
	}
	//cli reports
	 public static void writeReport(byte[] scanReport, String reportName, Logger log) {
	        try {
	            File reportFile = new File(reportName);
	            if (!reportFile.isAbsolute()) {
	                reportFile = new File(System.getProperty("user.dir") + CX_REPORT_LOCATION + File.separator + reportFile);
	            }

	            if (!reportFile.getParentFile().exists()) {
	                reportFile.getParentFile().mkdirs();
	            }

	            FileUtils.writeByteArrayToFile(reportFile, scanReport);
	            log.info("Report location: " + reportFile.getAbsolutePath());
	        } catch (Exception e) {
	            log.error("Failed to write report: ", e.getMessage());
	        }
	    }
    /**
     * Waits for SCA scan to finish, then gets scan results.
     *
     * @throws CxClientException in case of a network error, scan failure or when scan is aborted by timeout.
     */
    @Override
    public Results waitForScanResults() {
        AstScaResults scaResults;
        try {
            waitForScanToFinish(scanId);
            scaResults = tryGetScanResults().orElseThrow(() -> new CxClientException("Unable to get scan results: scan not found."));
            if (config.getScaJsonReport() != null) {
                OSAUtils.writeJsonToFile(REPORT_SCA_FINDINGS + JSON_EXTENSION, scaResults.getFindings(), config.getReportsDir(), config.getOsaGenerateJsonReport(), log);
                OSAUtils.writeJsonToFile(REPORT_SCA_PACKAGES + JSON_EXTENSION, scaResults.getPackages(), config.getReportsDir(), config.getOsaGenerateJsonReport(), log);
                OSAUtils.writeJsonToFile(REPORT_SCA_SUMMARY + JSON_EXTENSION, scaResults.getSummary(), config.getReportsDir(), config.getOsaGenerateJsonReport(), log);
            }
            
			if (config.isGenerateScaReport()) {
				String reportFormat = config.getScaReportFormat();
				log.info("Generating SCA report. Report type: " + reportFormat);
				byte[] scanReport = getReport(scaResults.getScanId(), reportFormat);
				scaResults.setPDFReport(scanReport);
				String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
				String PDF_REPORT_NAME = "AstScaReport";
				String fileName = "";
				if (reportFormat.equalsIgnoreCase("CSV")) {
					reportFormat = "zip";
				}

				fileName = PDF_REPORT_NAME + "_" + now + "." + reportFormat.toLowerCase();
				String pdfLink = SASTUtils.writePDFReport(scanReport, config.getReportsDir(), fileName, log);
				if (reportFormat.toLowerCase().equals("pdf")) {
					scaResults.setScaPDFLink(pdfLink);
					scaResults.setPdfFileName(fileName);
				}

			}
            return scaResults;
        } catch (CxClientException e) {
            log.error(e.getMessage());
            scaResults = new AstScaResults();
            scaResults.setException(e);
        }
        catch(IOException e) {
        	log.error(e.getMessage());
        } catch(ConditionTimeoutException e) {
        	log.error(e.getMessage());
            scaResults = new AstScaResults();
        	scaResults.setException(new CxClientException(e));
        } 
        return new AstScaResults();
       // return scaResults;
    }

    @Override
    protected void uploadArchive(byte[] source, String uploadUrl) throws IOException {
        log.info("Uploading the zipped data.");
        CxHttpClient uploader = null;
        HttpEntity request = new ByteArrayEntity(source);

        try {
            uploader = createHttpClient(uploadUrl);

            // Relative path is empty, because we use the whole upload URL as the base URL for the HTTP client.
            // Content type is empty, because the server at uploadUrl throws an error if Content-Type is non-empty.
            uploader.putRequest("", "", request, JsonNode.class, HttpStatus.SC_OK, "upload ZIP file");
        }finally {
            Optional.ofNullable(uploader).ifPresent(CxHttpClient::close);
        }

    }
    
    @Override
    public Results initiateScan() {
        log.info("----------------------------------- Initiating {} Scan:------------------------------------",
                getScannerDisplayName());
        AstScaResults scaResults = new AstScaResults();
        scanId = null;
        projectId = null;
        try {
            AstScaConfig scaConfig = config.getAstScaConfig();
            SourceLocationType locationType = scaConfig.getSourceLocationType();
            HttpResponse response;

            projectId = resolveRiskManagementProject();
            boolean isManifestAndFingerprintsOnly = !config.getAstScaConfig().isIncludeSources();
            if (isManifestAndFingerprintsOnly) {
                this.resolvingConfiguration = getCxSCAResolvingConfigurationForProject(this.projectId);
                log.info("Got the following manifest patterns {}", this.resolvingConfiguration.getManifests());
                log.info("Got the following fingerprint patterns {}", this.resolvingConfiguration.getFingerprints());
            }

            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(scaConfig, projectId);
            } else {
                if (scaConfig.isIncludeSources()) {
                    response = submitAllSourcesFromLocalDir(projectId, astScaConfig.getZipFilePath());
                } else if(scaConfig.isEnableScaResolver()) {	
                	response = submitScaResolverEvidenceFile(scaConfig);
                }else {
                    response = submitManifestsAndFingerprintsFromLocalDir(projectId);
                }
            }
            this.scanId = extractScanIdFrom(response);
            scaResults.setScanId(scanId);
            if(scaConfig.isEnableScaResolver() && tempUploadFile != null){
                log.info("Deleting uploaded file for scan {}", tempUploadFile.getAbsolutePath());
                if(!tempUploadFile.delete())
                {
                    log.error("Error while deleting uploaded file for scan {}", tempUploadFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while initiating scan.", e);
            setState(State.FAILED);
            scaResults.setException(new CxClientException("Error creating scan.", e));         
        }
        return scaResults;
    }

    protected HttpResponse submitAllSourcesFromLocalDir(String projectId, String zipFilePath) throws IOException {
        log.info("Using local directory flow.");

        PathFilter filter = new PathFilter(config.getOsaFolderExclusions(), config.getOsaFilterPattern(), log);
        String sourceDir = config.getEffectiveSourceDirForDependencyScan();

        Path configFileDestination = copyConfigFileToSourceDir(sourceDir);

        byte[] zipFile = CxZipUtils.getZippedSources(config, filter, sourceDir, log);

        
        FileUtils.deleteDirectory(configFileDestination.toFile());

        return initiateScanForUpload(projectId, zipFile, config.getAstScaConfig());
    }

    /**
     * This method
     *  1) executes sca resolver to generate result json file.
     *  2) create ScaResolverResultsxxxx.zip file with sca resolver result json file to be uploaded for scan
     *  3) Execute initiateScan method to generate SCA scan.
     * @param scaConfig - AST Sca config object
     * @return - Returns the response
     * @throws IOException
     */
    private HttpResponse submitScaResolverEvidenceFile(AstScaConfig scaConfig) throws IOException,CxClientException {
        log.info("Executing SCA Resolver flow.");
    	log.info("Path to Sca Resolver: {}", scaConfig.getPathToScaResolver());
    	File zipFile;
    	String pathToResultJSONFile = "";
        String pathToSASTResultJSONFile = ""; 
        String pathToResultJSONFileNew= "";
        String pathToSASTResultJSONFileNew= "";

        String scaResultPathArgName = getScaResultPathArgumentName(scaConfig);
        if(scaResultPathArgName != "") {
        	try {
        		pathToResultJSONFile = getScaResolverResultFilePathFromAdditionalParams(scaConfig.getScaResolverAddParameters(), scaResultPathArgName);     
            } catch (ParseException e) {
                throw new CxClientException(e.getMessage());
            }
        }
        
        log.info("SCA resolver result path configured: " + pathToResultJSONFile);

        String timeStamp = getTimestampFolder();
        pathToResultJSONFileNew = createTimestampBasedPath(pathToResultJSONFile, timeStamp, SASTParam.SCA_RESOLVER_RESULT_FILE_NAME);
        if (checkSastResultPath(scaConfig)) {
        	try {
        		pathToSASTResultJSONFile = getScaResolverResultFilePathFromAdditionalParams(scaConfig.getScaResolverAddParameters(), "--sast-result-path");                
                
            } catch (ParseException e) {
                throw new CxClientException(e.getMessage());
            }
            log.info("SAST result path location configured: " + pathToSASTResultJSONFile);
            pathToSASTResultJSONFileNew = createTimestampBasedPath(pathToSASTResultJSONFile, timeStamp, SASTParam.SAST_RESOLVER_RESULT_FILE_NAME);
        }
        log.info("Launching dependency resolution by ScaResolver. ScaResolver logs can be viewed in debug level logs of the pipeline."); 
        int exitCode = SpawnScaResolver.runScaResolver(scaConfig.getPathToScaResolver(), scaConfig.getScaResolverAddParameters(),pathToResultJSONFileNew,pathToSASTResultJSONFileNew, log);
        if (exitCode == 0) {
        	log.info("Dependency resolution completed."); 
			String parentDir = pathToResultJSONFileNew.substring(0, pathToResultJSONFileNew.lastIndexOf(File.separator));
			String parentDirSast = "";
			File destPartentSastDir = null;
			if (!StringUtils.isEmpty(pathToSASTResultJSONFileNew)) {
				parentDirSast = pathToSASTResultJSONFileNew.substring(0,
						pathToSASTResultJSONFileNew.lastIndexOf(File.separator));
				destPartentSastDir = new File(parentDirSast);
			}

            String tempDirectory = parentDir +  File.separator + "tmp";
            String tempResultFile =  tempDirectory + File.separator + SASTParam.SCA_RESOLVER_RESULT_FILE_NAME;
            String tempSASTResultFile =  tempDirectory + File.separator + SASTParam.SAST_RESOLVER_RESULT_FILE_NAME;
            log.debug("Copying ScaResolver result files to temporary location.");
            File destTempDir = new File(tempDirectory);
			File destParentDir = new File(parentDir);
			if (!destTempDir.exists()) {
				Files.createDirectory(destTempDir.toPath());
			}

			Files.copy(new File(pathToResultJSONFileNew).toPath(), new File(tempResultFile).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
            if(!StringUtils.isEmpty(pathToSASTResultJSONFileNew)) 
            	Files.copy(new File(pathToSASTResultJSONFileNew).toPath(), new File(tempSASTResultFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Completed File copy to "+tempDirectory);
            zipFile = zipEvidenceFile(destTempDir);
            
			if (!pathToResultJSONFileNew.equals(pathToResultJSONFile)) {
				log.info("Deleting directory of result file {}", destParentDir.getAbsolutePath());
				FileUtils.deleteDirectory(destParentDir);
				log.info("Deleted directory of result file " + destParentDir.getAbsolutePath());
			} else {
				log.info("Deleting temporary uploaded file for scan {}", destTempDir.getAbsolutePath());
				FileUtils.deleteDirectory(destTempDir);
				log.info("Deleted temp directory " + destTempDir.getAbsolutePath());
			}
			if (!StringUtils.isEmpty(pathToSASTResultJSONFileNew) && !pathToSASTResultJSONFileNew.equals(pathToSASTResultJSONFile)) {
				log.info("Deleting directory of result file {}", destPartentSastDir.getAbsolutePath());
				FileUtils.deleteDirectory(destPartentSastDir);
				log.info("Deleted directory of result file " + destPartentSastDir.getAbsolutePath());
			}

		}else{
            throw new CxClientException("Error while running sca resolver executable. Exit code: "+exitCode);
        }
    	return initiateScanForUpload(projectId, FileUtils.readFileToByteArray(zipFile), config.getAstScaConfig());
    }

    public boolean checkSastResultPath(AstScaConfig scaConfig) {
    	if (scaConfig.getScaResolverAddParameters().contains("--sast-result-path")) {
            return true;
        }
        return false;
	}
	private String createTimestampBasedPath(String inputResultFilePath, String timeStamp,
			String targetFileName) {
    	 if(inputResultFilePath.isEmpty()) 
             return inputResultFilePath;

             String lastPathComponent = "";
             if (!inputResultFilePath.endsWith(File.separator)) {
                 lastPathComponent = inputResultFilePath.substring(inputResultFilePath.lastIndexOf(File.separator)+1, inputResultFilePath.length());
             }
             if (inputResultFilePath.endsWith(File.separator) || lastPathComponent.indexOf(".") == -1) {                     
                     //if so, it's a directory
                     if(!inputResultFilePath.endsWith(File.separator))
                         inputResultFilePath = inputResultFilePath + File.separator ;
                     inputResultFilePath = inputResultFilePath + timeStamp + File.separator + targetFileName;  
	}else {
        //Honor user's choice to create file at given absolute path
        return inputResultFilePath;
    }
             return inputResultFilePath;
    }
	private String getTimestampFolder() {
    	SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Date date = new Date();
        Timestamp tt = new Timestamp(System.currentTimeMillis());
       
        
        String ss =(sd.format(tt));
        String res = ss.replace(".", "");
        return res;
	}

	private String getScaResultPathArgumentName(AstScaConfig scaConfig) {
    	String scaResolverResultPathArgName = "";
        
        
		if (scaConfig.getScaResolverAddParameters().contains("--resolver-result-path")) {
            scaResolverResultPathArgName = "--resolver-result-path";
        }else if (scaConfig.getScaResolverAddParameters().contains("-r")) {
            scaResolverResultPathArgName = "-r";
        }
        return scaResolverResultPathArgName;
	}

	/**
     * This method returns SCA Resolver execution result file path. SCA Resolver additional
     * parameter has result file location. Appending result directory path with file name
     * .cxsca-results.json
     * @param scaResolverAddParams - SCA resolver additional parameters
     * @return - SCA resolver execution result file path.
     */
	public String getScaResolverResultFilePathFromAdditionalParams(String scaResolverAddParams,String arg)throws ParseException {
        String[] argument;
        String resolverResultPath = "";
        argument = scaResolverAddParams.split(" ");
        for (int i = 0; i < argument.length; i++) {
            if (arg.equals(argument[i])) {
                if (argument.length - 1 == i) {
                resolverResultPath = argument[i];
                }
                else {
                    resolverResultPath = argument[i + 1] ;
                }
                break;
            }
        }
        return resolverResultPath;
    }

    private HttpResponse submitManifestsAndFingerprintsFromLocalDir(String projectId) throws IOException {
        log.info("Using manifest only and fingerprint flow");
        String sourceDir = config.getEffectiveSourceDirForDependencyScan();
        Path configFileDestination = copyConfigFileToSourceDir(sourceDir);
        String additinalFilters = getAdditionalManifestFilters();
        String finalFilters =  additinalFilters + getManifestsIncludePattern();
        PathFilter userFilter = new PathFilter(config.getOsaFolderExclusions(), config.getOsaFilterPattern(), log);
        if (ArrayUtils.isNotEmpty(userFilter.getIncludes()) && !ArrayUtils.contains(userFilter.getIncludes(), "**")) {
            userFilter.addToIncludes("**");
        }
        Set<String> scannedFileSet = new HashSet<>(Arrays.asList(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir, userFilter)));

        PathFilter manifestIncludeFilter = new PathFilter(null,finalFilters , log);
        if (manifestIncludeFilter.getIncludes().length == 0) {
            throw new CxClientException(String.format("Using manifest only mode requires include filter. Resolving config does not have include patterns defined: %s", getManifestsIncludePattern()));
        }

        List<String> filesToZip =
                Arrays.stream(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir, manifestIncludeFilter))
                        .filter(scannedFileSet::contains).
                        collect(Collectors.toList());

        List<String> filesToFingerprint =
                Arrays.stream(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir,
                        new PathFilter(null, getFingerprintsIncludePattern(), log)))
                        .filter(scannedFileSet::contains).
                        collect(Collectors.toList());


        CxSCAScanFingerprints fingerprints = fingerprintCollector.collectFingerprints(sourceDir, filesToFingerprint);

        File zipFile = zipDirectoryAndFingerprints(sourceDir, filesToZip, fingerprints);

        optionallyWriteFingerprintsToFile(fingerprints);


        FileUtils.deleteDirectory(configFileDestination.toFile());

        return initiateScanForUpload(projectId, FileUtils.readFileToByteArray(zipFile), astScaConfig);
    }

    /**
     * This file create zip file to
     * @param filePath - SCA Resolver evidence/result file path
     * @return - Zipped file
     * @throws IOException
     */
	private File zipEvidenceFile(File filePath) throws IOException {

        tempUploadFile = File.createTempFile(TEMP_FILE_NAME_TO_SCA_RESOLVER_RESULTS_ZIP, ".zip");
		String sourceDir = filePath.getAbsolutePath();

        log.info("Collecting files to zip archive: {}", tempUploadFile.getAbsolutePath());

        long maxZipSizeBytes = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;
        
        List<String> paths = Arrays.asList(filePath.list());
        try (NewCxZipFile zipper = new NewCxZipFile(tempUploadFile, maxZipSizeBytes, log)) {
            zipper.addMultipleFilesToArchive(new File(sourceDir), paths);
            log.info("Added {} files to zip.",  zipper.getFileCount());
            log.info("The sources were zipped to {}", tempUploadFile.getAbsolutePath());
            return tempUploadFile;
        } catch (Zipper.MaxZipSizeReached e) {
            throw handleFileDeletion(filePath, new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeBytes)));
        } catch (IOException ioException) {
            throw handleFileDeletion(filePath, ioException);
        }
    }

    /**
     * 
     * This method gets the additional config file(from different package manager) manifest filters 
     * e.g. returns "settings.xml,npmrc"/"
     **/
	 
	 
    
	private String getAdditionalManifestFilters() {
		List<String> configFilePaths = config.getAstScaConfig().getConfigFilePaths();
		String additionalFilters = "";
		if (configFilePaths != null) {
			for (String configFileString : configFilePaths) {
				if (StringUtils.isNotEmpty(configFileString)) {
					if (configFileString.lastIndexOf("\\") != -1)
						configFileString = configFileString.substring(configFileString.lastIndexOf("\\") + 1);
					additionalFilters = additionalFilters.concat("**/" + configFileString + ",");
				}
			}
		}
		return additionalFilters;
	}

    private Path copyConfigFileToSourceDir(String sourceDir) throws IOException {

        Path configFileDestination = Paths.get("");
        log.info("Source Directory : {}", sourceDir);
        List<String> configFilePaths = config.getAstScaConfig().getConfigFilePaths();

        if(configFilePaths != null) {
        for(String configFileString : configFilePaths) {

            if (StringUtils.isNotEmpty(configFileString)) {
                String fileSystemSeparator = FileSystems.getDefault().getSeparator();
                Path configFilePath = CxSCAFileSystemUtils.checkIfFileExists(sourceDir, configFileString, fileSystemSeparator, log);

                if (configFilePath != null) {
                    configFileDestination = Paths.get(sourceDir, fileSystemSeparator, SCA_CONFIG_FOLDER_NAME);

                    if (Files.notExists(configFileDestination)) {
                        Path destDir = Files.createDirectory(configFileDestination);
                        Files.copy(configFilePath, destDir.resolve(configFilePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        
                    } else {
                        Path r = configFileDestination.resolve(configFilePath.getFileName());
                        Files.copy(configFilePath,r , StandardCopyOption.REPLACE_EXISTING);
                        
                    }
                    log.info("Config file ({}) copied to directory: {}", configFilePath, configFileDestination);
                }
            }
        }
        }
        return configFileDestination;
    }

    

    private File zipDirectoryAndFingerprints(String sourceDir, List<String> paths, CxSCAScanFingerprints fingerprints) throws IOException {
        File result = config.getZipFile();
        if (result != null) {
            return result;
            
        }
        File tempFile = getZipFile();
        log.debug("Collecting files to zip archive: {}", tempFile.getAbsolutePath());
        long maxZipSizeBytes = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;
        

        try (NewCxZipFile zipper = new NewCxZipFile(tempFile, maxZipSizeBytes, log)) {
            zipper.addMultipleFilesToArchive(new File(sourceDir), paths);
            if (zipper.getFileCount() == 0 && fingerprints.getFingerprints().isEmpty()) {
                throw handleFileDeletion(tempFile);
            }
            if (!fingerprints.getFingerprints().isEmpty()) {
                zipper.zipContentAsFile(FINGERPRINT_FILE_NAME, FingerprintCollector.getFingerprintsAsJsonString(fingerprints).getBytes());
            } else {
                log.debug("No supported fingerprints found to zip");
            }

            log.debug("The sources were zipped to {}", tempFile.getAbsolutePath());
            return tempFile;
        } catch (Zipper.MaxZipSizeReached e) {
            throw handleFileDeletion(tempFile, new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeBytes)));
        } catch (IOException ioException) {
            throw handleFileDeletion(tempFile, ioException);
        }
    }

    private CxClientException handleFileDeletion(File file, IOException ioException) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            return new CxClientException(e);
        }

        return new CxClientException(ioException);

    }

    private CxClientException handleFileDeletion(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            return new CxClientException(e);
        }

        return new CxClientException("No files found to zip and no supported fingerprints found");
    }

    private String getFingerprintsIncludePattern() {
        if (StringUtils.isNotEmpty(astScaConfig.getFingerprintsIncludePattern())) {
            return astScaConfig.getFingerprintsIncludePattern();
        }

        return resolvingConfiguration.getFingerprintsIncludePattern();
    }

    private String getManifestsIncludePattern() {
        if (StringUtils.isNotEmpty(astScaConfig.getManifestsIncludePattern())) {
            return astScaConfig.getManifestsIncludePattern();
        }

        return resolvingConfiguration.getManifestsIncludePattern();
    }

    private File getZipFile() throws IOException {
        if (StringUtils.isNotEmpty(astScaConfig.getZipFilePath())) {
            return new File(astScaConfig.getZipFilePath());
        }
        return File.createTempFile(TEMP_FILE_NAME_TO_ZIP, ".bin");
    }

    private void optionallyWriteFingerprintsToFile(CxSCAScanFingerprints fingerprints) {
        if (StringUtils.isNotEmpty(astScaConfig.getFingerprintFilePath())) {
            try {
                fingerprintCollector.writeScanFingerprintsFile(fingerprints, astScaConfig.getFingerprintFilePath());
            } catch (IOException ioException) {
                log.error(String.format("Failed writing fingerprint file to %s", astScaConfig.getFingerprintFilePath()), ioException);
            }
        }
    }

    /**
     * Gets latest scan results using {@link CxScanConfig#getProjectName()} for the current config.
     *
     * @return results of the latest successful scan for a project, if present; null - otherwise.
     */
    @Override
    public Results getLatestScanResults() {
        AstScaResults result = new AstScaResults();
        try {
            log.info("Getting latest scan results.");
            projectId = getRiskManagementProjectId(config.getProjectName());
            scanId = getLatestScanId(projectId);
            result = tryGetScanResults().orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setException(new CxClientException("Error getting latest scan results.", e));
        }
        return result;
    }

    private Optional<AstScaResults> tryGetScanResults() {
        AstScaResults result = null;
        if (StringUtils.isNotEmpty(scanId)) {
            result = getScanResults();
        } else {
            log.info("Unable to get scan results");
        }
        return Optional.ofNullable(result);
    }

    private String getLatestScanId(String projectId) throws IOException {
        String result = null;
        if (StringUtils.isNotEmpty(projectId)) {
            log.debug("Getting latest scan ID for project ID: {}", projectId);
            String path = String.format(LATEST_SCAN, URLEncoder.encode(projectId, ENCODING));
            JsonNode response = httpClient.getRequest(path,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ArrayNode.class,
                    HttpStatus.SC_OK,
                    "scan ID by project ID",
                    false);

            result = Optional.ofNullable(response)
                    // 'riskReportId' is in fact scanId, but the name is kept for backward compatibility.
                    .map(resp -> resp.at("/0/riskReportId").textValue())
                    .orElse(null);
        }
        String message = (result == null ? "Scan not found" : String.format("Scan ID: %s", result));
        log.info(message);
        return result;
    }


    private void printWebReportLink(AstScaResults scaResult) {
        if (!StringUtils.isEmpty(scaResult.getWebReportLink())) {
            log.info("{} scan results location: {}", getScannerDisplayName(), scaResult.getWebReportLink());
        }
    }

    void testConnection() throws IOException {
        // The calls below allow to check both access control and API connectivity.
        login();
        getRiskManagementProjects();
    }

    public void login() throws IOException {
        log.info("Logging into {}", getScannerDisplayName());
        System.out.println("Logging into "+ getScannerDisplayName());
        AstScaConfig scaConfig = config.getAstScaConfig();

        String acUrl = scaConfig.getAccessControlUrl();
        LoginSettings settings = LoginSettings.builder()
                .accessControlBaseUrl(UrlUtils.parseURLToString(acUrl, CxPARAM.AUTHENTICATION))
                .username(scaConfig.getUsername())
                .password(scaConfig.getPassword())
                .tenant(scaConfig.getTenant())
                .build();
        log.info("ASC login acUrl"+acUrl);
        System.out.println("ASC login acUrl"+acUrl);
        ClientTypeResolver resolver = new ClientTypeResolver(config,log);
        ClientType clientType = resolver.determineClientType(acUrl);
        settings.setClientTypeForPasswordAuth(clientType);
        log.info("ASC login before httpClient login");
        System.out.println("ASC login before httpClient login");
        httpClient.login(settings);
        log.info("ASC login httpClient login successful");
        System.out.println("ASC login httpClient login successful");
    }
    
    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * The following config properties are used:
     * astScaConfig
     * proxyConfig
     * cxOrigin
     * disableCertificateValidation
     */
    public void testScaConnection() {
        try {
            testConnection();
        } catch (IOException e) {
            throw new CxClientException(e);
        }
    }
    
    private String resolveRiskManagementProject() throws IOException {
        String projectName = config.getProjectName();
        String assignedTeam = config.getAstScaConfig().getTeamPath();
        String assignedTeamId = config.getAstScaConfig().getTeamId();
                        
		if (!StringUtils.isEmpty(assignedTeamId)) {
			assignedTeam = getTeamById(assignedTeamId);
			
		} else if(StringUtils.isEmpty(assignedTeam)){
			
        	assignedTeam = config.getTeamPath();
        }
		
        log.info("Getting project by name: '{}'", projectName);
        String resolvedProjectId = getRiskManagementProjectId(projectName);
        if (resolvedProjectId == null) {
            log.info("Project not found, creating a new one.");
            resolvedProjectId = createRiskManagementProject(projectName, assignedTeam);
            log.info("Created a project with ID {}", resolvedProjectId);
        } else {
            log.info("Project already exists with ID {}", resolvedProjectId);
        }
        return resolvedProjectId;
    }

    private String getRiskManagementProjectId(String projectName) throws IOException {
        log.info("Getting project ID by name: '{}'", projectName);

        if (StringUtils.isEmpty(projectName)) {
            throw new CxClientException("Non-empty project name must be provided.");
        }

        Project project = sendGetProjectRequest(projectName);

        String result = Optional.ofNullable(project)
                .map(Project::getId)
                .orElse(null);

        String message = (result == null ? "Project not found" : String.format("Project ID: %s", result));
        log.info(message);

        return result;
    }

    private String getTeamById(String teamId) throws IOException {
        log.info("Getting Team name by ID : '{}'", teamId);

        if (StringUtils.isEmpty(teamId)) {
            throw new CxClientException("Team Id provided is empty.");
        }

        Team team = sendGetTeamById(teamId);

        String result = Optional.ofNullable(team)
                .map(Team::getFullName)
                .orElse(null);

        String message = (result == null ? "Team not found" : String.format("Team  name: %s", result));
        log.info(message);

        return result;
    }
    
    private Project sendGetProjectRequest(String projectName) throws IOException {
        Project result;
        try {
            String getProjectByName = String.format("%s?name=%s", PROJECTS, URLEncoder.encode(projectName, ENCODING));
            result = httpClient.getRequest(getProjectByName,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    Project.class,
                    HttpStatus.SC_OK,
                    "CxSCA project ID by name",
                    false);
        } catch (CxHTTPClientException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                result = null;
            } else {
                throw e;
            }
        }
        return result;
    }
    
    private Team sendGetTeamById(String teamId) throws IOException {
        Team result;
        try {        	
            String teamNameAPI = String.format("%s/%s", TEAMBYID, teamId);
            result = httpClient.getRequest(this.astScaConfig.getAccessControlUrl()+"/",teamNameAPI,ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    Team.class,
                    HttpStatus.SC_OK,
                    "CxSCA team ID by name",
                    false);
        } catch (CxHTTPClientException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                result = null;
            } else {
                throw e;
            }
        }
        return result;
    }

    private void getRiskManagementProjects() throws IOException {
        httpClient.getRequest(PROJECTS,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                Project.class,
                HttpStatus.SC_OK,
                "CxSCA projects",
                true);
    }

    private String createRiskManagementProject(String name, String assignedTeam) throws IOException {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);
        if(!StringUtils.isEmpty(assignedTeam)) {
        	request.addAssignedTeams(assignedTeam);        
        	log.info("Team name: {}", assignedTeam);
        }

        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        Project newProject = httpClient.postRequest(PROJECTS,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                entity,
                Project.class,
                HttpStatus.SC_CREATED,
                "create a project");

        return newProject.getId();
    }

    private AstScaResults getScanResults() {
        AstScaResults result;
        log.debug("Getting results for scan ID {}", scanId);
        try {
            result = new AstScaResults();
            result.setScanId(this.scanId);
           
            reportId = getReportId(scanId);
            result.setReportId(reportId);
            
            AstScaSummaryResults scanSummary = getSummaryReport(scanId);
            result.setSummary(scanSummary);
            printSummary(scanSummary, this.scanId);

            List<Finding> findings = getFindings(scanId);
            result.setFindings(findings);

            List<Package> packages = getPackages(scanId);
            result.setPackages(packages);            
            
            if(config.isEnablePolicyViolations()) {
            	List<PolicyEvaluation> policyEvaluations = getPolicyEvaluation(reportId);
            	result.setPolicyEvaluations(policyEvaluations);
            	printPolicyEvaluations(policyEvaluations);
            	determinePolicyViolations(result);
            }

            String reportLink = getWebReportLink(config.getAstScaConfig().getWebAppUrl());
            result.setWebReportLink(reportLink);
            printWebReportLink(result);
            result.setScaResultReady(true);
            log.info("Retrieved SCA results successfully.");
        } catch (IOException e) {
            throw new CxClientException("Error retrieving CxSCA scan results.", e);
        }
        return result;
    }

    @Override
    protected String getWebReportPath() throws UnsupportedEncodingException {
        return String.format(WEB_REPORT,
                URLEncoder.encode(projectId, ENCODING),
                URLEncoder.encode(scanId, ENCODING));
    }

    private AstScaSummaryResults getSummaryReport(String scanId) throws IOException {
        log.debug("Getting summary report.");

        String path = String.format(SUMMARY_REPORT, URLEncoder.encode(scanId, ENCODING));

        return httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                AstScaSummaryResults.class,
                HttpStatus.SC_OK,
                "CxSCA report summary",
                false);
    }

    private List<Finding> getFindings(String scanId) throws IOException {
        log.debug("Getting findings.");

        String path = String.format(FINDINGS, URLEncoder.encode(scanId, ENCODING));

        ArrayNode responseJson = httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ArrayNode.class,
                HttpStatus.SC_OK,
                "CxSCA findings",
                false);

        Finding[] findings = caseInsensitiveObjectMapper.treeToValue(responseJson, Finding[].class);

        return Arrays.asList(findings);
    }

    private List<Package> getPackages(String scanId) throws IOException {
        log.debug("Getting packages.");

        String path = String.format(PACKAGES, URLEncoder.encode(scanId, ENCODING));

        return (List<Package>) httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                Package.class,
                HttpStatus.SC_OK,
                "CxSCA findings",
                true);
    }
    
    public String getReportId(String scanId) throws IOException {
        log.debug("Getting report id.");

        String path = String.format(REPORTID_API, URLEncoder.encode(scanId, ENCODING));

        String resultReportId =  (String) httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                String.class,
                HttpStatus.SC_OK,
                "CxSCA Risk ReportId",
                false);
        return StringUtils.strip(resultReportId, "\"");
    }
    
    public List<PolicyEvaluation> getPolicyEvaluation(String reportId) throws IOException {
        log.debug("Getting policy evaluation for the scan report id {}.", reportId);

        String path = String.format(POLICY_MANAGEMENT_EVALUATION_API, URLEncoder.encode(reportId, ENCODING));

        return (List<PolicyEvaluation>) httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                PolicyEvaluation.class,
                HttpStatus.SC_OK,
                "CxSCA policy evaulation",
                true);
    }

    private void determinePolicyViolations(AstScaResults result) {
    
    	result.getPolicyEvaluations().forEach(p-> { 
	    		if(p.getIsViolated()) {
	    			//its enough even one policy is violated
	    			result.setPolicyViolated(true);
	    			if(p.getActions().isBreakBuild())
	    				result.setBreakTheBuild(true);	    			
	    		}
    		}
    	);    	
    }
    
    private void printSummary(AstScaSummaryResults summary, String scanId) {
        if (log.isInfoEnabled()) {
            log.info("----CxSCA risk report summary----");
            log.info("Created on: {}", summary.getCreatedOn());
            log.info("Direct packages: {}", summary.getDirectPackages());
            log.info("High vulnerabilities: {}", summary.getHighVulnerabilityCount());
            log.info("Medium vulnerabilities: {}", summary.getMediumVulnerabilityCount());
            log.info("Low vulnerabilities: {}", summary.getLowVulnerabilityCount());
            log.info("Scan ID: {}", scanId);
            log.info(String.format("Risk score: %.2f", summary.getRiskScore()));
            log.info("Total packages: {}", summary.getTotalPackages());
            log.info("Total outdated packages: {}", summary.getTotalOutdatedPackages());
        }
    }
    
    private void printPolicyEvaluations(List<PolicyEvaluation> policyEvaulations) {
        if (log.isInfoEnabled()) {
            log.info("----CxSCA Policy Evaluation Results----");            
            policyEvaulations.forEach(p-> printPolicyEvaluation(p));
            log.info("---------------------------------------");
        }
    }
    
    private void printPolicyEvaluation(PolicyEvaluation p) {
    	if (log.isInfoEnabled()) {
            log.info("  Policy name: {} | Violated:{} | Policy Description: {}", p.getName(), p.getIsViolated(), p.getDescription());
        
        	p.getRules().forEach(r->
            log.info("    Rule name: {} | Violated: {}", r.getName(), r.getIsViolated())
            );
        
    	}
    }

    private void validate(AstScaConfig config) {
        String error = null;
        if (config == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isEmpty(config.getApiUrl())) {
            error = "%s API URL must be provided.";
        } else if (StringUtils.isEmpty(config.getAccessControlUrl())) {
            error = "%s access control URL must be provided.";
        } else {
            RemoteRepositoryInfo repoInfo = config.getRemoteRepositoryInfo();
            if (repoInfo == null && config.getSourceLocationType() == SourceLocationType.REMOTE_REPOSITORY) {
                error = "%s remote repository info must be provided.";
            } else if (repoInfo != null && StringUtils.isNotEmpty(repoInfo.getBranch())) {
                error = "%s doesn't support specifying custom branches. It currently uses the default branch of a repo.";
            }
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }
}
