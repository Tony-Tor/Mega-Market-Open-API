package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class SystemItemHistoryResponse {
    @NonNull
    List<SystemItemHistoryUnit> items;
}
