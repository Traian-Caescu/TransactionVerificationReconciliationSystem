package org.example.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_data")
public class StockData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double open;

    @Column(nullable = false)
    private Double high;

    @Column(nullable = false)
    private Double low;

    @Column(nullable = false)
    private Double close;

    @Column(nullable = false)
    private Double adjClose;

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

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getOpen() {
        return open;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

    public Double getClose() {
        return close;
    }

    public Double getAdjClose() {
        return adjClose;
    }

    public Long getVolume() {
        return volume;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public void setAdjClose(Double adjClose) {
        this.adjClose = adjClose;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
