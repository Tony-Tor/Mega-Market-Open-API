package com.example.store.Mega.Market.Open.API.controllers;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.to.*;
import com.example.store.Mega.Market.Open.API.services.NodeService;
import com.example.store.Mega.Market.Open.API.utils.exceptions.BadRequestException;
import com.example.store.Mega.Market.Open.API.utils.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Tag(name = "Common", description = "The common nodes API")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonController {

    static private final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    NodeService service;

    @GetMapping("/")
    public RedirectView redirectRoot(){
        return new RedirectView("swagger-ui/index.html");
    }


    @Operation(summary = "Gets node by id", tags = "node")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Node by id. File or directory")})
    @GetMapping(value = "nodes/{id}")
    public Node getNode(@PathVariable UUID id){
        logger.info("Get node with UUID: " + id);
        Node node = service.get(id);
        //if(node.getChildren().size() == 0)node.setChildren(null);
        node.nullableChildren();

        return node;
    }

    @Operation(summary = "Saves the directory structure", tags = "node")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Saves the directory structure. A json object is passed in the request.")})

    @PostMapping(value = "imports")
    public void importNode(@RequestBody SystemItemImportRequest request){
        logger.info("Add " + request.getItems().size() + "items");
        ZonedDateTime updateDate = ZonedDateTime.parse(request.getUpdateDate());
        List<SystemItemImport> nodes = request.getItems();
        service.createAll(nodes, updateDate);
    }

    @Operation(summary = "Delete node", tags = "node")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete node by id")})

    @DeleteMapping(value = "delete/{id}")
    public void deleteNode(@PathVariable UUID id){
        logger.info("Delete node with UUID: " + id);
        service.delete(id);
    }

    @Operation(summary = "Get node updates for the specified date", tags = "history")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get node updates for the specified date")})

    @GetMapping(value = "updates")
    public SystemItemHistoryResponse getSales(@RequestParam String date){
        logger.info("Get sales statistic from: " + date);
        ZonedDateTime dateZ = ZonedDateTime.parse(date);
        return new SystemItemHistoryResponse(
                service.getStatisticShop(dateZ)
        );
    }

    @Operation(summary = "Get node updates history for the specified date", tags = "history")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get node updates history for the specified date")})

    @GetMapping(value = "node/{id}/history")
    public SystemItemHistoryResponse getStatistic(@PathVariable UUID id,
                                                  @RequestParam String dateStart,
                                                  @RequestParam String dateEnd){
        logger.info("Get statistic from: " + dateStart + " to: " + dateEnd);
        ZonedDateTime dateStartZ = ZonedDateTime.parse(dateStart);
        ZonedDateTime dateEndZ = ZonedDateTime.parse(dateEnd);
        return new SystemItemHistoryResponse(
                service.getStatisticFrom(id,dateStartZ,dateEndZ)
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorUnit handleException404(NotFoundException e){
        ErrorUnit error = new ErrorUnit(e.getMessage());
        error.setCode(404);
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorUnit handleException400(BadRequestException e){
        ErrorUnit error = new ErrorUnit(e.getMessage());
        error.setCode(400);
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public ErrorUnit handleException400(DateTimeParseException e){
        ErrorUnit error = new ErrorUnit(e.getMessage());
        error.setCode(400);
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorUnit handleException400(HttpMessageNotReadableException e){
        ErrorUnit error = new ErrorUnit(Objects.requireNonNull(e.getMessage()));
        error.setCode(400);
        return error;
    }
    @GetMapping(value = "test")
    public SystemItem getNode(){
        return new SystemItem();
    }


}
