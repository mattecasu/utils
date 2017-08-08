package utils.service;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import utils.model.TikaDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.UnknownFormatConversionException;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.tika.parser.RecursiveParserWrapper.EMBEDDED_RESOURCE_PATH;
import static org.apache.tika.parser.RecursiveParserWrapper.TIKA_CONTENT;
import static org.apache.tika.parser.pdf.PDFParserConfig.OCR_STRATEGY.NO_OCR;
import static org.apache.tika.parser.pdf.PDFParserConfig.OCR_STRATEGY.OCR_ONLY;


@Slf4j
public class TikaParser {

    private final static String TIKA_CONTENT_FIELD = TIKA_CONTENT.getName();
    @Getter
    private static final String PARSING_ERROR_FIELD = "parsingError";

    public static TikaDocument process(InputStream stream, boolean withOcr, BasicContentHandlerFactory.HANDLER_TYPE handlerType) {

        List<Metadata> metadatas = extractWithEmbedded(stream, withOcr, handlerType);

        Metadata masterDocMetadata = metadatas.remove(0);

        String masterContent = masterDocMetadata.get(TIKA_CONTENT_FIELD) != null ? masterDocMetadata.get(TIKA_CONTENT_FIELD) : EMPTY;
        masterDocMetadata.remove(TIKA_CONTENT_FIELD);
        TikaDocument masterDoc = new TikaDocument(masterContent, masterDocMetadata);

        metadatas = metadatas.stream()
                .filter(m -> m.get("resourceName") != null)
                .filter(m -> m.get(EMBEDDED_RESOURCE_PATH.getName()) != null)
                .filter(m -> m.get(TIKA_CONTENT_FIELD) != null)
                .collect(toList());

        metadatas.sort(
                Comparator.comparingInt(a -> Splitter.on("/").splitToList(a.get(EMBEDDED_RESOURCE_PATH.getName())).size())
        );

        for (Metadata metadata : metadatas) {
            List<String> path = Splitter.on("/")
                    .splitToList(metadata.get(EMBEDDED_RESOURCE_PATH.getName()))
                    .stream()
                    .filter(element -> !element.isEmpty())
                    .collect(toList());

            String content = metadata.get(TIKA_CONTENT_FIELD) != null ? metadata.get(TIKA_CONTENT_FIELD) : EMPTY;
            metadata.remove(TIKA_CONTENT_FIELD);

            TikaDocument newDoc = new TikaDocument(content, metadata);
            newDoc.getTikaMetadata().set("size", String.valueOf(content.getBytes().length));
            newDoc.setMetadata(newDoc.getTikaMetadataAsMap());
            // direct child
            if (path.size() == 1) {
                masterDoc.getEmbeddedDocuments().add(newDoc);
            }
            // assign relationship
            else {
                setRelationship(newDoc, masterDoc, path, 2);
            }

        }

        return masterDoc
                .setMetadata(masterDoc.getTikaMetadataAsMap());
    }


    private static void setRelationship(TikaDocument descendant, TikaDocument ancestor, List<String> path, int level) {

        String currentVisitedResourceName = path.get(level - 2);

        TikaDocument currentAncestorDoc = ancestor.getEmbeddedDocuments().stream()
                .filter(d -> d.getTikaMetadata().get("resourceName").equals(currentVisitedResourceName))
                .findFirst()
                .get();

        if (path.size() == level) {
            currentAncestorDoc.getEmbeddedDocuments().add(descendant);
            return;
        } else {
            setRelationship(descendant, currentAncestorDoc, path, level + 1);
        }
    }


    private static List<Metadata> extractWithEmbedded(InputStream stream, boolean withOcr, BasicContentHandlerFactory.HANDLER_TYPE handlerType) {

        TikaInputStream tikaStream = TikaInputStream.get(stream);

        BodyContentHandler handler = new BodyContentHandler(-1);
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        RecursiveParserWrapper wrapper = new RecursiveParserWrapper(
                parser,
                new BasicContentHandlerFactory(handlerType, -1)
        );

        try {
            metadata.set("size", String.valueOf(tikaStream.getLength()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info("Tika Detector: " + parser.getDetector().toString());

        try {

            TesseractOCRConfig ocrConfig = new TesseractOCRConfig();

            ParseContext parseContext = new ParseContext();

            parseContext.set(TesseractOCRConfig.class, ocrConfig);

            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setOcrStrategy(withOcr ? OCR_ONLY : NO_OCR);

            parseContext.set(PDFParserConfig.class, pdfConfig);

            parseContext.set(Parser.class, parser);

            wrapper.parse(tikaStream, handler, metadata, parseContext);

        } catch (IOException | SAXException | TikaException | UnknownFormatConversionException e) {

            log.error(e.getMessage());
            metadata.set(PARSING_ERROR_FIELD, e.getMessage());

            return newArrayList(metadata);
        }

        return wrapper.getMetadata();
    }

}