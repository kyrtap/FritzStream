public class Stream {
    private String name, URL, imageSource;
    private StreamType type;

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
