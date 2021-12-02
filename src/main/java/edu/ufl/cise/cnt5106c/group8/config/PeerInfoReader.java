package edu.ufl.cise.cnt5106c.group8.config;

import edu.ufl.cise.cnt5106c.group8.model.PeerInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PeerInfoReader{
    public static List<PeerInfo> read(String fileName) throws IOException {
        File file = new File("src/main/resources/config/" + fileName);
        InputStream in = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String str;
        List<PeerInfo> peerList = new ArrayList<>();
        while ((str = bufferedReader.readLine()) != null) {
            PeerInfo peerInfo = new PeerInfo();
            String[] info = str.split(" ");
            String peerId = info[0];
            peerInfo.setPeerId(peerId);
            String hostname = info[1];
            peerInfo.setHostname(hostname);
            String port = info[2];
            peerInfo.setPort(Integer.parseInt(port));
            String hasFileOrNot = info[3];
            peerInfo.setHasFile(hasFileOrNot.equals("1"));
            peerList.add(peerInfo);
        }
        return peerList;
    }
}
