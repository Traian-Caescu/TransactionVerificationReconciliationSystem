package org.example.dto;

public class OptionDTO {
    private String symbol; // e.g., "NVDA"
    private String symbolType; // e.g., "1" (type of the symbol)
    private String symbolName; // e.g., "Nvidia Corp" (full name of the symbol)
    private String hasOptions; // Change to String to accept "Yes" or "No"
    private double lastPrice; // e.g., 148.88 (the last price of the option)
    private double priceChange; // e.g., 3.27 (price change from previous close)
    private String percentChange; // e.g., "+2.25%" (percentage change)
    private String optionsTotalVolume; // e.g., "3,439,734" (total volume of options traded)
    private String optionsCallVolumePercent; // e.g., "63.79%" (percentage of call options)

    // Default constructor
    public OptionDTO() {}

    // Parameterized constructor
    public OptionDTO(String symbol, String symbolType, String symbolName, String hasOptions,
                     double lastPrice, double priceChange, String percentChange,
                     String optionsTotalVolume, String optionsCallVolumePercent) {
        this.symbol = symbol;
        this.symbolType = symbolType;
        this.symbolName = symbolName;
        this.hasOptions = hasOptions; // Keep as String
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
        return hasOptions; // Return as String
    }

    public void setHasOptions(String hasOptions) {
        this.hasOptions = hasOptions; // Set as String
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
                ", optionsTotalVolume='" + optionsTotalVolume + '\'' +
                ", optionsCallVolumePercent='" + optionsCallVolumePercent + '\'' +
                '}';
    }
}
