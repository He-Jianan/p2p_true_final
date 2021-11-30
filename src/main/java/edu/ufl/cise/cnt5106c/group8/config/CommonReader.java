package edu.ufl.cise.cnt5106c.group8.config;

import edu.ufl.cise.cnt5106c.group8.model.Common;

import java.io.*;

public class CommonReader {
    public static Common read(String fileName) {
        try {
            File file = new File("src/main/resources/config/" + fileName);
            InputStream in = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            int numberOfPreferredNeighbors = 0;
            int unchokingInterval = 0;
            int optimisticUnchokingInterval = 0;
            int fileSize = 0;
            int pieceSize = 0;
            int totalPieces;
            for (String line; (line = bufferedReader.readLine()) != null;) {
                String[] config = line.split(" ");
                String name = config[0];
                String value = config[1];

                switch (name) {
                    case "NumberOfPreferredNeighbors":
                        numberOfPreferredNeighbors = Integer.parseInt(value) ;
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(value);
                        break;
                    case "OptimisticUnchokingInterval":
                        optimisticUnchokingInterval = Integer.parseInt(value);
                        break;
                    case "FileName":
                        fileName = value;
                        break;
                    case "FileSize":
                        fileSize = Integer.parseInt(value);
                        break;
                    case "PieceSize":
                        pieceSize = Integer.parseInt(value);
                        break;
                }
            }
            in.close();
            totalPieces = (int) Math.ceil((double) fileSize/(double) pieceSize);
            int totalLength = (int) Math.ceil((double) totalPieces/8) * 8;
//            if (fileSize % pieceSize == 0) {
//                totalPieces = fileSize/pieceSize;
//            } else {
//                totalPieces = fileSize/pieceSize + 1;
//            }
            return new Common(numberOfPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize, totalPieces, totalLength);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
