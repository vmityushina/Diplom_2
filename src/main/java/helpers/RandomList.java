package helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RandomList {
    public static List<Map<String, String>> pickNRandom(List<Map<String, String>> lst, int n) {
        List<Map<String, String>> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }
}
