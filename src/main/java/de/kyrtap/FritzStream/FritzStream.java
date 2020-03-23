package de.kyrtap.FritzStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/** Representation of DVB-C streams collected from FRITZ!Box device.
 * @author kyrtap
 * @version 1.0.0
 */
public class FritzStream {
    private String username, password;
    private SessionHandler sessionHandler;

    /**
     * Creates new FritzStream instance for given FRITZ!Box credentials.
     * @param username The username for the FRITZ!Box web interface.
     * @param password The password for the FRITZ!Box web interface.
     * @throws Exception In case connection could not be established, e.g. the credentials were declined.
     */
    public FritzStream(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
        this.sessionHandler = new SessionHandler(username, password);
        sessionHandler.generateSID();
    }


    /**
     * Get list of all DVB-C streams obtained from the FRITZ!Box device.
     * @return List of Stream instances.
     */
    public ArrayList<Stream> getStreams() {
        ArrayList<Stream> ret = new ArrayList();
        try {
            for (StreamType type : StreamType.values()) {
                Document doc = Jsoup.connect("http://fritz.box/data.lua")
                        .data("xhr", "1")
                        .data("sid", sessionHandler.getSID())
                        .data("page", getParam(type))
                        .post();

                Element uiList = doc.select("div#uiList").first();
                Elements as = uiList.children();

                for (Element a : as) {
                    String link = "http://fritz.box/" + a.attr("href");
                    Element span = a.children().first();
                    String title = span.attr("title");
                    Element img = span.children().first();
                    String imgSource = img.attr("src");
                    ret.add(new Stream(title, type, link, imgSource));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * Convert StreamType to String.
     * @param type The requested stream type.
     * @return The converted result.
     */
    private String getParam(StreamType type) {
        switch (type) {
            case HDTV:
                return "tvhd";
            case SDTV:
                return "tvsd";
            case RADIO:
                return "dvbradio";
            default:
                return null;
        }
    }

    /**
     * Terminates the current session on the FRITZ!Box device.
     * @throws Exception In case session cannot be terminated, e.g. session has expired.
     */
    public void terminateConnection() throws Exception {
        sessionHandler.terminateConnection();
    }
}
