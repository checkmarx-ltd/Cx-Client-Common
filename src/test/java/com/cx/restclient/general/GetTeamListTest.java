package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.CxSASTClient;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Slf4j
public class GetTeamListTest extends CommonClientTest {

    //TODO : Fix this tes
    @Ignore("this test fails and needs to be fixed")
    @Test
    public void getTeamListTest() {
        CxScanConfig config = initConfig();
        config.setSastEnabled(true);
        try {
            CxClientDelegator client = new CxClientDelegator(config, log);
            CxSASTClient sastClient =  client.getSastClient();
            sastClient.login("9.0");
            List<Team> teams = sastClient.getTeamList();
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
