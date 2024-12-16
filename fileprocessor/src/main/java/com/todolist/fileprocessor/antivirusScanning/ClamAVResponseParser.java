package com.todolist.fileprocessor.antivirusScanning;

public class ClamAVResponseParser {

    public static String extractVirusName(String clamAVResponse) {
        if (clamAVResponse == null || clamAVResponse.isEmpty()) {
            return null;
        }

        String cleanResponse = clamAVResponse.replaceFirst("^stream:\\s*", "").trim();

        if (cleanResponse.contains("FOUND")) {
            return cleanResponse.split("FOUND")[0].trim();
        }

        return null;
    }
}