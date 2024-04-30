package etu2024.framework.utility;

public class MappingUrl {
    String url;
    String method;

    public MappingUrl(String url, String method) {
        setUrl(url);
        setMethod(method);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
