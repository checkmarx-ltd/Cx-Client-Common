package com.cxone.restclient.cxOneArm.utils;

import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.cxArm.dto.Violation;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.cx.restclient.common.CxPARAM.CX_ARM_VIOLATION;
import static com.cx.restclient.common.ShragaUtils.formatDate;
import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;

/**
 * Created by Galn on 7/30/2018.
 */
public abstract class CxOneARMUtils {
    public static List<Policy> getProjectViolatedPolicies(CxHttpClient httpClient, String cxARMUrl, long projectId, String provider) throws IOException, CxClientException {
        String relativePath = CX_ARM_VIOLATION.replace("{projectId}", Long.toString(projectId)).replace("{provider}", provider);
        return (List<Policy>) httpClient.getRequest(cxARMUrl, relativePath, CONTENT_TYPE_APPLICATION_JSON_V1, null, Policy.class, 200, "CxARM violations", true);
    }

    public static List<Policy> getPolicyList(Policy policy) {
        List<Policy> policies = new ArrayList<Policy>();
        Map<String, List<Violation>> rules = resolveRules(policy.getViolations());
        Iterator it = rules.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<Violation> violations = (List<Violation>) pair.getValue();
            String firstDate = resolveFirstDate(violations);
            policies.add(new Policy(policy.getPolicyId(), policy.getPolicyName(), pair.getKey().toString(), violations, firstDate));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return policies;
    }

    private static Map<String, List<Violation>> resolveRules(List<Violation> violations) {
        Map<String, List<Violation>> rules = violations.stream().collect(
                Collectors.toMap(Violation::getRuleName, e -> {
                            List<Violation> ary = new ArrayList<Violation>();
                            ary.add(e);
                            return ary;
                        },
                        (left, right) -> {
                            left.addAll(right);
                            return left;
                        }));

        return rules;
    }

    private static String resolveFirstDate(List<Violation> violations) {
        Date firstDetectionDate = new Date(violations.get(0).getFirstDetectionDateByArm());
        for (Violation violation : violations) {
            Date date = new Date(violation.getFirstDetectionDateByArm());
            if (date.before(firstDetectionDate)) {
                firstDetectionDate = date;
            }
        }
        String firstDate = formatDate(firstDetectionDate.toString(), "E MMM dd hh:mm:ss Z yyyy", "dd/MM/yy");
        return firstDate;
    }

    public static String getPoliciesNames(List<Policy> policies) {
        String str ="";
        for (Policy policy : policies){
            str += ", " + policy.getPolicyName();
        }
        str = str.substring(1, str.length());
        return str;
    }
}
