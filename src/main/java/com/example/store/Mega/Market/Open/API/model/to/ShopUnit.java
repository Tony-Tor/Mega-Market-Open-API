package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShopUnit {
    String id = null;
    String name = null;
    String dateTime = null;
    String parentId = null;
    String type = null;
    Integer price = null;
    List<ShopUnit> children = new ArrayList<>();

}
