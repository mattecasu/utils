package utils.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import utils.model.TextPortion;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class Shingler {

    public List<TextPortion> getShingles(String text, int maxShingleSize) {

        List<TextPortion> ngrams = newArrayList();

        ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(new SimpleAnalyzer(), maxShingleSize);

        TokenStream stream = shingleAnalyzer.tokenStream(EMPTY, new StringReader(text));

        CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);

        try {

            stream.reset();

            while (stream.incrementToken()) {

                String shingle = termAttribute.toString()
                        .replace("_", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

                ngrams.add(
                        new TextPortion()
                                .setText(shingle)
                                .setStart(Long.valueOf(String.valueOf(offsetAttribute.startOffset())))
                                .setEnd(Long.valueOf(String.valueOf(offsetAttribute.endOffset())))
                );
            }

            stream.end();
            stream.close();

        } catch (IOException e) {
            log.error("Unable to get token.", e);
        }

        shingleAnalyzer.close();

        return ngrams;

    }

}
