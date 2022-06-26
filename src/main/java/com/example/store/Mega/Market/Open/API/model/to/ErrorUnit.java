package com.example.store.Mega.Market.Open.API.model.to;

import lombok.Data;
import lombok.NonNull;

@Data
public class ErrorUnit {
    int code;
    @NonNull
    String message;
}
