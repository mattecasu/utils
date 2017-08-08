package utils;


import org.apache.tika.sax.BasicContentHandlerFactory;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import utils.model.TextPortion;
import utils.model.TikaDocument;
import utils.service.Shingler;
import utils.service.TikaParser;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class Endpoint {


    @RequestMapping(value = "/tika", method = POST)
    public @ResponseBody
    TikaDocument processWithTika(@RequestBody byte[] file, @RequestParam(defaultValue = "html") String handlerType) {

        BasicContentHandlerFactory.HANDLER_TYPE tikaHandler = BasicContentHandlerFactory.HANDLER_TYPE.valueOf(handlerType.toUpperCase());

        TikaDocument tikaDocument = TikaParser.process(new ByteArrayInputStream(file), false, tikaHandler);

        if (Jsoup.parse(tikaDocument.getContent()).text().trim().isEmpty()) {
            return TikaParser.process(new ByteArrayInputStream(file), true, tikaHandler);
        }

        return tikaDocument;

    }

    @RequestMapping(value = "/shingles", method = POST)
    public @ResponseBody
    List<TextPortion> getShingles(@RequestBody String text, @RequestParam(defaultValue = "3") String shingleSize) {

        return Shingler.getShingles(text, Integer.valueOf(shingleSize));

    }

}