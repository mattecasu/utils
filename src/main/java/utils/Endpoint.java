package utils;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import utils.model.TikaDocument;
import utils.service.TikaParser;

import java.io.ByteArrayInputStream;

@RestController
public class Endpoint {


    @RequestMapping(value = "/tika", method = RequestMethod.POST)
    public @ResponseBody
    TikaDocument processWithTika(@RequestBody byte[] file) {

        return TikaParser.process(new ByteArrayInputStream(file), true);
    }

}