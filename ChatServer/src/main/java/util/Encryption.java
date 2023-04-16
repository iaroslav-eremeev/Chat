package util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class Encryption {
    public static String generateHash() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        return DigestUtils.md5Hex(RandomStringUtils.random(length, useLetters, useNumbers));
    }
}
