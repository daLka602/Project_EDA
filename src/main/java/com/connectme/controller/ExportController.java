package com.connectme.controller;

import com.connectme.model.entities.Contact;
import com.connectme.model.util.PdfUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ExportController {

    private static final Logger logger = Logger.getLogger(ExportController.class.getName());

    /**
     * Exportar contactos para TXT
     */
    public boolean exportToTxt(List<Contact> contacts, File destFile) {
        if (contacts == null || destFile == null) {
            logger.warning("Parâmetros inválidos para exportação TXT");
            return false;
        }

        try {
            PdfUtil.exportContactsToTxt(contacts, destFile);
            logger.info("Contactos exportados para TXT: " + destFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.severe("Erro ao exportar para TXT: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exportar contactos para HTML
     */
    public boolean exportToHtml(List<Contact> contacts, File destFile) {
        if (contacts == null || destFile == null) {
            logger.warning("Parâmetros inválidos para exportação HTML");
            return false;
        }

        try {
            PdfUtil.exportContactsToHtml(contacts, destFile);
            logger.info("Contactos exportados para HTML: " + destFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.severe("Erro ao exportar para HTML: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exportar para múltiplos formatos
     */
    public boolean exportMultiple(List<Contact> contacts, File destDir, String... formats) {
        if (contacts == null || destDir == null) return false;

        if (!destDir.exists() && !destDir.mkdirs()) {
            logger.severe("Não foi possível criar diretório de exportação");
            return false;
        }

        boolean allSuccess = true;

        for (String format : formats) {
            try {
                String timestamp = String.valueOf(System.currentTimeMillis());
                File file = new File(destDir, "contactos_" + timestamp + "." + format.toLowerCase());

                if ("txt".equalsIgnoreCase(format)) {
                    exportToTxt(contacts, file);
                } else if ("html".equalsIgnoreCase(format)) {
                    exportToHtml(contacts, file);
                } else {
                    logger.warning("Formato não suportado: " + format);
                    allSuccess = false;
                }
            } catch (Exception e) {
                logger.severe("Erro ao exportar formato " + format + ": " + e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess;
    }
}