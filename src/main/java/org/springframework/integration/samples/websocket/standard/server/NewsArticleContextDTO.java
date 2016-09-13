package org.springframework.integration.samples.websocket.standard.server;

import java.math.BigDecimal;

public class NewsArticleContextDTO {

    private boolean isMotif;
    private boolean isOwned;
    private boolean isWatched;
    private BigDecimal todaysChangePercentage;
    // Owned only
    private BigDecimal todaysChange;
    private BigDecimal ownedValue;
    // Motif properties
    private boolean isCustom;
    private String motifUrl;
    private String motifName;
    // Stock properties
    private String companyName;
    private String symbol;

    public NewsArticleContextDTO() {
    }

    public boolean isMotif() {
        return isMotif;
    }

    public void setMotif(boolean isMotif) {
        this.isMotif = isMotif;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean isOwned) {
        this.isOwned = isOwned;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean isWatched) {
        this.isWatched = isWatched;
    }

    public BigDecimal getTodaysChange() {
        return todaysChange;
    }

    public void setTodaysChange(BigDecimal todaysChange) {
        this.todaysChange = todaysChange;
    }

    public BigDecimal getTodaysChangePercentage() {
        return todaysChangePercentage;
    }

    public void setTodaysChangePercentage(BigDecimal todaysChangePercentage) {
        this.todaysChangePercentage = todaysChangePercentage;
    }

    public BigDecimal getOwnedValue() {
        return ownedValue;
    }

    public void setOwnedValue(BigDecimal ownedValue) {
        this.ownedValue = ownedValue;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getMotifUrl() {
        return motifUrl;
    }

    public void setMotifUrl(String motifUrl) {
        this.motifUrl = motifUrl;
    }

    public String getMotifName() {
        return motifName;
    }

    public void setMotifName(String motifName) {
        this.motifName = motifName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}