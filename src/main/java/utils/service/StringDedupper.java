package utils.service;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;

public class StringDedupper {

    public static List<String> getChunks(String string, int sizeInBytes, boolean overlapping) {

        List<String> toReturn = newArrayList();

        byte[] bytes = string.getBytes(UTF_8);
        int cursor = 0;

        while (cursor != bytes.length) {
            int advancement = bytes.length - cursor >= sizeInBytes ? sizeInBytes : bytes.length - cursor;
            byte[] chunk;

            if (overlapping)
                chunk = Arrays.copyOfRange(
                        bytes,
                        cursor >= getOverlap(sizeInBytes) ? cursor - getOverlap(sizeInBytes) : cursor,
                        cursor + advancement
                );
            else
                chunk = Arrays.copyOfRange(bytes, cursor, cursor + advancement);

            toReturn.add(new String(chunk, UTF_8));
            cursor += advancement;
        }

        return toReturn;

    }

    public static int getOverlap(int sizeOfChunkInBytes) {
        return Math.min(60, Math.floorDiv(sizeOfChunkInBytes, 10) + 1);
    }

}
