package wtf.alexhanwow;

import java.util.Random;

public class CodeGen {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String Random(int length,Boolean isOpen) {
        if (isOpen) {
            StringBuilder result = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }
            return result.toString();
        }else return null;
    }
}
