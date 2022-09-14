package com.example.store.Mega.Market.Open.API;

import com.example.store.Mega.Market.Open.API.controllers.CommonController;
import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.NodeType;
import com.example.store.Mega.Market.Open.API.model.to.SystemItemImport;
import com.example.store.Mega.Market.Open.API.model.to.SystemItemImportRequest;
import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.GeneratorStrategy;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
class CommonControllerTests {

    static private CommonController controller;

    @BeforeAll
    //@Sql("classpath:data.sql")
    static void initializeService(@Autowired CommonController controller){
        CommonControllerTests.controller = controller;
    }

    /*@Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    public void test1000request(){
        //for (int i = 0; i <= 1; i++){
            SystemItemImport itemImport = new SystemItemImport();
            UUID uuid = Generators.timeBasedGenerator().generate();
            itemImport.setId(uuid.toString());
            itemImport.setUrl("ggg");
            itemImport.setType(NodeType.FILE.toString());
            itemImport.setParentId(null);
            itemImport.setSize(0);

            ArrayList<SystemItemImport> arr = new ArrayList<>();
            arr.add(itemImport);
            String date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            SystemItemImportRequest request = new SystemItemImportRequest(arr, date);

            controller.importNode(request);
        //}
    }*/
}
