import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class FritzStream {
    private String username, password;
    private SessionHandler sessionHandler;

    public FritzStream(String username, String password) throws Exception {
        this.username = username;
        this.password = password;
        this.sessionHandler = new SessionHandler(username, password);
        sessionHandler.generateSID();
    }


    public ArrayList<Stream> getStreams() {
        ArrayList<Stream> ret = new ArrayList<>();
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

    public void terminateConnection() throws Exception {
        sessionHandler.terminateConnection();
    }
}
