package de.kyrtap.FritzStream;

/** Represents a single DVB-C stream.
 * @author kyrtap
 * @version 1.0.0
 */
public class Stream {
    private String name, URL, imageSource;
    private StreamType type;

    /**
     * Create stream with specified name, type, URL and logo image.
     * @param name The stream name.
     * @param type The stream type.
     * @param URL The URL address linking to the stream.
     * @param imageSource A logo image representing the stream.
     */
    public Stream(String name, StreamType type, String URL, String imageSource) {
        this.name = name;
        this.type = type;
        this.URL = URL;
        this.imageSource = imageSource;
    }

    @Override
    public String toString() {
        return "Name: \"" + name + "\", Type: " + type.toString() + ", URL: " + URL + ", Image: " + imageSource;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public StreamType getType() {
        return type;
    }

    public String getImageSource() {
        return imageSource;
    }
}
