package com.arun.pdfreport.controller;

import com.arun.pdfreport.model.Fee;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    @GetMapping("/generatePdf")
    public ResponseEntity generatePdfReport() throws JRException, IOException {
        ClassPathResource reportResource = new ClassPathResource("jasper2/Main_report.jrxml");
        InputStream reportStream = reportResource.getInputStream();
        // Compile jrxml to Jasper
        JasperReport report = JasperCompileManager.compileReport(reportStream);

        ClassPathResource subReportResource = new ClassPathResource("jasper2/Sub_report.jrxml");
        InputStream subReportStream = subReportResource.getInputStream();
        // Compile jrxml to Jasper
        JasperReport subReport = JasperCompileManager.compileReport(subReportStream);
        // Prepare the parameters for the main report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("UserNo", "u123"); // Example portfolio number
        
        parameters.put("subReport", subReport);
        parameters.put("subReportParameter", getSubReportParameter());

        // Fill the main report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters,
                new JREmptyDataSource());

        // Export the filled report to a PDF file
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        // Set the HTTP headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Return the PDF as a byte array
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);

    }

    private static JRBeanCollectionDataSource getSubDataSource() {
        List<Fee> dataList = new ArrayList<>();
        dataList.add(new Fee("Apple", "100.00", "90.00"));
        dataList.add(new Fee("Orange", "200.00", "180.00"));
        dataList.add(new Fee("Banana", "50.00", "45.00"));
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        return dataSource;
    }

    private static Map getSubReportParameter() {
        Map<String, Object> subParameter = new HashMap<>();
        subParameter.put("subReportDataSet", getSubDataSource());
        subParameter.put("SubUserNo", "u123-01");
        return subParameter;
    }
}
