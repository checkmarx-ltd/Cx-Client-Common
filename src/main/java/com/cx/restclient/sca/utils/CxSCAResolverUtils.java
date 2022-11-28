package com.cx.restclient.sca.utils;

import java.util.*;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;


public class CxSCAResolverUtils {

    public static Map<String, String> parseArguments(String text) throws ParseException {
        // Split the provided arguments text on spaces.
        // NOTE: We loose multiple spaces information but that should not be of an issue.
        Map<String, String> parsed = new HashMap<>();

        text = text.trim();
        if (StringUtils.isEmpty(text)) {
            return parsed;
        }

        String[] arguments = text.split("\\s+");

        int parsePos = 0;    // Keep track of our position in the text for error reporting.
        for (int i = 0; i < arguments.length;) {
            String arg = arguments[i];
            // The argument value may have spaces. This buffer is used to reconstruct the original value.
            List<String> valueBuffer = new ArrayList<>();

            // Check that we caught a short (`-x`) or long (`--xxxx`) argument.
            if (!(arg.startsWith("-") && arg.length() == 2) && !arg.startsWith("--")) {
                throw new ParseException("Could not parse provided arguments: " + text, parsePos);
            }

            // Do we have a long argument in the form `--xxxx=XXXXX`?
            if (arg.contains("=")) {
                String[] parts = arg.split("=", 2);
                arg = parts[0];
                valueBuffer.add(parts[1]);
            }

            parsePos += arg.length() + 1;
            // Complete the value until we reach a new argument.
            for (i++; i < arguments.length; i++) {
                if ((arguments[i].startsWith("-") && arguments[i].length() == 2) || arguments[i].startsWith("--")) {
                    break;
                }
                valueBuffer.add(arguments[i]);
            }

            // Reconstruct and unescape the value.
            String value = null;
            if (!valueBuffer.isEmpty()) {
                value = String.join(" ", valueBuffer);
                parsePos += value.length() + 1;

                // Remove potential enclosing quotes.
                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                value = StringEscapeUtils.unescapeXSI(value);
            }

            parsed.put(arg, value);
        }

        return parsed;
    }
}
