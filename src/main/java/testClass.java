import java.io.IOException;

public class testClass {

    static final int LIMIT = 20000;
    static final String KEY = "Qwerty12345";
    static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    public static void main(String[] asd) throws IOException {

    }

    private static int ownEncrypt(byte[] text, byte[] key, int off) {
        int[] keyHash = createSimpleHash(key);

        int nextKey = keyHash[0] % key.length;
        int tmpChar;

        for (int i = 0; i < text.length; i++) {
            tmpChar = text[i];
            text[i] = (byte) ((text[i] ^ key[nextKey]) ^ keyHash[i % key.length]);
            nextKey = (tmpChar * text[i]) % key.length;
        }

        return nextKey;
    }

    private static String normalEncrypt(String text, String key) {
        char[] chars = new char[text.length()];
        int[] key_int = key.chars().toArray();

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ key_int[i % key_int.length]);
        }

        return String.copyValueOf(chars);
    }

    private static int[] createSimpleHash(byte[] key) {
        int[] chars2 = new int[key.length];
        int[] hash = new int[key.length];
        for (int i = 0; i < key.length; i++) {
            chars2[i] = key[key.length-(i+1)] ^ i;
            hash[i] = (char)chars2[i];
            for (int n = 0; n < chars2.length; n++) {
                hash[i] = hash[i] ^ key[n];
            }
        }
        return hash;
    }
}
