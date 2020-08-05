import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.io.*;
 import java.nio.charset.StandardCharsets; 
import java.nio.file.*;
import java.util.stream.Collectors;
public class ArithmeticCoding  extends LZWCHW{
    private static class Triple<A, B, C> {
        A a;
        B b;
        C c;
 
        Triple(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
 
    private static class Freq extends HashMap<Character, Long> {
        //"type alias"
    }
 
    private static Freq cumulativeFreq(Freq freq) {
        long total = 0;
        Freq cf = new Freq();
        for (int i = 0; i < 256; ++i) {
            char c = (char) i;
            Long v = freq.get(c);
            if (v != null) {
                cf.put(c, total);
                total += v;
            }
        }
        return cf;
    }
 
    private static Triple<BigInteger, Integer, Freq> arithmeticCoding(String str, Long radix) {
        // Convert the string into a char array
        char[] chars = str.toCharArray();
 
        // The frequency characters
        Freq freq = new Freq();
        for (char c : chars) {
            if (!freq.containsKey(c))
                freq.put(c, 1L);
            else
                freq.put(c, freq.get(c) + 1);
        }
 
        // The cumulative frequency
        Freq cf = cumulativeFreq(freq);
 
        // Base
        BigInteger base = BigInteger.valueOf(chars.length);
 
        // LowerBound
        BigInteger lower = BigInteger.ZERO;
 
        // Product of all frequencies
        BigInteger pf = BigInteger.ONE;
 
        // Each term is multiplied by the product of the
        // frequencies of all previously occurring symbols
        for (char c : chars) {
            BigInteger x = BigInteger.valueOf(cf.get(c));
            lower = lower.multiply(base).add(x.multiply(pf));
            pf = pf.multiply(BigInteger.valueOf(freq.get(c)));
        }
 
        // Upper bound
        BigInteger upper = lower.add(pf);
 
        int powr = 0;
        BigInteger bigRadix = BigInteger.valueOf(radix);
 
        while (true) {
            pf = pf.divide(bigRadix);
            if (pf.equals(BigInteger.ZERO)) break;
            powr++;
        }
 
        BigInteger diff = upper.subtract(BigInteger.ONE).divide(bigRadix.pow(powr));
        return new Triple<>(diff, powr, freq);
    }
 
    private static String arithmeticDecoding(BigInteger num, long radix, int pwr, Freq freq) {
        BigInteger powr = BigInteger.valueOf(radix);
        BigInteger enc = num.multiply(powr.pow(pwr));
        long base = 0;
        for (Long v : freq.values()) base += v;
 
        // Create the cumulative frequency table
        Freq cf = cumulativeFreq(freq);
 
        // Create the dictionary
        Map<Long, Character> dict = new HashMap<>();
        for (Map.Entry<Character, Long> entry : cf.entrySet()) dict.put(entry.getValue(), entry.getKey());
 
        // Fill the gaps in the dictionary
        long lchar = -1;
        for (long i = 0; i < base; ++i) {
            Character v = dict.get(i);
            if (v != null) {
                lchar = v;
            } else if (lchar != -1) {
                dict.put(i, (char) lchar);
            }
        }
 
        // Decode the input number
        StringBuilder decoded = new StringBuilder((int) base);
        BigInteger bigBase = BigInteger.valueOf(base);
        for (long i = base - 1; i >= 0; --i) {
            BigInteger pow = bigBase.pow((int) i);
            BigInteger div = enc.divide(pow);
            Character c = dict.get(div.longValue());
            BigInteger fv = BigInteger.valueOf(freq.get(c));
            BigInteger cv = BigInteger.valueOf(cf.get(c));
            BigInteger diff = enc.subtract(pow.multiply(cv));
            enc = diff.divide(fv);
            decoded.append(c);
        }
        // Return the decoded output
        return decoded.toString();
    }
 
    public static void main(String[] args) throws IOException {
    	   long radix = 10;
    	
		List<String> lines = readFileInList("D:\\DC\\DataCompression\\src\\output.txt");
        System.out.println("data1 : "+lines);
        for(int i=0;i<lines.size();i++)
        {  String fmt = "%-25s=> %19s * %d^%s\n"; 
        	 Triple<BigInteger, Integer, Freq> encoded = arithmeticCoding(lines.get(i), radix);
             String dec = arithmeticDecoding(encoded.a, radix, encoded.b, encoded.c);
             System.out.printf(fmt, lines.get(i), encoded.a, radix, encoded.b);
             
             if (!Objects.equals(lines.get(i), dec)) throw new RuntimeException("\tHowever that is incorrect!");
        	//adaptive arithmetic coding
		        DataOutputStream saveData = new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\outputarithmeticCoding1.txt"));
		        saveData.writeBytes(lines.get(i)+" ==>"+encoded.a + " " +radix + "^"+ encoded.b);
		        saveData.close();
        }
        
    	List<String> lines2 = readFileInList("D:\\DC\\DataCompression\\src\\compressed.txt");
        DataOutputStream saveData = new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\outputarithmeticCoding2.txt"));

        System.out.println("data1 : "+lines2);
        for(int i=0;i<lines2.size();i++)
        {  String fmt = "%-25s=> %19s * %d^%s\n"; 
        	 Triple<BigInteger, Integer, Freq> encoded = arithmeticCoding(lines2.get(i), radix);
             String dec = arithmeticDecoding(encoded.a, radix, encoded.b, encoded.c);
             System.out.printf(fmt, lines2.get(i), encoded.a, radix, encoded.b);
             
             if (!Objects.equals(lines2.get(i), dec)) throw new RuntimeException("\tHowever that is incorrect!");
        	//adaptive arithmetic coding
 		        saveData.writeBytes(lines2.get(i)+" ==>"+encoded.a + " " +radix + "^"+ encoded.b+ "\n");
		       // saveData.close();
        }
    }
}