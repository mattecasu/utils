package utils.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class TextPortion {

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private Long start;

    @Getter
    @Setter
    private Long end;

}