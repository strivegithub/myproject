package com.d4games.dzix.util;

import java.nio.charset.Charset;

import com.strategicgains.restexpress.Request;

public enum StringUtils {
    INSTANCE;

    public static String removeSpecialCharacters(String src) {
        String result = src.replaceAll("\\W", "_");
        return result;
    };

    public static String getBodyString(Request request) {
        request.getBody().resetReaderIndex();
        return new String(request.getBody().toString(Charset.defaultCharset()));
    };

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.equalsIgnoreCase("null");
    };

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    };
};
