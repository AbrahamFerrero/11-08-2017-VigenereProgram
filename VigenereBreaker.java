
/**
 *
 * This is the only class to complete by me. The rest of them were given by the exercise. 
 * 
 * @author (Abraham Ferrero) 
 * @version (08/NOV/2017)
 */
import java.util.*;
import edu.duke.*;


public class VigenereBreaker {
    private int[] validKey;
    //This method returns a string message, sliced the way the exercise wants (please read readme.txt file)
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //whichSlice por donde empezar
        //totalslices, cada cuanto corta (cada 3, cada 5...)
        StringBuilder sb = new StringBuilder();
        for(int i = whichSlice; i < message.length(); i += totalSlices){
            sb.append(message.charAt(i));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        /*This method gets a String (a encrypted text as a String from another method),
         * and slices it in an amount of portions as big as the key length, and calculates the key
         * for each portion of encrypted string sliced. After that, gets every key and stores it in an Array,
         * to be returned at the end of the method. for more info, read the readme.txt file.
         */
        int[] key = new int[klength]; 
        CaesarCracker cc = new CaesarCracker();
        for ( int i=0; i<klength; i++){
            String SliceEach = sliceString(encrypted, i, klength);
            int newKey = cc.getKey(SliceEach);
            key[i] = newKey;
        }
        return key;
    }

    public void breakVigenere () {
       /*This method follows the rules given by the readme.txt file, please read. 
        * basically applies every method created, in a decrrypted file selected, and prints the
        * decrypted text.
        */
        FileResource f = new FileResource();
       String textToStr = f.asString();
       /*I think it is allright to set the keylength manually to 5 as the exercise tells it.
        * Our next exercise is about gessing the length of the key anyway. 
        */
       int[] applykey = tryKeyLength(textToStr,5,'e');
       VigenereCipher vignere = new VigenereCipher(applykey);
       System.out.println(vignere.decrypt(textToStr));
    }
    
    //This methods are private because I used them for my particular testing purposes, they weren't asked by the exercise itself
    private void tempTest(){
       String slice = sliceString("abcdefghijklm", 4, 5);
       System.out.println(slice);
    }
    private void testTryKeyLength(){
        //Test method for the TryKeyLength() method, demonstrating it works.
        FileResource f = new FileResource("data/secretmessage1.txt");
        String keyflute = f.asString();
        int []key = tryKeyLength(keyflute,4,'e');
        for (int i=0; i< key.length; i++){
            System.out.println(key[i]);
        }
    }
    
    //From now on, every method is related to Assigment 2 tasks:
    
    public HashSet<String> readDictionary(FileResource fr){
        /*As the exercise demands(read the readme.txt file for more info), 
         * this method creates a hashset, adding every word of the fr file, which
         * will be a dictionary, as a String and returns the hashset created. Very simple.
         */
        HashSet<String> dictionaryList = new HashSet<String>();
        for (String line : fr.lines()) {
            String lineLower = line.toLowerCase();
            dictionaryList.add(lineLower);
        }
        return dictionaryList;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        String[] messageSplit = message.split("\\W+");
        int commonWords = 0;
        for(int i=0; i < messageSplit.length; i++){
            String word = messageSplit[i].toLowerCase();
            if (dictionary.contains(word)){
                commonWords++;
            }
        }
        return commonWords;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        int max = 0;
        String perfectDecrypted = "";
        for(int i=1; i<=100; i++){
            /*Before, the third parameter was 'e', but I've changed this as demanded in
             * assignment 3 so it takes the mostCommon character in the dictionary as parameter.
             * This will make it work for every dictionary in our folder with some methods shown below
             */
            int[] keys = tryKeyLength(encrypted, i, mostCommonCharIn(dictionary));
            VigenereCipher vignere = new VigenereCipher(keys);
            String decrypted = vignere.decrypt(encrypted);
            int wordsCounted = countWords(decrypted, dictionary);
            if(wordsCounted > max){
                max = wordsCounted;
                perfectDecrypted = decrypted;
                validKey = keys;
            }
        }
        System.out.println("This file contains " + max + " valid words.");
        return perfectDecrypted;
    }
    /*I created this method myself that returns the valid key, as a bunch of numbers and as a word,
     * just in case it is asked in the following tests.
     */
    public String getKeyAsWord(){
        String alphabet= "abcdefghijklmnopqrstuvwxyz";
        StringBuilder keyAsWord = new StringBuilder();
        for (int i=0; i<validKey.length;i++){
            keyAsWord.append(alphabet.charAt(validKey[i]));
        }
        System.out.println("This is the valid key as numbers: ");
        for(int i=0; i<validKey.length; i++){
            System.out.println(validKey[i]);
        }
        System.out.println("And as a word:");
        return keyAsWord.toString();
    }
    
    public void breakVigenere2(){
        FileResource text = new FileResource("data/secretmessage2.txt");
        FileResource fr = new FileResource("dictionaries/English");
        HashSet<String> dictionary = readDictionary(fr);
        String testingFile = text.asString();
        String perfectDecryption = breakForLanguage(testingFile, dictionary);
        System.out.println(perfectDecryption);
        String keyAsWord = getKeyAsWord();
        System.out.println(keyAsWord);
    }
    //I've created this method for particular testing purposes, that's why it is private. 
    private void countWordsTester(){
        FileResource fr = new FileResource("dictionaries/English");
        HashSet<String> dictionary = readDictionary(fr);
        FileResource message = new FileResource("data/athens.txt");
        String messageStr = message.asString();
        System.out.println(countWords(messageStr, dictionary));
    }
    //Assignment 3 methods from now on:
    //I also included some modifications to other methods. When done, it will be explained as a comment on each method.
    public char mostCommonCharIn(HashSet<String> dictionary){
        /*I am sure there are many ways to get the most common char in a dictionary,
         * I did this my own way: First, I created a Hashmap with the letters of the dictionary and counts.
         * After that, I iterated over every word, and for each word every letter in the dictionary.
         * If the word contains the letters in the dictionary, count 1 for each time.
         * Then, it is time to get the letter that is repeated the most, and return it:
         */
        HashMap<Character, Integer> characters = new HashMap<Character, Integer>();
        char[] chars = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
                        'p','q','r','s','t','u','v','w','x','y','z'};
        for( int i=0; i<chars.length; i++){
            characters.put(chars[i],0);
        }
        for (String word : dictionary){
           for( char s : characters.keySet()){
               if (word.contains(Character.toString(s))){
                   characters.put(s, characters.get(s)+1);
                }
            }
        }
        int maxValue = 0;
        for(char s : characters.keySet()){
            int value = characters.get(s);
            if (value > maxValue){
                maxValue = value;
            }
        }
        for(char s : characters.keySet()){
            if (characters.get(s) == maxValue){
                return s;
            }
        }
        return 'a';
    }
    
    private HashMap<String, HashSet<String>> langList(){
        /*This method is private as it is to prepare the next one.
         * Basically, what it does is creating a HashMap with every language as a string
         * and the dictionary as a HashSet with every word on it.
         */
        HashMap<String, HashSet<String>> languages = new HashMap<String, HashSet<String>>();
        FileResource English = new FileResource("dictionaries/English");
        languages.put("English", readDictionary(English));
        FileResource Danish = new FileResource("dictionaries/Danish");
        languages.put("Danish", readDictionary(Danish));
        FileResource Dutch = new FileResource("dictionaries/Dutch");
        languages.put("Dutch", readDictionary(Dutch));
        FileResource French = new FileResource("dictionaries/French");
        languages.put("French", readDictionary(French));
        FileResource German = new FileResource("dictionaries/German");
        languages.put("German", readDictionary(German));
        FileResource Italian = new FileResource("dictionaries/Italian");
        languages.put("Italian", readDictionary(Italian));
        FileResource Portuguese = new FileResource("dictionaries/Portuguese");
        languages.put("Portuguese", readDictionary(Portuguese));
        FileResource Spanish = new FileResource("dictionaries/Spanish");
        languages.put("Spanish", readDictionary(Spanish));
        /* To test if the method is right:
         * for (String s : languages.keySet()){
            HashSet<String> size = languages.get(s);
            System.out.println(s + "  " + size.size());
        }*/
        return languages;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
        for (String s : languages.keySet()){
            HashSet lang = languages.get(s);
            String decrypted = breakForLanguage(encrypted,lang);
            int wordCount = countWords(decrypted, lang);
            System.out.println("LANGUAGE CHOSEN = " + s);
            System.out.println("Decrypted message  ="+ decrypted);
            System.out.println("Words counted = "+ wordCount);
        }
    }
    /*Instead of modifying breakVigenere, i will create a new method and modify it.
     * I want to preserve my last methods so we can test older exercises too.
     */
    public void breakVigenere3(){
        FileResource text = new FileResource("data/secretmessage3.txt");
        langList();
        String testingFile = text.asString();
        breakForAllLangs(testingFile, langList());
        String keyAsWord = getKeyAsWord();
        System.out.println(keyAsWord);
    }
}
