package utils.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tika.metadata.Metadata;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;

@ToString(exclude = "content")
@Accessors(chain = true)
public class TikaDocument {

    @Getter
    @Setter
    private String content;

    @Getter
    private final Metadata tikaMetadata;

    @Getter
    @Setter
    private List<TikaDocument> embeddedDocuments;

    @Getter
    @Setter
    private byte[] originalDocAsBytes;


    public TikaDocument(String content, Metadata tikaMetadata) {
        this.content = content;
        this.tikaMetadata = tikaMetadata;
        this.embeddedDocuments = newArrayList();
    }


    public Map<String, Object> getTikaMetadataAsMap() {
        Map<String, Object> map = newHashMap();
        for (String name : tikaMetadata.names()) {
            if (tikaMetadata.isMultiValued(name))
                map.put(name, asList(tikaMetadata.getValues(name)));
            else
                map.put(name, tikaMetadata.get(name));
        }
        return map;
    }

}