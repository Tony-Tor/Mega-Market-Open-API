package com.example.store.Mega.Market.Open.API.controllers;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.to.*;
import com.example.store.Mega.Market.Open.API.services.NodeService;
import com.example.store.Mega.Market.Open.API.utils.exceptions.BadRequestException;
import com.example.store.Mega.Market.Open.API.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

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

    @GetMapping(value = "sales")
    public ShopUnitStatisticResponse getSales(@RequestParam String date){
        ZonedDateTime dateZ = ZonedDateTime.parse(date);
        return new ShopUnitStatisticResponse(
                service.getStatisticShop(dateZ)
        );
    }

    @GetMapping(value = "node/{id}/statistic")
    public ShopUnitStatisticResponse getStatistic(@PathVariable UUID id,
                                                  @RequestParam String dateStart,
                                                  @RequestParam String dateEnd){
        ZonedDateTime dateStartZ = ZonedDateTime.parse(dateStart);
        ZonedDateTime dateEndZ = ZonedDateTime.parse(dateEnd);
        return new ShopUnitStatisticResponse(
                service.getStatisticFrom(id,dateStartZ,dateEndZ)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorUnit handleException404(NotFoundException e){
        ErrorUnit error = new ErrorUnit(e.getMessage());
        error.setCode(404);
        return error;
    }

    @ExceptionHandler(BadRequestException.class)
    public ErrorUnit handleException400(BadRequestException e){
        ErrorUnit error = new ErrorUnit(e.getMessage());
        error.setCode(400);
        return error;
    }





    @GetMapping(value = "test")
    public ShopUnit getNode(){
        return new ShopUnit();
    }


}
