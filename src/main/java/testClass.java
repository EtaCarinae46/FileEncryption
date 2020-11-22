public class testClass {
    public static void main(String[] asd) {
        String str1 = ">\u000E\u0013\u0004\f\u0011\u0012\u0014\f";
        String str2 = "sajtoskenyér";
        String str3 = "!\u000E\f\u0004A\u0013 \u000F\u0005\u000E\fA\u0015\u0004\u0019\u0015OOO";

        String key = "abc123";

        //System.out.println(normalEncrypt(str1, key));
        //System.out.println(normalEncrypt(str2, key));

        //System.out.println(ownEncrypt(str1, key));
        System.out.println(ownEncrypt(str2, key));
        //System.out.println(ownEncrypt(str3, key));
    }

    private static String ownEncrypt(String text, String key) {
        char[] chars = new char[text.length()];
        int[] key_int = key.chars().toArray();

        int nextKey = 0;
        int tmpChar;

        for (int i = 0; i < chars.length; i++) {
            tmpChar = chars[i];
            chars[i] = (char)(text.charAt(i) ^ key_int[nextKey]);
            nextKey = tmpChar % key_int.length;
        }

        chars[0] = (char)(chars[0] ^ key_int[key_int.length-1]);

        return String.copyValueOf(chars);
    }

    private static String normalEncrypt(String text, String key) {
        char[] chars = new char[text.length()];
        int[] key_int = key.chars().toArray();

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char)(text.charAt(i) ^ key_int[i % key_int.length]);
        }

        return String.copyValueOf(chars);
    }
}
