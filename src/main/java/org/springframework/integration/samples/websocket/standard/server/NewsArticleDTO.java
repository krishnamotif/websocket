package org.springframework.integration.samples.websocket.standard.server;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsArticleDTO implements Comparable<NewsArticleDTO> {

	private static AtomicInteger counter = new AtomicInteger(0);
    private String imageUrl;
    private String source;
    private String summary;
    private String title;
    private String url;
    private String videoUrl;
    private Long date;
    private Long tweetCount;
    private NewsArticleContextDTO primaryContext;
    private List<NewsArticleContextDTO> otherContexts;

    public NewsArticleDTO() {
    	primaryContext  = new NewsArticleContextDTO();
    	primaryContext.setCompanyName("Cisco System");
    	primaryContext.setOwned(true);
    	primaryContext.setOwnedValue(BigDecimal.TEN);
    	primaryContext.setTodaysChange(BigDecimal.ONE);
    	primaryContext.setTodaysChangePercentage(new BigDecimal("0.10"));
    	primaryContext.setSymbol("cisco");
    	primaryContext.setMotif(false);
    	
    	int c = counter.incrementAndGet();
    	summary = "Much fun to be had with web sockets: " + c;
    	source = "U,K and C News Sources";
    	title = "Team 'Much wOw' wins hackathon. Counter: " + c;
    	date = (new Date()).getTime();
    	tweetCount = 0L;
    	imageUrl = "http://i.imgur.com/AnMOqWc.png";
    }

    @Override
    // newest first
    public int compareTo(NewsArticleDTO o) {
        return o.getDate().compareTo(this.date);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(Long tweetCount) {
        this.tweetCount = tweetCount;
    }

    public NewsArticleContextDTO getPrimaryContext() {
        return primaryContext;
    }

    public void setPrimaryContext(NewsArticleContextDTO primaryContext) {
        this.primaryContext = primaryContext;
    }

    public List<NewsArticleContextDTO> getOtherContexts() {
        return otherContexts;
    }

    public void setOtherContexts(List<NewsArticleContextDTO> otherContexts) {
        this.otherContexts = otherContexts;
    }
}
