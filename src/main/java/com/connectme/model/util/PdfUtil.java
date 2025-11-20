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

public class PdfUtil {

    public static void exportContactsToPdf(List<Contact> contacts, File destFile) throws IOException {
        if (contacts == null) throw new IllegalArgumentException("contacts == null");
        if (destFile == null) throw new IllegalArgumentException("destFile == null");

        File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PdfWriter writer = new PdfWriter(destFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            document.add(new Paragraph("Lista de Contactos - ConnectMe")
                    .setBold()
                    .setFontSize(14));

            document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(dtf))
                    .setFontSize(10));

            document.add(new Paragraph("\n"));

            // Tabela simples compatível com iText 7.2.5
            float[] columnWidths = {200F, 120F, 200F, 240F};
            Table table = new Table(columnWidths);

            // HEADER
            table.addHeaderCell(new Cell().add(new Paragraph("Nome").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Telefone").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Morada").setBold()));

            // BODY
            for (Contact c : contacts) {
                table.addCell(new Cell().add(new Paragraph(nullSafe(c.getName()))));
                table.addCell(new Cell().add(new Paragraph(nullSafe(c.getPhone()))));
                table.addCell(new Cell().add(new Paragraph(nullSafe(c.getEmail()))));
                table.addCell(new Cell().add(new Paragraph(nullSafe(c.getAddress()))));
            }

            document.add(table);
        }
    }

    public static void exportContactsToTxt(List<Contact> contacts, File destFile) throws IOException {
        if (contacts == null) throw new IllegalArgumentException("contacts == null");
        if (destFile == null) throw new IllegalArgumentException("destFile == null");

        File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destFile))) {
            writer.write("ConnectMe - Lista de Contactos");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();
            writer.write(String.format("%-40s\t%-15s\t%-30s\t%-40s", "Nome", "Telefone", "Email", "Morada"));
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();

            for (Contact c : contacts) {
                writer.write(String.format("%-40s\t%-15s\t%-30s\t%-40s",
                        nullSafe(c.getName()),
                        nullSafe(c.getPhone()),
                        nullSafe(c.getEmail()),
                        nullSafe(c.getAddress())
                ));
                writer.newLine();
            }
        }
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
