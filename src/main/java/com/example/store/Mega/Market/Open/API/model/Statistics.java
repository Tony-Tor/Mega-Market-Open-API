package com.example.store.Mega.Market.Open.API.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "statistics")
public class Statistics {
    @Id
    @GeneratedValue
    int id;
    @Column
    int price;
    @Column
    @NotNull
    ZonedDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return price == that.price && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, date);
    }
}
