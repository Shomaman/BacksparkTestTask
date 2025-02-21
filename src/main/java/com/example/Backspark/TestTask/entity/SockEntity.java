package com.example.Backspark.TestTask.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sock")
public class SockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    Integer id;
    @Column(name = "sock_color", nullable = false)
    String sockColor;
    @Column(name = "cotton", nullable = false)
    Double cotton;
    @Column(name = "quantity", nullable = false)
    Double quantity;

    public SockEntity() {
    }

    public SockEntity(String sockColor, Double cotton, Double quantity) {
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

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SockEntity that = (SockEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(sockColor, that.sockColor)
                && Objects.equals(cotton, that.cotton) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sockColor, cotton, quantity);
    }

    @Override
    public String toString() {
        return "SockEntity{" +
                "id=" + id +
                ", sockColor='" + sockColor + '\'' +
                ", cotton=" + cotton +
                ", quantity=" + quantity +
                '}';
    }
}
