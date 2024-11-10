package org.example.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "stock_data")
public class StockData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Open price is required")
    @Column(nullable = false)
    private Double open;

    @NotNull(message = "High price is required")
    @Column(nullable = false)
    private Double high;

    @NotNull(message = "Low price is required")
    @Column(nullable = false)
    private Double low;

    @NotNull(message = "Close price is required")
    @Column(nullable = false)
    private Double close;

    @NotNull(message = "Adjusted close price is required")
    @Column(nullable = false)
    private Double adjClose;

    @NotNull(message = "Volume is required")
    @Column(nullable = false)
    private Long volume;

    // Default constructor
    public StockData() {}

    // Parameterized constructor
    public StockData(LocalDate date, Double open, Double high, Double low, Double close, Double adjClose, Long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjClose = adjClose;
        this.volume = volume;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(Double adjClose) {
        this.adjClose = adjClose;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}