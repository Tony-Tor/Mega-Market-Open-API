package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;

import java.util.Objects;

@Data
public class ShopUnitStatisticUnit {
    String id = null;
    String name = null;
    String dateTime = null;
    String parentId = null;
    String type = null;
    Integer price = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopUnitStatisticUnit that = (ShopUnitStatisticUnit) o;
        return id.equals(that.id) && name.equals(that.name) && dateTime.equals(that.dateTime) && Objects.equals(parentId, that.parentId) && type.equals(that.type) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateTime, parentId, type, price);
    }
}
