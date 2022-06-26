package com.example.store.Mega.Market.Open.API.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

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
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
