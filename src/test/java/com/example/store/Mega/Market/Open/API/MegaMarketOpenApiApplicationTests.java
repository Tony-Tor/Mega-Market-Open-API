package com.example.store.Mega.Market.Open.API;

import com.example.store.Mega.Market.Open.API.controllers.CommonController;
import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImport;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImportRequest;
import com.example.store.Mega.Market.Open.API.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CommonControllerTests {

	static private CommonController controller;

	static String json1 = """
			{
			  "items": [
			    {
			      "type": "CATEGORY",
			      "name": "Товары",
			      "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
			      "parentId": null
			    }
			  ],
			  "updateDate": "2022-02-01T12:00:00.000Z"
			}
			""";
	static String json2 = """
			{
			  "items": [
			    {
			      "type": "CATEGORY",
			      "name": "Смартфоны",
			      "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			      "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
			    },
			    {
			      "type": "OFFER",
			      "name": "jPhone 13",
			      "id": "863e1a7a-1304-42ae-943b-179184c077e3",
			      "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			      "price": 79999
			    },
			    {
			      "type": "OFFER",
			      "name": "Xomiа Readme 10",
			      "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
			      "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			      "price": 59999
			    }
			  ],
			  "updateDate": "2022-02-02T12:00:00.000Z"
			}
			""";
	static String json3 = """
			{
			  "items": [
			    {
			      "type": "CATEGORY",
			      "name": "Телевизоры",
			      "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			      "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
			    },
			    {
			      "type": "OFFER",
			      "name": "Samson 70\\" LED UHD Smart",
			      "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
			      "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			      "price": 32999
			    },
			    {
			      "type": "OFFER",
			      "name": "Phyllis 50\\" LED UHD Smarter",
			      "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
			      "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			      "price": 49999
			    }
			  ],
			  "updateDate": "2022-02-03T12:00:00.000Z"
			}
			""";
	static String json4 = """
			{
			  "items": [
			    {
			      "type": "OFFER",
			      "name": "Goldstar 65\\" LED UHD LOL Very Smart",
			      "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
			      "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			      "price": 69999
			    }
			  ],
			  "updateDate": "2022-02-03T15:00:00.000Z"
			}
			""";

	static String expectedJson = """
			{
			  "type": "CATEGORY",
			  "name": "Товары",
			  "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
			  "price": 58599,
			  "parentId": null,
			  "date": "2022-02-03T15:00:00.000Z",
			  "children": [
			    {
			      "type": "CATEGORY",
			      "name": "Телевизоры",
			      "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			      "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
			      "price": 50999,
			      "date": "2022-02-03T15:00:00.000Z",
			      "children": [
			        {
			          "type": "OFFER",
			          "name": "Samson 70\\" LED UHD Smart",
			          "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
			          "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			          "price": 32999,
			          "date": "2022-02-03T12:00:00.000Z",
			          "children": null
			        },
			        {
			          "type": "OFFER",
			          "name": "Phyllis 50\\" LED UHD Smarter",
			          "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
			          "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			          "price": 49999,
			          "date": "2022-02-03T12:00:00.000Z",
			          "children": null
			        },
			        {
			          "type": "OFFER",
			          "name": "Goldstar 65\\" LED UHD LOL Very Smart",
			          "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
			          "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
			          "price": 69999,
			          "date": "2022-02-03T15:00:00.000Z",
			          "children": null
			        }
			      ]
			    },
			    {
			      "type": "CATEGORY",
			      "name": "Смартфоны",
			      "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			      "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
			      "price": 69999,
			      "date": "2022-02-02T12:00:00.000Z",
			      "children": [
			        {
			          "type": "OFFER",
			          "name": "jPhone 13",
			          "id": "863e1a7a-1304-42ae-943b-179184c077e3",
			          "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			          "price": 79999,
			          "date": "2022-02-02T12:00:00.000Z",
			          "children": null
			        },
			        {
			          "type": "OFFER",
			          "name": "Xomiа Readme 10",
			          "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
			          "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
			          "price": 59999,
			          "date": "2022-02-02T12:00:00.000Z",
			          "children": null
			        }
			      ]
			    }
			  ]
			}
			""";

	@BeforeAll
	@Sql("classpath:data.sql")
	static void initializeService(@Autowired CommonController controller){
		CommonControllerTests.controller = controller;

		extracted(json1);
		extracted(json2);
		extracted(json3);
		extracted(json4);
	}

	@Test
	void importNodeTest() {



		UUID id = UUID.fromString("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");

		Node node = controller.getNode(id);

		Assertions.assertEquals("Товары", node.getName());
		Assertions.assertEquals("2022-02-01T12:00:00.000Z", node.getDateTime().withZoneSameInstant(ZoneId.of("GMT")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
	}

	private static void extracted(String json1) {
		ShopUnitImportRequest shopUnitImportRequest = null;

		try {
			JSONObject object = new JSONObject(json1);
			JSONArray array = object.getJSONArray("items");

			List<ShopUnitImport> items = new ArrayList<>();
			for(int i = 0; i<array.length(); i++){
				JSONObject item = array.getJSONObject(i);
				ShopUnitImport shopUnitImport = new ShopUnitImport();
				shopUnitImport.setId(item.getString("id"));
				shopUnitImport.setName(item.getString("name"));
				try {
					shopUnitImport.setPrice(item.getInt("price"));
				} catch (JSONException e){
					shopUnitImport.setPrice(0);
				}
				shopUnitImport.setType(item.getString("type"));
				String parentId = item.getString("parentId");
				shopUnitImport.setParentId(!parentId.equals("null")?parentId:null);

				items.add(shopUnitImport);
			}

			String date = object.getString("updateDate");

			shopUnitImportRequest = new ShopUnitImportRequest(items, date);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println(shopUnitImportRequest);

		controller.importNode(Objects.requireNonNull(shopUnitImportRequest));
	}

	@Test
	void hierarchyTest(){
		Node node = controller.getNode(UUID.fromString("73bc3b36-02d1-4245-ab35-3106c9ee1c65"));

		Node parent1 = controller.getNode(node.getParentId());

		Node parent2 = controller.getNode(parent1.getParentId());

		UUID nULL = parent2.getParentId();

		Assertions.assertEquals("Goldstar 65\" LED UHD LOL Very Smart", node.getName());
		Assertions.assertEquals("Телевизоры", parent1.getName());
		Assertions.assertEquals("Товары", parent2.getName());
		Assertions.assertNull(nULL);
	}

	@Test
	void deleteTest(){
		controller.deleteNode(UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2"));
		Assertions.assertThrows(NotFoundException.class,()-> controller.getNode(UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2")));
		Assertions.assertThrows(NotFoundException.class,()-> controller.getNode(UUID.fromString("73bc3b36-02d1-4245-ab35-3106c9ee1c65")));
	}


}
