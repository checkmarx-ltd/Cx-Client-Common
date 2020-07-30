package com.cx.configprovider.dto;

import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigLocation {
    private RemoteRepositoryInfo repoInfo;
}
