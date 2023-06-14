package com.ilyak.service;

import com.google.common.base.Joiner;
import com.ilyak.entity.jpa.Files;
import com.ilyak.repository.FilesRepository;
import com.ilyak.repository.TransactionalRepository;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.thymeleaf.util.ClassLoaderUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Singleton
public class DocumentService {

    @Inject
    TransactionalRepository transactionalRepository;

    @Inject
    FilesService filesService;

    @Inject
    FilesRepository filesRepository;

    @SneakyThrows
    public Files genDocument(Map<String, Object> anchors){
        String path =
                filesService.getDirPattern() +
                filesService.getDocuments() +
                filesService.uniqueName(
                        Joiner.on(";").withKeyValueSeparator(":").join(anchors),
                        ".pdf"
                );
        replacer("conclusion_template.docx", anchors).saveToFile(path, FileFormat.PDF);
        return new Files(
                null,
                path,
                new File(path).length(),
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    @SneakyThrows
    public ByteArrayOutputStream genReport(Map<String, Object> anchors){
//        ?
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        replacer("report_template.docx", anchors).saveToStream(os, FileFormat.PDF);
        return os;
    }

    private Document replacer(
            String template,
            Map<String, Object> anchors
    ){
        Document document = new Document(ClassLoaderUtils.findResourceAsStream(template));
        for(Section section : (Iterable<Section>)document.getSections()) {
            for (Paragraph para : (Iterable<Paragraph>) section.getParagraphs()) {
                for (Map.Entry<String, Object> entry : anchors.entrySet()) {
                    para.replace(
                            "${" + entry.getKey() + "}",
                            entry.getValue() == null ? "N/A" : entry.getValue().toString(),
                            false,
                            false
                    );
                }
            }
        }
        return document;
    }

    static void replaceTextInDocumentBody(Map<String, String> map, Document document){

    }
}
