package com.stavre.cfrapiadapter.utils;

import java.util.concurrent.Callable;

public class ScraperUtils {

    public String getOrBlank(Callable<String> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            return "";
        }
    }
}
