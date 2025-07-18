package com.example.myapp.controller;

import com.example.myapp.Models.Weight;
import com.example.myapp.Repository.WeightRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

@Controller
public class PdfController {

    @Autowired
    private WeightRepository weightRepository;

    @GetMapping("/weight/pdf")
    public void downloadWeightsPdf(HttpServletResponse response, Principal principal) throws Exception {
        String username = principal.getName();
        // Fetch all weights for this user, ordered by date descending (or ascending if you prefer)
        List<Weight> weights = weightRepository.findByUsernameOrderByAddedTimeDesc(username, Pageable.unpaged()).getContent();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=weights.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Weight List for " + username);
            contentStream.endText();

            // Content font
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            int yPosition = 720;

            for (Weight weight : weights) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    yPosition = 750;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                String line = String.format("Date: %s | Weight: %.2f kg",
                    weight.getAddedTime().toString(), weight.getWeight());
                contentStream.showText(line);
                contentStream.endText();
                yPosition -= 20;
            }

            contentStream.close();
            document.save(response.getOutputStream());
        }
    }
    }

