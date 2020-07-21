package com.cx.restclient.ast.dto.common;

import com.cx.restclient.dto.Results;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AstSastResults implements Serializable, Results {
    private String scanId;
    private AstSastSummaryResults summary;
}
