package de.kyrtap.FritzStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SessionHandler {
    private String username, password, SID;

    public SessionHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String generateSID() throws Exception {
        String url = "http://fritz.box/login_sid.lua";
        Document doc = Jsoup.connect(url).get();
        SID = doc.select("SID").text();

        if (SID.equals("0000000000000000")) {
            String challenge = doc.select("challenge").text();
            url = "http://fritz.box/login_sid.lua?username=" +
                    username + "&response=" + createResponse(challenge, password);
            doc = Jsoup.connect(url).get();
            SID = doc.select("SID").text();

            if (SID.equals("0000000000000000")) {
                int blockTime = Integer.parseInt(doc.select("BlockTime").text());
                throw new Exception("Wrong password! Try again in " + blockTime + " seconds.");
            }
        }
        return SID;
    }

    public String getSID() {
        return SID;
    }

    private String createResponse(String challenge, String password) {
        return challenge + "-" + createMD5Hash(challenge + "-" + password);
    }

    private String createMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] inputByte = input.getBytes("UTF-16LE");
            md.update(inputByte, 0, inputByte.length);
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception ex) {
            return null;
        }
    }

    public void terminateConnection() throws Exception {
        String url = "http://fritz.box/login_sid.lua?logout=1&sid=" + SID;
        Document doc = Jsoup.connect(url).get();
    }
}
