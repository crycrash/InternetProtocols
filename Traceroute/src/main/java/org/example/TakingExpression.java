package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TakingExpression {
    public String takingCountry(String input) {
        Pattern countryPattern = Pattern.compile("country:\\s+(\\w{2})");
        Matcher countryMatcher = countryPattern.matcher(input);
        Pattern countryPattern1 = Pattern.compile("Country:\\s+(\\w{2})");
        Matcher countryMatcher1 = countryPattern1.matcher(input);
        String country;
        if (countryMatcher.find()) {
            country = countryMatcher.group(1);
            return country;
        }
        if (countryMatcher1.find()) {
            country = countryMatcher1.group(1);
            return country;
        }
        return null;
    }
    public String takingOrigin(String input){
        Pattern originPattern = Pattern.compile("origin:\\s+(AS\\d+)");
        Matcher originMatcher = originPattern.matcher(input);
        Pattern originPattern1 = Pattern.compile("OriginAS:\\s+(AS\\d+)");
        Matcher originMatcher1 = originPattern1.matcher(input);
        String origin;
        if(originMatcher.find()) {
            origin = originMatcher.group(1);
            return origin;
        }
        if(originMatcher1.find()) {
            origin = originMatcher1.group(1);
            return origin;
        }
        return null;
    }
    public String local(String ip){

        String[] ipParts = ip.split("\\.");

        if (Integer.parseInt(ipParts[0]) == 10) {
            return "local";
        }
        if (Integer.parseInt(ipParts[0]) == 100 && Integer.parseInt(ipParts[1]) >= 64 && Integer.parseInt(ipParts[1]) <= 127) {
            return "local";
        }
        if (Integer.parseInt(ipParts[0]) == 172 && Integer.parseInt(ipParts[1]) >= 16 && Integer.parseInt(ipParts[1]) <= 31) {
            return "local";
        }
        if (Integer.parseInt(ipParts[0]) == 192 && Integer.parseInt(ipParts[1]) == 168) {
            return "local";
        }
        return "no";
    }

    public String takingWhois(String input){
        Pattern originPattern = Pattern.compile("whois:\\s+(\\w+.\\w+.{4})");
        Matcher originMatcher = originPattern.matcher(input);
        String origin = null;
        if(originMatcher.find()) {
            origin = originMatcher.group(1);
//System.out.println("Origin: " + origin);
        }
        return origin;
    }

    public String takingName(String input){
        Pattern netnamePattern = Pattern.compile("netname:\\s+([A-Z0-9-]+)");
        Matcher netnameMatcher = netnamePattern.matcher(input);
        Pattern netnamePattern1 = Pattern.compile("NetName:\\s+([A-Z0-9-]+)");
        Matcher netnameMatcher1 = netnamePattern1.matcher(input);
        String netName;
        if(netnameMatcher.find()) {
            netName = netnameMatcher.group(1);
            return netName;
        }
        if(netnameMatcher1.find()) {
            netName = netnameMatcher1.group(1);
            return netName;
        }
        return null;
    }
    public String takingEU(String input){
        Pattern EuPattern = Pattern.compile("phone:\\s+(\\+\\d+)");
        Matcher EuMatcher = EuPattern.matcher(input);

        String origin;
        if(EuMatcher.find()) {
            origin = EuMatcher.group(1);
            return origin;
        }

        return null;
    }
}