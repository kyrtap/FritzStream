package de.kyrtap.FritzStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigInteger;
import java.security.MessageDigest;

/** Supplies methods to deal with streams from a FRITZ!Box cable device.
 * @author kyrtap
 * @version 1.0.0
 */
public class SessionHandler {
    private String username, password, SID;

    /**
     * Creates a new handler instance.
     * @param username Username to connect to the FRITZ!Box device.
     * @param password Password for the FRITZ!Box web interface.
     */
    public SessionHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Generate valid session ID using given credentials to authenticate to FRITZ!Box device.
     * @return Generated session ID.
     * @throws Exception In case an error occurs, e.g. the credentials were declined.
     */
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

    /**
     * Creates response to authentication challenge sent by FRITZ!Box device.
     * @param challenge Challenge sent by FRITZ!Box device.
     * @param password Password used to generate response hash.
     * @return Solved challenge hash to be sent back.
     */
    private String createResponse(String challenge, String password) {
        return challenge + "-" + createMD5Hash(challenge + "-" + password);
    }

    /**
     * Generates MD5 hash for given input.
     * @param input The input to be hashed.
     * @return The hashed output.
     */
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

    /**
     * Terminates ongoing sessions.
     * @throws Exception In case an error occurs, e.g. no session with given ID exists.
     */
    public void terminateConnection() throws Exception {
        String url = "http://fritz.box/login_sid.lua?logout=1&sid=" + SID;
        Document doc = Jsoup.connect(url).get();
    }
}
