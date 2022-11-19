package configs;

import java.util.List;

public class Config {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    private static final int HASH_CODE_LENGTH = 24;

    public static String getBaseUri() {
        return BASE_URI;
    }
    public static int getHashCodeLength() {return HASH_CODE_LENGTH; }
}
