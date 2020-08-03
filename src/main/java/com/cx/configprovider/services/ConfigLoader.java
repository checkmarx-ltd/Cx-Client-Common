package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;

interface ConfigLoader {
    RawConfigAsCode getConfigAsCode(ConfigLocation configLocation);
}
