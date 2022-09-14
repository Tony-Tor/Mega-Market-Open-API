package com.example.store.Mega.Market.Open.API.model;

import com.example.store.Mega.Market.Open.API.repository.HistoryRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "node")
public class Node{

    @Id
    UUID id;
    @Column
    @Size(max = 255)
    String url;
    @Column(name = "date_time")
    @NotNull
    //@LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    ZonedDateTime date;
    @Column
    UUID parentId;
    @NotNull
    @Column
    NodeType type;
    @Column
    Integer size;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<Node> children = null;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<History> histories = new ArrayList<>();

    public int calculatePrice(HistoryRepository historyRepository){
        if(type==NodeType.FOLDER){
            size = children.stream().map(f->f.calculatePrice(historyRepository)).reduce(0, Integer::sum);
        }

        History statistic = new History();
        statistic.setPrice(size);
        statistic.setDate(date);
        histories.add(statistic);

        historyRepository.save(statistic);

        return size;
    }

    public void addChild(Node child){
        children.add(child);
        child.parentId = id;
    }

    public void deleteStatistic(HistoryRepository historyRepository){
        children.forEach(f->f.deleteStatistic(historyRepository));
        getHistories().forEach(historyRepository::delete);
    }

    public void nullableChildren(){
        children.forEach(Node::nullableChildren);
        if(children.size() == 0) children = null;
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
