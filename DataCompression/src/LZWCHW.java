import java.util.*;
import java.io.*;
 import java.nio.charset.StandardCharsets; 
import java.nio.file.*;
import java.util.stream.Collectors;
public class LZWCHW {

	
	  /** Compress a string to a list of output symbols. */
    public static List<Integer> compress(String uncompressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);
 
        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }
 
        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return result;
    }
 
    /** Decompress a list of output ks to a string. */
    public static String decompress(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char)i);
 
        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k))
                entry = dictionary.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);
 
            result.append(entry);
 
            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));
 
            w = entry;
        }
        return result.toString();
    } 	
    private static void binaryform(int number ,DataOutputStream binFile) throws IOException {
        int remainder;
	        if (number <= 1) {
            System.out.print(number);
            return; // KICK OUT OF THE RECURSION
        }

        remainder = number % 2;
        binaryform(number >> 1,binFile);
        System.out.print(remainder);
		binFile.writeBytes(String.valueOf(remainder));

        
    }
    public static List<String> readFileInList(String fileName) 
    { 
      List<String> lines = Collections.emptyList(); 
      try
      { 
        lines = 
         Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); 
      } 
      catch (IOException e) 
      { 
        // do something 
        e.printStackTrace(); 
      } 
      return lines; 
    } 
    
    public static String intToString(int number, int groupSize) {
        StringBuilder result = new StringBuilder();

        for(int i = 8; i >= 0 ; i--) {
            int mask = 1 << i;
            result.append((number & mask) != 0 ? "1" : "0");

            if (i % groupSize == 0) {
                result.append(" ");
                }
        }
        result.replace(result.length() - 1, result.length(), "");

        return result.toString();
    }
    
    // read file and return as a string 
    public static String readFileAsString(String fileName)throws Exception 
    { 
      String data = ""; 
      data = new String(Files.readAllBytes(Paths.get(fileName))); 
      return data; 
    } 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
                String newLine = System.getProperty("line.separator");
                // read the input file 
		        String data = readFileAsString("D:\\DC\\DataCompression\\src\\input.txt"); 
	            //System.out.println("data1 : "+data);
	            
 		         
		        // to mult the id 1*(I.D.) 2*(I.D) � 9*(I.D.) 
		        long mulID=Integer.parseInt(data);
		        String newID=Long.toString(mulID);
		        for	(int i=2;i<9;i++)
		        {
		        	mulID=i*mulID;
		        	//System.out.println("mulID "+ mulID);
		        	newID+=Long.toString(mulID);
		        }
		        // to save the new String at the new File name inputMultID.txt		       
		        DataOutputStream newIDFile =  new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\inputMultID.txt")); 
		        newIDFile.writeBytes(newID);
		        //System.out.println("LOL "+ newID);
		        newIDFile.close();
		        List<Integer> compressed = compress(newID);
		        System.out.println(" compressed : \n"+compressed);
		        DataOutputStream binFile = new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\compressed.txt"));
		        DataOutputStream binFile1 = new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\compressed.bin"));

		        for(int i=0;i<compressed.size();i++)
		        {
		        String num=intToString(compressed.get(i),2);
		        System.out.println("\n binary "+num);
		        num = num.replaceAll("\\s+","");
		        //System.err.println("no spa.. : "+ num);
 				binFile.writeBytes(num);
		        binFile.writeBytes(newLine);
		        binFile1.writeBytes(num);
		        binFile1.writeBytes(newLine);
	         }
		        binFile.close();
		        binFile1.close();
				List<String> lines = readFileInList("D:\\DC\\DataCompression\\src\\compressed.txt");
		         
		    	List<Integer> newList = new ArrayList<Integer>();
		    	for(int i=0;i<lines.size();i++)
		    	{
		    		int k=Integer.parseInt(lines.get(i),2);
 
		    		newList.add(i, k);
 		    		
		    	}
		    	System.out.println("\nlines " +lines + "\nnewList :" +newList);
		    	//System.out.println("maraaaa"+ Integer.parseInt(lines.get(0),2) );
		    	String decompressed = decompress(newList);
		        System.out.println( "decompressed :\n"+ decompressed);
		        
		        DataOutputStream decfile = new DataOutputStream(new FileOutputStream("D:\\DC\\DataCompression\\src\\output.txt"));

		     
		        decfile.writeBytes(decompressed);
		        decfile.close();
	        
		        
	}
		    
 	
	
	

}
