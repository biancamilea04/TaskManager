package org.example.projectjava.Controller;

import org.example.projectjava.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;


@RestController
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @GetMapping("/api/statistics/export")
    public ResponseEntity<?> statistics() {
        try{
            byte[] pdfByte = statisticsService.createPdfReport();
            if( pdfByte == null ) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "statistici_asociatie.pdf");
            return new ResponseEntity<>(pdfByte, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
