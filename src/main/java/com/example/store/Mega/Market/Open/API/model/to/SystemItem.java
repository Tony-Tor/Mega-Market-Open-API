package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SystemItem{
    String id = null;
    String url = null;
    String date = null;
    String parentId = null;
    String type = null;
    Integer size = null;
    List<SystemItem> children = new ArrayList<>();

}
