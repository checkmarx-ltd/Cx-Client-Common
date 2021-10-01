package com.cx.restclient.dto;

import java.io.Serializable;

import com.cx.restclient.exception.CxClientException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Results implements Serializable {
    private CxClientException exception;
}
