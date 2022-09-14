package com.example.store.Mega.Market.Open.API.model.to;

import com.example.store.Mega.Market.Open.API.model.INode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SystemItemNullChildren implements INode {
    String id = null;
    String url = null;
    String date = null;
    String parentId = null;
    String type = null;
    Integer size = null;
    Object children = null;
}
