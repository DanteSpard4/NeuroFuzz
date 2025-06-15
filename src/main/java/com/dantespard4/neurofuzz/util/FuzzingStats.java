package com.dantespard4.neurofuzz.util;

public class FuzzingStats {
    private int count2xx = 0;
    private int count3xx = 0;
    private int count4xx = 0;
    private int count5xx = 0;
    private int countTimeouts = 0;
    private String outputFileName;

    public int getCount2xx() {
        return count2xx;
    }

    public int getCount3xx() {
        return count3xx;
    }

    public int getCount4xx() {
        return count4xx;
    }

    public int getCount5xx() {
        return count5xx;
    }

    public int getCountTimeouts() {
        return countTimeouts;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public int getTotalCount() {
        return count2xx + count3xx + count4xx + count5xx + countTimeouts;
    }

    public void recordStatus(int statusCode) {
        switch (statusCode / 100) {
            case 2 -> count2xx++;
            case 3 -> count3xx++;
            case 4 -> count4xx++;
            case 5 -> count5xx++;
            case 6 -> countTimeouts++;
        }
    }
}