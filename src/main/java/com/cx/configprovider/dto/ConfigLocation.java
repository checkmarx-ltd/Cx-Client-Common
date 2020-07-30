package com.cx.configprovider.dto;

import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigLocation {
    private SourceProviderType sourceProviderType;
    private String path;
    private RemoteRepositoryInfo repoInfo;
}
