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


import java.io.File;
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
    public Files genDocument(Map<String, String> anchors){
        Document doc = new Document(ClassLoaderUtils.findResourceAsStream("conclusion_template.docx"));
        replaceTextInDocumentBody(anchors, doc);
        String path =
                filesService.getDirPattern() +
                filesService.getAvatars() +
                filesService.uniqueName(
                        Joiner.on(";").withKeyValueSeparator(":").join(anchors),
                        ".pdf"
                );

        doc.saveToFile(path, FileFormat.PDF);
        return new Files(
                null,
                path,
                new File(path).length(),
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    static void replaceTextInDocumentBody(Map<String, String> map, Document document){
        for(Section section : (Iterable<Section>)document.getSections()) {
            for (Paragraph para : (Iterable<Paragraph>) section.getParagraphs()) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    para.replace("${" + entry.getKey() + "}", entry.getValue(), false, true);
                }
            }
        }
    }
}
