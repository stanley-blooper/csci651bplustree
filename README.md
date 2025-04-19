# csci651bplustree
This project builds a parts catalog using a B-tree for storage


1. Part Class

class Part {
    String id;
    String description;
    ...
}

Represents a single part in the catalog.

Contains a part ID and a description.

Overrides toString() to print in id: description format.

2. BPlusTreeNode Class

class BPlusTreeNode {
    boolean isLeaf;
    List<String> keys;
    List<BPlusTreeNode> children;
    List<Part> records;
    BPlusTreeNode next;
    ...
}

Defines the structure of each B+ Tree node.

If it's a leaf, it stores:

keys: list of part IDs

records: list of Part objects

next: pointer to the next leaf node for sequential access

If it's an internal node, it stores:

keys: separator keys

children: pointers to child nodes

3. BPlusTree Class

Manages the logic for inserting, searching, deleting, and splitting nodes.

Constructor:

public BPlusTree() { root = new BPlusTreeNode(true); }

Initializes the root as an empty leaf node.

insert(String key, Part part)

Inserts a new part into the tree.

If the root is full, it is split and a new root is created.

insertNonFull(...)

Recursively finds the correct location to insert a key.

Handles both leaf and internal node cases.

splitChild(...)

Splits a full child node:

Promotes a middle key to the parent.

Creates a new sibling node.

Ensures leaf nodes remain linked for traversal.

search(String key)

Navigates down the tree to find and return the matching Part.

delete(String key)

Locates and removes a key and its record from the leaf node.

Note: No tree rebalancing is implemented.

display()

Traverses leaf nodes left to right, printing each part.

getAllParts()

Returns all part records from the tree (used for saving).

4. PartsCatalog Class

Manages file I/O and user interaction.

Constructor:

public PartsCatalog(String filename) { ... }

Loads part data from partfile.txt.

loadParts()

Reads each line of the file.

Extracts ID (0–7 chars) and description (starting at char 15).

Inserts data into the B+ Tree.

run()

Console-based UI that offers the following options:

Search by part ID

Add a new part

Delete a part

Modify a part's description

Display all parts

Exit and optionally save

saveParts()

Writes all records from the B+ Tree back into partfile.txt.

Formats each line with fixed-width for ID and description.

5. PartsCatalogApp Class

public class PartsCatalogApp {
    public static void main(String[] args) {
        PartsCatalog catalog = new PartsCatalog("partfile.txt");
        catalog.run();
    }
}

Entry point of the program.

Initializes the catalog system with the file partfile.txt and starts the menu loop.

Summary

The B+ Tree handles efficient searching and sequential access via linked leaf nodes.

The app supports full CRUD operations for part records.

Input/output is handled via the terminal and partfile.txt.

Scanner reads user input; BufferedReader and BufferedWriter handle file I/O.

