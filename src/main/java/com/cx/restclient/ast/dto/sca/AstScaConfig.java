package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.ast.dto.common.ASTConfig;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.sca.dto.RemoteRepositoryInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AstScaConfig extends ASTConfig implements Serializable {
    private String accessControlUrl;
    private String username;
    private String password;
    private String tenant;
    private String webAppUrl;
    private boolean includeSources;
    private String FingerprintsIncludePattern;
    private String ManifestsIncludePattern;
    private String FingerprintFilePath;
    private String zipFilePath;
}
