package org.example.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class OptionDTO {

    @NotBlank(message = "Symbol is mandatory")
    private String symbol;

    @NotBlank(message = "Symbol type is mandatory")
    private String symbolType;

    @NotBlank(message = "Symbol name is mandatory")
    private String symbolName;

    private String hasOptions; // Boolean represented as "Yes" or "No"

    @NotNull(message = "Last price is required")
    @Positive(message = "Last price must be a positive value")
    private double lastPrice;

    private double priceChange;
    private String percentChange;
    private long volume;
    private long marketCap;
    private double peRatio;

    @NotBlank(message = "Options total volume is required")
    private String optionsTotalVolume;

    private String optionsCallVolumePercent;

    // Default constructor
    public OptionDTO() {}

    // Parameterized constructor
    public OptionDTO(String symbol, String symbolType, String symbolName, String hasOptions,
                     double lastPrice, double priceChange, String percentChange,
                     String optionsTotalVolume, String optionsCallVolumePercent) {
        this.symbol = symbol;
        this.symbolType = symbolType;
        this.symbolName = symbolName;
        this.hasOptions = hasOptions;
        this.lastPrice = lastPrice;
        this.priceChange = priceChange;
        this.percentChange = percentChange;
        this.optionsTotalVolume = optionsTotalVolume;
        this.optionsCallVolumePercent = optionsCallVolumePercent;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getHasOptions() {
        return hasOptions;
    }

    public void setHasOptions(String hasOptions) {
        this.hasOptions = hasOptions;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public String getOptionsTotalVolume() {
        return optionsTotalVolume;
    }

    public void setOptionsTotalVolume(String optionsTotalVolume) {
        this.optionsTotalVolume = optionsTotalVolume;
    }

    public String getOptionsCallVolumePercent() {
        return optionsCallVolumePercent;
    }

    public void setOptionsCallVolumePercent(String optionsCallVolumePercent) {
        this.optionsCallVolumePercent = optionsCallVolumePercent;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(long marketCap) {
        this.marketCap = marketCap;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public double getPrice() {
        return lastPrice;
    }

    public void setPrice(double price) {
        this.lastPrice = price;
    }

    public double getChange() {
        return priceChange;
    }

    public void setChange(double change) {
        this.priceChange = change;
    }

    // Override toString() method for better logging
    @Override
    public String toString() {
        return "OptionDTO{" +
                "symbol='" + symbol + '\'' +
                ", symbolType='" + symbolType + '\'' +
                ", symbolName='" + symbolName + '\'' +
                ", hasOptions='" + hasOptions + '\'' +
                ", lastPrice=" + lastPrice +
                ", priceChange=" + priceChange +
                ", percentChange='" + percentChange + '\'' +
                ", volume=" + volume +
                ", marketCap=" + marketCap +
                ", peRatio=" + peRatio +
                ", optionsTotalVolume='" + optionsTotalVolume + '\'' +
                ", optionsCallVolumePercent='" + optionsCallVolumePercent + '\'' +
                '}';
    }
}
