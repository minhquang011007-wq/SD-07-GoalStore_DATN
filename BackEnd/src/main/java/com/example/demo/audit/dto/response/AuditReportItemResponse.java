package com.example.demo.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuditReportItemResponse {
    private String key;
    private Long total;
}