package com.connectme.model.util;

import com.connectme.model.entities.Contact;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

// iText 7
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;

public class ExportUtil {

    public static void exportContactsToPdf(List<Contact> contacts, File destFile) throws IOException {
        exportContactsToPdf(contacts, destFile, "Lista de Contactos - ConnectMe");
    }

    public static void exportContactsToPdf(List<Contact> contacts, File destFile, String title) throws IOException {
        if (contacts == null) throw new IllegalArgumentException("contacts == null");
        if (destFile == null) throw new IllegalArgumentException("destFile == null");

        File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PdfWriter writer = new PdfWriter(destFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Título
            document.add(new Paragraph(title)
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            // Informações do documento
            document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(dtf))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            document.add(new Paragraph("Total de contactos: " + contacts.size())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Tabela com larguras proporcionais
            float[] columnWidths = {3, 2, 3, 4}; // Proporções
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Cabeçalho com estilo
            String[] headers = {"Nome", "Telefone", "Email", "Morada"};
            for (String header : headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setPadding(5));
            }

            // Dados
            for (Contact c : contacts) {
                table.addCell(createCell(nullSafe(c.getName())));
                table.addCell(createCell(nullSafe(c.getPhone())));
                table.addCell(createCell(nullSafe(c.getEmail())));
                table.addCell(createCell(nullSafe(c.getAddress())));
            }

            document.add(table);

            // Rodapé
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Documento gerado automaticamente por ConnectMe")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());

        }
    }

    private static Cell createCell(String content) {
        return new Cell()
                .add(new Paragraph(content.isEmpty() ? "-" : content))
                .setPadding(5);
    }

    public static void exportContactsToTxt(List<Contact> contacts, File destFile) throws IOException {
        exportContactsToTxt(contacts, destFile, "ConnectMe - Lista de Contactos");
    }

    public static void exportContactsToTxt(List<Contact> contacts, File destFile, String title) throws IOException {
        if (contacts == null) throw new IllegalArgumentException("contacts == null");
        if (destFile == null) throw new IllegalArgumentException("destFile == null");

        File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destFile))) {
            // Cabeçalho
            writer.write("=".repeat(80));
            writer.newLine();
            writer.write(centerText(title, 80));
            writer.newLine();
            writer.write("=".repeat(80));
            writer.newLine();
            
            writer.write("Gerado em: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            writer.newLine();
            writer.write("Total de contactos: " + contacts.size());
            writer.newLine();
            writer.write("-".repeat(80));
            writer.newLine();
            
            // Cabeçalho da tabela
            writer.write(String.format("%-25s %-15s %-25s %-30s", 
                "NOME", "TELEFONE", "EMAIL", "MORADA"));
            writer.newLine();
            writer.write("-".repeat(80));
            writer.newLine();

            // Dados
            for (Contact c : contacts) {
                writer.write(String.format("%-25s %-15s %-25s %-30s",
                    truncate(nullSafe(c.getName()), 24),
                    truncate(nullSafe(c.getPhone()), 14),
                    truncate(nullSafe(c.getEmail()), 24),
                    truncate(nullSafe(c.getAddress()), 29)
                ));
                writer.newLine();
            }
            
            writer.write("-".repeat(80));
            writer.newLine();
            writer.write("Fim da lista");
            writer.newLine();
        }
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    /**
     * NOVO: Exportar para HTML
     */
    public static void exportContactsToHtml(List<Contact> contacts, File destFile) throws IOException {
        if (contacts == null || destFile == null) throw new IllegalArgumentException();

        File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destFile))) {
            writer.write("<!DOCTYPE html>");
            writer.write("<html><head><title>Contactos - ConnectMe</title>");
            writer.write("<style>");
            writer.write("body { font-family: Arial, sans-serif; margin: 20px; }");
            writer.write("h1 { color: #333; text-align: center; }");
            writer.write("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
            writer.write("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            writer.write("th { background-color: #f2f2f2; font-weight: bold; }");
            writer.write("tr:nth-child(even) { background-color: #f9f9f9; }");
            writer.write(".info { text-align: center; color: #666; margin: 10px 0; }");
            writer.write("</style>");
            writer.write("</head><body>");

            writer.write("<h1>📞 Lista de Contactos - ConnectMe</h1>");
            writer.write("<div class='info'>");
            writer.write("Gerado em: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " | ");
            writer.write("Total: " + contacts.size() + " contactos");
            writer.write("</div>");

            writer.write("<table>");
            writer.write("<tr><th>Nome</th><th>Telefone</th><th>Email</th><th>Morada</th></tr>");

            for (Contact c : contacts) {
                writer.write("<tr>");
                writer.write("<td>" + escapeHtml(nullSafe(c.getName())) + "</td>");
                writer.write("<td>" + escapeHtml(nullSafe(c.getPhone())) + "</td>");
                writer.write("<td>" + escapeHtml(nullSafe(c.getEmail())) + "</td>");
                writer.write("<td>" + escapeHtml(nullSafe(c.getAddress())) + "</td>");
                writer.write("</tr>");
            }

            writer.write("</table>");
            writer.write("<div class='info'>Documento gerado automaticamente por ConnectMe</div>");
            writer.write("</body></html>");
        }
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}