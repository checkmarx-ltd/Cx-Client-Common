package com.cx.restclient.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Results {
    private Exception createException;
    private Exception waitException;
}
