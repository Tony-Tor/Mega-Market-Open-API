package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShopUnitImport {
    String id = null;
    String name = null;
    String parentId = null;
    String type = null;
    int price = 0;
}
