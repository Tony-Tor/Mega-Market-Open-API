package com.example.store.Mega.Market.Open.API.repository;

import com.example.store.Mega.Market.Open.API.model.Node;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NodeRepository extends CrudRepository<Node, UUID> {
}
