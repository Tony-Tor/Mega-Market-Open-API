package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;

import java.util.Objects;

@Data
public class SystemItemHistoryUnit {
    String id = null;
    String url = null;
    String date = null;
    String parentId = null;
    String type = null;
    Integer size = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemItemHistoryUnit that = (SystemItemHistoryUnit) o;
        return id.equals(that.id) && url.equals(that.url) && date.equals(that.date) && Objects.equals(parentId, that.parentId) && type.equals(that.type) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, date, parentId, type, size);
    }
}
