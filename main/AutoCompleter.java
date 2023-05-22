import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TrieNode {

    // TrieNode children.
    private TrieNode[] children;
    private boolean isEndOfWord;

    // Constructor to initialize child nodes.
    public TrieNode() {
        children = new TrieNode[52]; //Accommodates both uppercase and lowercase letters
        isEndOfWord = false;
    }

    // Checks to make sure string input is valid, and returns an integer index for other method.
    private int getIndex(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return ch - 'a';
        } else if (ch >= 'A' && ch <= 'Z') {
            return ch - 'A' + 26;
        }
        throw new IllegalArgumentException("Invalid character: " + ch);
    }


    // Insertion takes O(word.length()) complexity with this format, uses helper method above to add 
    // specific characters to their correct index.
    public void insert(String word) {
        TrieNode current = this;
        for (char ch : word.toCharArray()) {
            int index = getIndex(ch);
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    // Search takes O(word.length()), uses the getIndex helper method for the index to search for.
    public boolean search(String word) {
        TrieNode current = this;
        for (char ch : word.toCharArray()) {
            int index = getIndex(ch);
            if (current.children[index] == null) {
                return false;
            }
            current = current.children[index];
        }
        return current != null && current.isEndOfWord;
    }

    // Follows through a temp TrieNode to the last index of the prefix, 
    // then uses a helper method to collect the words after the current index.
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> words = new ArrayList<>();
        TrieNode current = this;
        for (char ch : prefix.toCharArray()) {
            int index = getIndex(ch);
            if (current.children[index] == null) {
                return words;
            }
            current = current.children[index];
        }
        collectWords(current, prefix, words);
        return words;
    }

    // Recursive method to follow through the TrieNode and gather the end of the words
    // from the above method. 
    private void collectWords(TrieNode node, String prefix, List<String> words) {
        if (node.isEndOfWord) {
            words.add(prefix);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            int index = getIndex(ch);
            if (node.children[index] != null) {
                collectWords(node.children[index], prefix + ch, words);
            }
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            int index = getIndex(ch);
            if (node.children[index] != null) {
                collectWords(node.children[index], prefix + ch, words); // prefix + ch = complete word
            }
        }
    }
}

public class AutoCompleter {
    private static TrieNode trie = new TrieNode();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the name of the text file (without quotes and extension): ");
        String fileName = scanner.nextLine();
        String filePath = "./" + fileName + ".txt";

        // try-catch block for user convenience.
        try {
            loadNamesFromFile(filePath);
        } catch (IOException e) {
            System.out.println("Error occurred while reading the file.");
            e.printStackTrace();
            return;
        }

        System.out.println("File loaded successfully.");
        // main driver part of main function.
        while (true) {
			System.out.println("Enter a name to autocomplete:");			
            String input = scanner.nextLine();
            List<String> autocompleteList = trie.getWordsWithPrefix(input.toLowerCase());

            if (autocompleteList.isEmpty()) {
                System.out.println("No matching names found.");
            } else if (autocompleteList.size() == 1 && autocompleteList.get(0).equals(input.toLowerCase())) {
                System.out.println("Autocomplete: " + input);
            } else {
                System.out.println("Autocomplete suggestions:");
                for (String name : autocompleteList) {
                    System.out.println(name);
                }
            }
        }
    }
    // method to handle the file input from users and insert into the trie.
    private static void loadNamesFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] names = line.split(",");
            for (String name : names) {
                String trimmedName = name.trim().toLowerCase();
                trie.insert(trimmedName);
            }
        }
        reader.close();
    }
}
