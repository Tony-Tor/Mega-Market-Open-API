package com.example.store.Mega.Market.Open.API.repository;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.Statistics;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StatisticsRepository extends CrudRepository<Statistics, Integer> {
}
