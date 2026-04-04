package com.example.demo.payment.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class VnpayUtil {

    private VnpayUtil() {
    }

    public static String hmacSHA512(String secretKey, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo chữ ký VNPAY", e);
        }
    }

    public static String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        fieldNames.sort(String::compareTo);

        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isBlank()) {
                continue;
            }

            if (!first) {
                hashData.append('&');
            }
            hashData.append(fieldName)
                    .append('=')
                    .append(urlEncode(fieldValue));
            first = false;
        }
        return hashData.toString();
    }

    public static String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        fieldNames.sort(String::compareTo);

        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isBlank()) {
                continue;
            }

            if (!first) {
                query.append('&');
            }
            query.append(urlEncode(fieldName))
                    .append('=')
                    .append(urlEncode(fieldValue));
            first = false;
        }
        return query.toString();
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    public static String sanitizeOrderInfo(String value) {
        if (value == null || value.isBlank()) {
            return "Thanh toan don hang";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .replaceAll("[^a-zA-Z0-9 #:_-]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.length() > 255) {
            return normalized.substring(0, 255);
        }
        return normalized;
    }

    public static String normalizeIpAddress(String value) {
        if (value == null || value.isBlank()) {
            return "127.0.0.1";
        }

        String ip = value.split(",")[0].trim();
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring(7);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip.toLowerCase(Locale.ROOT);
    }
}
