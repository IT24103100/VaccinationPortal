package com.sliit.vaccinationportal.service.IT24103048;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PdfReportService {

    @Autowired
    private ReportService reportService;

    public byte[] generateComprehensiveReport() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Define colors
            DeviceRgb primaryColor = new DeviceRgb(30, 81, 219); // #1E51DB
            DeviceRgb headerBg = new DeviceRgb(240, 243, 250);
            DeviceRgb greenColor = new DeviceRgb(34, 197, 94);
            DeviceRgb redColor = new DeviceRgb(239, 68, 68);
            DeviceRgb yellowColor = new DeviceRgb(234, 179, 8);

            // Title
            Paragraph title = new Paragraph("VaxConnect Sri Lanka")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(primaryColor)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Comprehensive System Report")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(date);

            // 1. User Role Distribution
            addSectionHeader(document, "1. User Role Distribution", primaryColor);
            Map<String, Object> userReport = reportService.generateUserReport();
            List<Object[]> userRoleData = (List<Object[]>) userReport.get("userRoleData");

            if (userRoleData != null && !userRoleData.isEmpty()) {
                Table userTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                userTable.setWidth(UnitValue.createPercentValue(100));

                // Header
                userTable.addHeaderCell(createHeaderCell("User Role", headerBg));
                userTable.addHeaderCell(createHeaderCell("Count", headerBg));

                // Data
                for (Object[] row : userRoleData) {
                    userTable.addCell(createDataCell(String.valueOf(row[0])));
                    userTable.addCell(createDataCell(String.valueOf(row[1])));
                }

                document.add(userTable);
            } else {
                document.add(new Paragraph("No user role data available.").setItalic().setMarginBottom(20));
            }

            // 2. Recent Registrations
            addSectionHeader(document, "2. Recent Registrations (Last 30 Days)", primaryColor);
            List<Object[]> recentRegistrations = (List<Object[]>) userReport.get("recentRegistrations");

            if (recentRegistrations != null && !recentRegistrations.isEmpty()) {
                Table regTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                regTable.setWidth(UnitValue.createPercentValue(100));

                // Header
                regTable.addHeaderCell(createHeaderCell("Registration Date", headerBg));
                regTable.addHeaderCell(createHeaderCell("New Users", headerBg));

                // Data
                for (Object[] row : recentRegistrations) {
                    regTable.addCell(createDataCell(String.valueOf(row[0])));
                    regTable.addCell(createDataCell(String.valueOf(row[1])));
                }

                document.add(regTable);
            } else {
                document.add(new Paragraph("No recent registration data available.").setItalic().setMarginBottom(20));
            }

            // 3. Vaccinations by Vaccine Type
            addSectionHeader(document, "3. Vaccinations by Vaccine Type (Last 30 Days)", primaryColor);
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            Map<String, Object> vaccinationReport = reportService.generateVaccinationReport(startDate, endDate);
            List<Object[]> vaccineTypeCounts = (List<Object[]>) vaccinationReport.get("vaccineTypeCounts");

            if (vaccineTypeCounts != null && !vaccineTypeCounts.isEmpty()) {
                Table vaccineTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                vaccineTable.setWidth(UnitValue.createPercentValue(100));

                // Header
                vaccineTable.addHeaderCell(createHeaderCell("Vaccine Name", headerBg));
                vaccineTable.addHeaderCell(createHeaderCell("Doses Administered", headerBg));

                // Data
                for (Object[] row : vaccineTypeCounts) {
                    vaccineTable.addCell(createDataCell(String.valueOf(row[0])));
                    vaccineTable.addCell(createDataCell(String.valueOf(row[1])));
                }

                document.add(vaccineTable);
            } else {
                document.add(new Paragraph("No vaccination data available.").setItalic().setMarginBottom(20));
            }

            // 4. Complete Vaccine Inventory
            addSectionHeader(document, "4. Complete Vaccine Inventory", primaryColor);
            Map<String, Object> inventoryReport = reportService.generateInventoryReport();
            List<Object[]> fullStock = (List<Object[]>) inventoryReport.get("fullStock");

            if (fullStock != null && !fullStock.isEmpty()) {
                // 4.1 Good Stock Vaccines
                document.add(new Paragraph("4.1 Good Stock Vaccines (>100 doses)")
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(10)
                        .setMarginBottom(5));

                Table goodStockTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1}));
                goodStockTable.setWidth(UnitValue.createPercentValue(100));

                // Header
                goodStockTable.addHeaderCell(createHeaderCell("Vaccine Name", headerBg));
                goodStockTable.addHeaderCell(createHeaderCell("Stock Level", headerBg));
                goodStockTable.addHeaderCell(createHeaderCell("Status", headerBg));

                // Data - Only Good Stock
                int goodStockCount = 0;
                for (Object[] row : fullStock) {
                    Long stockLevel = ((Number) row[1]).longValue();
                    if (stockLevel > 100) {
                        goodStockTable.addCell(createDataCell(String.valueOf(row[0])));
                        goodStockTable.addCell(createDataCell(String.valueOf(row[1])));
                        goodStockTable.addCell(createStatusCell("Good Stock", greenColor));
                        goodStockCount++;
                    }
                }

                if (goodStockCount > 0) {
                    document.add(goodStockTable);
                } else {
                    document.add(new Paragraph("No good stock vaccines available.").setItalic().setMarginBottom(10));
                }

                // 4.2 Low Stock Vaccines
                document.add(new Paragraph("4.2 Low Stock Vaccines (≤100 doses)")
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(10)
                        .setMarginBottom(5));

                Table lowStockTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1}));
                lowStockTable.setWidth(UnitValue.createPercentValue(100));

                // Header
                lowStockTable.addHeaderCell(createHeaderCell("Vaccine Name", headerBg));
                lowStockTable.addHeaderCell(createHeaderCell("Stock Level", headerBg));
                lowStockTable.addHeaderCell(createHeaderCell("Status", headerBg));

                // Data - Only Low Stock
                int lowStockCount = 0;
                for (Object[] row : fullStock) {
                    Long stockLevel = ((Number) row[1]).longValue();
                    if (stockLevel <= 100) {
                        lowStockTable.addCell(createDataCell(String.valueOf(row[0])));
                        lowStockTable.addCell(createDataCell(String.valueOf(row[1])));
                        
                        if (stockLevel <= 50) {
                            lowStockTable.addCell(createStatusCell("Critical", redColor));
                        } else {
                            lowStockTable.addCell(createStatusCell("Low Stock", yellowColor));
                        }
                        lowStockCount++;
                    }
                }

                if (lowStockCount > 0) {
                    document.add(lowStockTable);
                } else {
                    document.add(new Paragraph("No low stock vaccines.").setItalic().setMarginBottom(10));
                }
            } else {
                document.add(new Paragraph("No inventory data available.").setItalic().setMarginBottom(20));
            }

            // Footer
            document.add(new Paragraph("\n\n--- End of Report ---")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    private void addSectionHeader(Document document, String headerText, DeviceRgb color) {
        Paragraph header = new Paragraph(headerText)
                .setFontSize(14)
                .setBold()
                .setFontColor(color)
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(header);
    }

    private Cell createHeaderCell(String text, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(bgColor)
                .setPadding(8)
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell createDataCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(6)
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell createStatusCell(String text, DeviceRgb color) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontColor(color))
                .setPadding(6)
                .setTextAlignment(TextAlignment.LEFT);
    }
}

