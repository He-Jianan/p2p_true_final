package edu.ufl.cise.cnt5106c.group8.manager;

import edu.ufl.cise.cnt5106c.group8.config.CommonReader;
import edu.ufl.cise.cnt5106c.group8.model.Common;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileManager {
    public static ConcurrentMap<Integer, byte[]> file2Piece(String filename, int pieceSize) throws IOException {
        File file = new File("src/main/resources/" + filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ConcurrentMap<Integer, byte[]> pieceMap = new ConcurrentHashMap<>();
        long fileLength = file.length();
        long savedLength = 0;
        int index = 0;
        while (savedLength < fileLength) {
            if (fileLength - savedLength >= pieceSize) {
                savedLength += pieceSize;
            } else {
                pieceSize = Math.toIntExact(fileLength - savedLength);
                savedLength = fileLength;
            }
            byte[] filePiece = new byte[pieceSize];
            bufferedInputStream.read(filePiece, 0, pieceSize);
            pieceMap.put(index, filePiece);
            index++;
        }
        return pieceMap;
    }

    public static void assembleFile(ConcurrentMap<Integer, byte[]> indexPieceMap, String filename) throws IOException {
        Common common = CommonReader.read("Common-dev.cfg");
        File file = new File("src/main/resources/" + filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (int i = 0; i < common.getTotalPieces(); i++) {
            fileOutputStream.write(indexPieceMap.get(i));
        }
        fileOutputStream.close();
    }
}

