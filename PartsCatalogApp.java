import java.io.*;
import java.util.*;

// Class representing a Part
class Part {
    String id;
    String description;

    public Part(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ": " + description;
    }
}

// B+ Tree Node
class BPlusTreeNode {
    boolean isLeaf;
    List<String> keys;
    List<BPlusTreeNode> children;
    List<Part> records;
    BPlusTreeNode next;

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        if (isLeaf) {
            this.records = new ArrayList<>();
            this.next = null;
        } else {
            this.children = new ArrayList<>();
        }
    }
}

// B+ Tree Implementation
class BPlusTree {
    private BPlusTreeNode root;
    private int minDegree = 2;
    private int maxKeys = 4;
    private int maxRecords = 16;

    public BPlusTree() {
        root = new BPlusTreeNode(true);
    }

    public void insert(String key, Part part) {
        // B+ tree insertion logic
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
                i++;
            }
            node = node.children.get(i);
        }

        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        node.keys.add(i, key);
        node.records.add(i, part);

        if (node.records.size() > maxRecords) {
            splitLeaf(node);
        }
    }

    private void splitLeaf(BPlusTreeNode node) {
        BPlusTreeNode newNode = new BPlusTreeNode(true);
        int mid = node.records.size() / 2;
        newNode.records.addAll(node.records.subList(mid, node.records.size()));
        newNode.keys.addAll(node.keys.subList(mid, node.keys.size()));
        node.records.subList(mid, node.records.size()).clear();
        node.keys.subList(mid, node.keys.size()).clear();
        newNode.next = node.next;
        node.next = newNode;
    }

    public Part search(String key) {
        // Search logic
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
                i++;
            }
            node = node.children.get(i);
        }

        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i).equals(key)) {
                return node.records.get(i);
            }
        }
        return null;
    }

    public void delete(String key) {
        // Deletion logic
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
                i++;
            }
            node = node.children.get(i);
        }

        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i).equals(key)) {
                node.keys.remove(i);
                node.records.remove(i);
                return;
            }
        }
    }

    public void display() {
        // B+ tree display logic
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(0);
        }

        while (node != null) {
            for (Part part : node.records) {
                System.out.println(part);
            }
            node = node.next;
        }
    }
    public List<Part> getAllParts() {
        List<Part> partsList = new ArrayList<>();
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        while (node != null) {
            partsList.addAll(node.records);
            node = node.next;
        }
        return partsList;
    }
}

// Catalog System
class PartsCatalog {
    private BPlusTree tree;
    private String filename;

    public PartsCatalog(String filename) {
        this.filename = filename;
        this.tree = new BPlusTree();
        loadParts();
    }

    private void loadParts() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() >= 16) {
                    String id = line.substring(0, 7).trim();
                    String description = line.substring(15).trim();
                    tree.insert(id, new Part(id, description));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading parts file: " + e.getMessage());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Search Part\n2. Add Part\n3. Delete Part\n4. Modify Part\n5. Display\n6. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Part ID: ");
                    String searchId = scanner.nextLine();
                    Part found = tree.search(searchId);
                    System.out.println(found != null ? found : "Part not found.");
                    break;
                case 2:
                    System.out.print("Enter Part ID: ");
                    String newId = scanner.nextLine();
                    System.out.print("Enter Description: ");
                    String newDesc = scanner.nextLine();
                    tree.insert(newId, new Part(newId, newDesc));
                    System.out.println("Part added.");
                    break;
                case 3:
                    System.out.print("Enter Part ID to delete: ");
                    String delId = scanner.nextLine();
                    tree.delete(delId);
                    System.out.println("Part deleted.");
                    break;
                case 4:
                    System.out.print("Enter Part ID to modify: ");
                    String modId = scanner.nextLine();
                    System.out.print("Enter new Description: ");
                    String modDesc = scanner.nextLine();
                    tree.delete(modId);
                    tree.insert(modId, new Part(modId, modDesc));
                    System.out.println("Part modified.");
                    break;
                case 5:
                    tree.display();
                    break;
                case 6:
                    System.out.print("Save changes? (yes/no): ");
                    String save = scanner.nextLine();
                    if (save.equalsIgnoreCase("yes")) {
                        saveParts();
                    }
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void saveParts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Saving logic from B+ tree to file
            for (Part part : tree.getAllParts()) {
                bw.write(String.format("%-7s %-65s", part.id, part.description));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving parts file: " + e.getMessage());
        }
    }
}

// Main Class
public class PartsCatalogApp {
    public static void main(String[] args) {
        PartsCatalog catalog = new PartsCatalog("partfile.txt");
        catalog.run();
    }
}
