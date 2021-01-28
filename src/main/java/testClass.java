import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class testClass {
    public static void main(String[] asd) throws IOException {

        File f = new File("C:\\xampp\\htdocs\\mywebsite\\password.txt");
        RandomAccessFile file = new RandomAccessFile(f, "r");
        int[] key = new int[]{5,-14,31,-9,3};

        for (int i=0; i < 10; i++) {
            byte b = file.readByte();
            System.out.println((char)(b >> -key[i%5]));
        }


    }

    private static String ownEncrypt(String text, String key) {
        char[] chars = new char[text.length()];
        int[] key_int = key.chars().toArray();

        int nextKey = 0;
        int tmpChar;

        for (int i = 0; i < chars.length; i++) {
            tmpChar = chars[i];
            chars[i] = (char) (text.charAt(i) ^ key_int[nextKey]);
            nextKey = tmpChar % key_int.length;
        }

        chars[0] = (char) (chars[0] ^ key_int[key_int.length - 1]);

        return String.copyValueOf(chars);
    }

    private static String normalEncrypt(String text, String key) {
        char[] chars = new char[text.length()];
        int[] key_int = key.chars().toArray();

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (text.charAt(i) ^ key_int[i % key_int.length]);
        }

        return String.copyValueOf(chars);
    }

    public static List<Integer> primeFactors(int number) {
        int n = number;
        List<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        return factors;
    }
}
