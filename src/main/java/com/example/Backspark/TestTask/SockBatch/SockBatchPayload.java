package com.example.Backspark.TestTask.SockBatch;

public class SockBatchPayload {

    private String sockColor;
    private Double cotton;
    private Double quantity;

    public SockBatchPayload() {
    }

    public SockBatchPayload(String sockColor, Double cotton, Double quantity) {
        this.sockColor = sockColor;
        this.cotton = cotton;
        this.quantity = quantity;
    }

    public String getSockColor() {
        return sockColor;
    }

    public void setSockColor(String sockColor) {
        this.sockColor = sockColor;
    }

    public Double getCotton() {
        return cotton;
    }

    public void setCotton(Double cotton) {
        this.cotton = cotton;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "SockPayload{" +
                "sockColor='" + sockColor + '\'' +
                ", cotton=" + cotton +
                ", quantity=" + quantity +
                '}';
    }
}
