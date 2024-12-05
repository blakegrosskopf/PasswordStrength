import java.io.*;
import java.util.*;

public class StrongPasswordChecker {
    private static final int M_CHAINING = 1000; // Hash table size for separate chaining
    private static final int M_PROBING = 20000; // Hash table size for linear probing
    private static final int MIN_PASSWORD_LENGTH = 8;

    // Hash table for separate chaining
    private static List<String>[] chainingTable = new List[M_CHAINING];
    // Hash table for linear probing
    private static String[] probingTable = new String[M_PROBING];

    // Main method
    public static void main(String[] args) throws IOException {
        // Load dictionary into hash tables
        System.out.println("Loading dictionary...");
        loadDictionary("lib/wordlist.txt");
        System.out.println("Dictionary loaded.");

        // Accept password from user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a password to check: ");
        String password = scanner.nextLine();

        // Check password strength
        System.out.println("Checking password: " + password);
        boolean isStrong = isStrongPassword(password);
        System.out.println("Is the password strong? " + isStrong);
    }

    // Load dictionary words into hash tables
    private static void loadDictionary(String dictionaryFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(dictionaryFile));
        String word;
        int lineNumber = 1;

        while ((word = br.readLine()) != null) {
            insertChaining(word, lineNumber);
            insertProbing(word, lineNumber);
            lineNumber++;
        }

        br.close();
    }

    // Insert into separate chaining hash table
    private static void insertChaining(String word, int lineNumber) {
        int hash1 = hashCodeOld(word) % M_CHAINING;
        int index = Math.abs(hash1);

        if (chainingTable[index] == null) {
            chainingTable[index] = new LinkedList<>();
        }
        chainingTable[index].add(word);
    }

    // Insert into linear probing hash table
    private static void insertProbing(String word, int lineNumber) {
        int hash2 = hashCodeNew(word) % M_PROBING;
        int index = Math.abs(hash2);

        while (probingTable[index] != null) {
            index = (index + 1) % M_PROBING;
        }
        probingTable[index] = word;
    }

    // Check if a password is strong
    private static boolean isStrongPassword(String password) {
        // Rule (i): Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        // Rule (ii) and (iii): Check against dictionary and dictionary+digit
        if (existsInChaining(password) || existsInProbing(password)) {
            return false;
        }

        for (int i = 0; i <= 9; i++) {
            if (existsInChaining(password + i) || existsInProbing(password + i)) {
                return false;
            }
        }

        return true;
    }

    // Check existence in separate chaining hash table
    private static boolean existsInChaining(String word) {
        int hash1 = hashCodeOld(word) % M_CHAINING;
        int index = Math.abs(hash1);

        List<String> bucket = chainingTable[index];
        if (bucket != null) {
            return bucket.contains(word);
        }

        return false;
    }

    // Check existence in linear probing hash table
    private static boolean existsInProbing(String word) {
        int hash2 = hashCodeNew(word) % M_PROBING;
        int index = Math.abs(hash2);

        while (probingTable[index] != null) {
            if (probingTable[index].equals(word)) {
                return true;
            }
            index = (index + 1) % M_PROBING;
        }

        return false;
    }

    // Old hashCode function
    private static int hashCodeOld(String str) {
        int hash = 0;
        int skip = Math.max(1, str.length() / 8);
        for (int i = 0; i < str.length(); i += skip) {
            hash = (hash * 37) + str.charAt(i);
        }
        return hash;
    }

    // New hashCode function
    private static int hashCodeNew(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash * 31) + str.charAt(i);
        }
        return hash;
    }
}
