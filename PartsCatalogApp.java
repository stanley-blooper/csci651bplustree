// Enhanced B+ Tree Java Code with Internal Node Splitting and Promotion

import java.io.*;
import java.util.*;

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

class BPlusTreeNode {
    boolean isLeaf;
    List<String> keys = new ArrayList<>();
    List<BPlusTreeNode> children = new ArrayList<>();
    List<Part> records = new ArrayList<>();
    BPlusTreeNode next;

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }
}

class BPlusTree {
    private BPlusTreeNode root;
    private final int MAX_KEYS = 3;

    public BPlusTree() {
        root = new BPlusTreeNode(true);
    }

    public void insert(String key, Part part) {
        BPlusTreeNode r = root;
        if (r.keys.size() == MAX_KEYS) {
            BPlusTreeNode s = new BPlusTreeNode(false);
            s.children.add(r);
            splitChild(s, 0);
            root = s;
        }
        insertNonFull(root, key, part);
    }

    private void insertNonFull(BPlusTreeNode node, String key, Part part) {
        int i = Collections.binarySearch(node.keys, key);
        i = i >= 0 ? i : -i - 1;

        if (node.isLeaf) {
            node.keys.add(i, key);
            node.records.add(i, part);
        } else {
            BPlusTreeNode child = node.children.get(i);
            if (child.keys.size() == MAX_KEYS) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key, part);
        }
    }

    private void splitChild(BPlusTreeNode parent, int index) {
        BPlusTreeNode full = parent.children.get(index);

        if (full.keys.size() < 2) return;

        BPlusTreeNode sibling = new BPlusTreeNode(full.isLeaf);
        int mid = full.keys.size() / 2;

        sibling.keys.addAll(full.keys.subList(mid + 1, full.keys.size()));
        List<String> leftKeys = new ArrayList<>(full.keys.subList(0, mid));

        if (full.isLeaf) {
            sibling.records.addAll(full.records.subList(mid + 1, full.records.size()));
            full.records = new ArrayList<>(full.records.subList(0, mid + 1));
            sibling.next = full.next;
            full.next = sibling;

            full.keys = new ArrayList<>(full.keys.subList(0, mid + 1));
            parent.keys.add(index, sibling.keys.get(0));
        } else {
            String promotedKey = full.keys.get(mid); // Save key before truncating list
            sibling.children.addAll(full.children.subList(mid + 1, full.children.size()));
            full.children = new ArrayList<>(full.children.subList(0, mid + 1));
            full.keys = leftKeys;

            parent.keys.add(index, promotedKey); // Safe access
        }

        parent.children.add(index + 1, sibling);
    }

    public Part search(String key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = Collections.binarySearch(node.keys, key);
            i = i >= 0 ? i + 1 : -i - 1;
            node = node.children.get(i);
        }
        int idx = node.keys.indexOf(key);
        return idx >= 0 ? node.records.get(idx) : null;
    }

    public void delete(String key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = Collections.binarySearch(node.keys, key);
            i = i >= 0 ? i + 1 : -i - 1;
            node = node.children.get(i);
        }
        int idx = node.keys.indexOf(key);
        if (idx >= 0) {
            node.keys.remove(idx);
            node.records.remove(idx);
        }
    }

    public void display() {
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
        List<Part> all = new ArrayList<>();
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        while (node != null) {
            all.addAll(node.records);
            node = node.next;
        }
        return all;
    }
}

class PartsCatalog {
    private final BPlusTree tree = new BPlusTree();
    private final String filename;

    public PartsCatalog(String filename) {
        this.filename = filename;
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
            System.out.println("Error loading parts: " + e.getMessage());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Search Part\n2. Add Part\n3. Delete Part\n4. Modify Part\n5. Display\n6. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Part ID: ");
                    String id = scanner.nextLine();
                    Part found = tree.search(id);
                    System.out.println(found != null ? found : "Not found");
                }
                case 2 -> {
                    System.out.print("ID: ");
                    String newId = scanner.nextLine();
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();
                    tree.insert(newId, new Part(newId, desc));
                }
                case 3 -> {
                    System.out.print("ID to delete: ");
                    tree.delete(scanner.nextLine());
                }
                case 4 -> {
                    System.out.print("ID to modify: ");
                    String modId = scanner.nextLine();
                    System.out.print("New description: ");
                    String modDesc = scanner.nextLine();
                    tree.delete(modId);
                    tree.insert(modId, new Part(modId, modDesc));
                }
                case 5 -> tree.display();
                case 6 -> {
                    System.out.print("Save changes? (yes/no): ");
                    if (scanner.nextLine().equalsIgnoreCase("yes")) saveParts();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void saveParts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Part part : tree.getAllParts()) {
                bw.write(String.format("%-7s %-65s", part.id, part.description));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving parts: " + e.getMessage());
        }
    }
}

public class PartsCatalogApp {
    public static void main(String[] args) {
        PartsCatalog catalog = new PartsCatalog("partfile.txt"); // partfile.txt must exist in the project directory
        catalog.run();
    }
}
