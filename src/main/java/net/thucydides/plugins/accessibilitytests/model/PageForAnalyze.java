package net.thucydides.plugins.accessibilitytests.model;

public class PageForAnalyze {

    private String url;
    private String pageSource;

    public PageForAnalyze(String url, String pageSource) {
        this.url = url;
        this.pageSource = pageSource;
    }

    public String getUrl() {
        return url;
    }

    public String getPageSource() {
        return pageSource;
    }
}