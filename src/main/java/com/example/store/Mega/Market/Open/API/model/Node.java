package com.example.store.Mega.Market.Open.API.model;

import com.example.store.Mega.Market.Open.API.repository.StatisticsRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "node")
public class Node {

    @Id
    //@GenericGenerator(name = "uuid2", strategy = "uuid2")
    //@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    UUID id; // Пока не понял как создавать UUID вбд
    @Column
    @NotNull
    String name;
    @Column
    @NotNull
    //@LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    ZonedDateTime dateTime;
    @Column
    UUID parentId;
    @NotNull
    @Column
    NodeType type;
    @Column
    int price;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<Node> children;
    @JsonIgnore
    @OneToMany(mappedBy="node", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Statistics> statistics = new ArrayList<>();

    public int calculatePrice(StatisticsRepository statisticsRepository){
        if(type==NodeType.CATEGORY){
            price = children.stream().map(f->f.calculatePrice(statisticsRepository)).reduce(0, Integer::sum);
        }

        //if (statistics == null) statistics;

        Statistics statistic = new Statistics();
        statistic.setPrice(price);
        statistic.setDate(dateTime);
        statistics.add(statistic);

        statisticsRepository.save(statistic);

        return price;
    }

    public void addChild(Node child){
        children.add(child);
        child.parentId = id;
    }

    public void deleteStatistic(StatisticsRepository statisticsRepository){
        children.stream().forEach(f->f.deleteStatistic(statisticsRepository));
        getStatistics().stream().forEach(f->statisticsRepository.delete(f));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
