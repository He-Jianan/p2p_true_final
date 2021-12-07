package edu.ufl.cise.cnt5106c.group8.manager;

import edu.ufl.cise.cnt5106c.group8.config.CommonReader;
import edu.ufl.cise.cnt5106c.group8.model.Common;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileManager{
    public static ConcurrentMap<Integer, byte[]> file2Piece(String filename, int pieceSize) throws IOException {
        File file = new File("./" + filename);
//        URL url = FileManager.class.getClassLoader().getResource(filename);
//        File file = new File(url.getFile());
//        InputStream inputStream = FileManager.class.getClassLoader().getResourceAsStream(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

//        InputStream inputStream = FileManager.class.getClassLoader().getResourceAsStream("config/" + filename);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

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
        Common common = CommonReader.read("Common.cfg");
        File file = new File("./" + filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (int i = 0; i < common.getTotalPieces(); i++) {
            fileOutputStream.write(indexPieceMap.get(i));
        }
        fileOutputStream.close();
    }
}

