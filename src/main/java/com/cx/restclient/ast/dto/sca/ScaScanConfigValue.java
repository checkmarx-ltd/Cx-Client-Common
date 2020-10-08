package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.ast.dto.common.ScanConfigValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * AST-SCA-specific config parameters. Should be expanded with other supported properties.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScaScanConfigValue implements ScanConfigValue {

    private Map<String, String> additionalMetadata;
}
