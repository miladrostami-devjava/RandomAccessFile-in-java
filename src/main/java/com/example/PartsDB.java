package com.example;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PartsDB {
    // Constants for field lengths
    public static final int PNUMLEN = 20;   // Part number length
    public static final int DESCLEN = 30;   // Description length
    public static final int QUANLEN = 4;    // Quantity field length
    public static final int COSTLEN = 4;    // Cost field length

    // Record length: calculated based on field lengths
    private static final int RECLEN = 2 * PNUMLEN + 2 * DESCLEN + QUANLEN + COSTLEN;

    // RandomAccessFile object for file operations
    private static RandomAccessFile raf;

    // Constructor to initialize RandomAccessFile
    public PartsDB(String path) throws IOException {
        raf = new RandomAccessFile(path, "rw");
    }

    // Method to append a new part to the database
    public static void append(String partnum, String partdesc, int qty, int ucost) throws IOException {
        raf.seek(raf.length()); // Move to the end of the file
        write(partnum, partdesc, qty, ucost); // Write the part information
    }

    // Method to close the RandomAccessFile
    public void close() {
        try {
            raf.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    // Method to get the number of records in the database
    public int numRecs() throws IOException {
        return (int) (raf.length() / RECLEN);
    }

    // Method to select a part by its record number
    public Part select(int recno) throws IOException {
        if (recno < 0 || recno >= numRecs()) {
            throw new IllegalArgumentException(recno + " out of range");
        }
        raf.seek(recno * RECLEN); // Move to the specified record
        return read(); // Read and return the part
    }

    // Method to update a part at a specific record number
    public void update(int recno, String partnum, String partdesc, int qty, int ucost) throws IOException {
        if (recno < 0 || recno >= numRecs()) {
            throw new IllegalArgumentException(recno + " out of range");
        }
        raf.seek(recno * RECLEN); // Move to the specified record
        write(partnum, partdesc, qty, ucost); // Write the new part information
    }

    // Private method to read a part from the current file position
    private Part read() throws IOException {
        StringBuilder sb = new StringBuilder();

        // Read part number
        for (int i = 0; i < PNUMLEN; i++) {
            sb.append(raf.readChar());
        }
        String partnum = sb.toString().trim();
        sb.setLength(0);

        // Read part description
        for (int i = 0; i < DESCLEN; i++) {
            sb.append(raf.readChar());
        }
        String partdesc = sb.toString().trim();

        // Read quantity and unit cost
        int qty = raf.readInt();
        int ucost = raf.readInt();

        return new Part(partnum, partdesc, qty, ucost);
    }

    // Private method to write a part to the current file position
    private static void write(String partnum, String partdesc, int qty, int ucost) throws IOException {
        StringBuilder sb = new StringBuilder(partnum);

        // Ensure part number has fixed length
        if (sb.length() > PNUMLEN) {
            sb.setLength(PNUMLEN);
        } else {
            while (sb.length() < PNUMLEN) {
                sb.append(" ");
            }
        }
        raf.writeChars(sb.toString());

        // Ensure description has fixed length
        sb = new StringBuilder(partdesc);
        if (sb.length() > DESCLEN) {
            sb.setLength(DESCLEN);
        } else {
            while (sb.length() < DESCLEN) {
                sb.append(" ");
            }
        }
        raf.writeChars(sb.toString());

        // Write quantity and unit cost
        raf.writeInt(qty);
        raf.writeInt(ucost);
    }

    // Inner class to represent a part
    public static class Part {
        private String partnum;
        private String desc;
        private int qty;
        private int ucost;

        public Part(String partnum, String desc, int qty, int ucost) {
            this.partnum = partnum;
            this.desc = desc;
            this.qty = qty;
            this.ucost = ucost;
        }

        public String getDesc() {
            return desc;
        }

        public String getPartnum() {
            return partnum;
        }

        public int getQty() {
            return qty;
        }

        public int getUnitCost() {
            return ucost;
        }
    }


    public static void main(String[] args) throws IOException {
        PartsDB db = new PartsDB("src/main/resources/partsdb.txt");
db.read();
db.numRecs();
        append("book","note book",4,1500);
    }

}
