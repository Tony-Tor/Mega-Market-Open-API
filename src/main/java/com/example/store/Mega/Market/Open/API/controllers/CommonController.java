package com.example.store.Mega.Market.Open.API.controllers;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.NodeType;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnit;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImport;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImportRequest;
import com.example.store.Mega.Market.Open.API.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonController {

    @Autowired
    NodeService service;

    @GetMapping(value = "nodes/{id}")
    public Node getNode(@PathVariable UUID id){
        return service.get(id);
    }

    @PostMapping(value = "imports")
    public void importNode(@RequestBody ShopUnitImportRequest request){
        ZonedDateTime updateDate = ZonedDateTime.parse(request.getUpdateDate());
        List<ShopUnitImport> nodes = request.getItems();
        service.createAll(nodes, updateDate);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteNode(@PathVariable UUID id){
        service.delete(id);
    }

    @GetMapping(value = "test")
    public ShopUnit getNode(){
        return new ShopUnit();
    }


}
