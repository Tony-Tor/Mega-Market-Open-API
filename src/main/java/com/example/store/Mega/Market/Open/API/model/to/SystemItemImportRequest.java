package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class SystemItemImportRequest {

    @NonNull
    List<SystemItemImport> items;
    @NonNull
    String updateDate;

}
