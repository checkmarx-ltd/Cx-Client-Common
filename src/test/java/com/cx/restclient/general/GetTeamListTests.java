package com.cx.restclient.general;

import com.cx.restclient.CxClientWrapper;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Slf4j
public class GetTeamListTests extends CommonClientTest {
    @Test
    public void getTeamListTest() {
        CxScanConfig config = initConfig();
        try {
            CxClientWrapper client = new CxClientWrapper(config, log);
            client.login("9.0");
            List<Team> teams = client.getTeamList();
            Assert.assertNotNull(teams);
            Assert.assertFalse(teams.isEmpty());
        } catch (Exception e) {
            failOnException(e);
        }
    }

    private CxScanConfig initConfig() {
        CxScanConfig config = new CxScanConfig();
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setCxOrigin("common");
        return config;
    }
}
