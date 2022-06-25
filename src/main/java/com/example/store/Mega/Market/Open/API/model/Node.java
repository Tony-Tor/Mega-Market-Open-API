package com.example.store.Mega.Market.Open.API.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    /*@Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    String id;*/
    @Column
    @NotNull
    String name;
    @Column
    @NotNull
    ZonedDateTime dateTime;
    @Column
    UUID parentId;
    @NotNull
    @Column
    NodeType type;
    @Column
    int price;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<Node> children;

    public void addParent(Node parent){
        parent.children.add(this);
        parentId = parent.getParentId();
    }

    public int calculatePrice(){
        if(type==NodeType.CATEGORY){
            price = children.stream().map(Node::calculatePrice).reduce(0, Integer::sum);
        }
        return price;
    }

    /*public UUID getIdAsUUID() {
        return UUID.fromString(id);
    }*/

    public void addChild(Node child){
        children.add(child);
        child.parentId = id;
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
