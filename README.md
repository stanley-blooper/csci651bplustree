# csci651bplustree

This project builds a parts catalog using a B+ Tree (2–4 Tree structure) for efficient storage and retrieval of parts information.

---

## 1. `Part` Class
```java
class Part {
    String id;
    String description;
    ...
}
```
- Represents a single part in the catalog.
- Contains a part ID and description.
- Overrides `toString()` for printing in `id: description` format.

---

## 2. `BPlusTreeNode` Class
```java
class BPlusTreeNode {
    boolean isLeaf;
    List<String> keys;
    List<BPlusTreeNode> children;
    List<Part> records;
    BPlusTreeNode next;
    ...
}
```
- Defines the structure of a node in the B+ Tree.
- Leaf nodes store:
  - `keys`: list of part IDs
  - `records`: list of corresponding `Part` objects
  - `next`: pointer to the next leaf node for sequential access
- Internal nodes store:
  - `keys`: separator keys
  - `children`: pointers to child nodes

---

## 3. `BPlusTree` Class
- Implements the logic for inserting, searching, deleting, updating, and splitting nodes in a B+ Tree.
- Uses a **2–4 Tree logic** with a `MAX_KEYS = 4`.

### Constructor
```java
public BPlusTree() {
    root = new BPlusTreeNode(true);
}
```
- Initializes the tree with a single leaf node.

### `insert(String key, Part part)`
- Inserts a new part.
- If a node is full, splits it and promotes a middle key to the parent.

### `insertNonFull(...)`
- Recursively inserts into a node that is not full.
- Chooses the correct child or inserts into the leaf.

### `splitChild(...)`
- Splits a full node:
  - Promotes a middle key
  - Moves keys and children or records to a sibling
  - Maintains leaf link structure

### `search(String key)`
- Finds and returns a part with the given ID.

### `delete(String key)`
- Removes a key and its record from the appropriate leaf.

### `update(String key, String newDescription)` *(new)*
- Searches for a part by ID and updates its description in-place.

### `display()`
- Traverses and prints all parts from the linked leaf nodes.

### `getAllParts()`
- Collects all records for file saving.

---

## 4. `PartsCatalog` Class
- Handles file I/O and interactive user menu.

### Constructor
```java
public PartsCatalog(String filename) { ... }
```
- Loads parts from a file into the B+ Tree.

### `loadParts()`
- Parses `partfile.txt`:
  - ID is extracted from characters 0–7
  - Description is extracted from character 15 onward

### `run()`
- Provides a text-based menu:
  1. Search a part
  2. Add a part
  3. Delete a part
  4. Modify a part *(uses new `update()` logic)*
  5. Display all parts
  6. Exit with option to save

### `saveParts()`
- Writes formatted output to `partfile.txt`
- Ensures consistent formatting for ID and description

---

## 5. `PartsCatalogApp` Class
```java
public class PartsCatalogApp {
    public static void main(String[] args) {
        PartsCatalog catalog = new PartsCatalog("partfile.txt");
        catalog.run();
    }
}
```
- Launches the catalog system with the input file.

---

Summary of Updates
- Tree now supports **2–4 node structure** with up to 4 keys per node.
- **`update()` method** added to simplify record modification.
- **Menu option 4** now updates instead of delete + insert.
- Code is optimized for balanced performance and cleaner modifications.

---

Requirements
- Java 17+
- VS Code with Java Extension Pack
- `partfile.txt` in the project directory

---

Future Features (Optional Enhancements)
- Add GUI using JavaFX or Swing
- Implement rebalancing on delete
- Export to CSV or JSON

