package com.ilyak.service;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import io.micronaut.core.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.thymeleaf.util.ClassLoaderUtils;

import java.io.IOException;
import java.util.Map;



class DocumentServiceTest {


    @Test
    public void genTest() throws IOException {
        
        Map<String, String> anchors = CollectionUtils.mapOf(
                        "conclusion_day", "02",
                        "conclusion_mouth", "04",
                        "conclusion_year", "23"
        );

        Document doc = new Document(ClassLoaderUtils.findResourceAsStream("conclusion_template.docx"));
        replaceTextinDocumentBody(anchors, doc);
        doc.saveToFile("/asd.pdf", FileFormat.PDF);

//        PDDocument template = PDDocument.load(ClassLoaderUtils.findResourceAsStream("conclusion_template.pdf"));
//        PDFTextStripper stripper = new PDFTextStripper();
//        String txt= stripper.getText(template);
//
//        anchors.forEach((key, value )-> {
////            txt.replace("${" + key + "}", value);
//            txt.rep
//        });


//        System.out.println(txt);
    }

    static void replaceTextinDocumentBody(Map<String, String> map, Document document){
        for(Section section : (Iterable<Section>)document.getSections()) {
            for (Paragraph para : (Iterable<Paragraph>) section.getParagraphs()) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    para.replace("${" + entry.getKey() + "}", entry.getValue(), false, true);
                }
            }
        }
    }
}