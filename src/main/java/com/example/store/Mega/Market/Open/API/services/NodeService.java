package com.example.store.Mega.Market.Open.API.services;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.NodeType;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImport;
import com.example.store.Mega.Market.Open.API.repository.NodeRepository;
import com.example.store.Mega.Market.Open.API.utils.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NodeService {

    private final NodeRepository repository;

    static private final Logger logger = LoggerFactory.getLogger(NodeService.class);

    public NodeService(NodeRepository repository) {
        this.repository = repository;
    }

    public Node get(UUID id) {
        return repository.findById(id).orElseThrow(
                ()->new NotFoundException(
                        String.format("Character not found by id: %s", id.toString())
                )
        );
    }

    public List<Node> getAll() {
        return (List<Node>) repository.findAll();
    }


    public Node create(Node obj) {
        return repository.save(obj);
    }

    public Iterable<Node> createAll(List<ShopUnitImport> nodes, ZonedDateTime updateDate) {

        List<Node> list = nodes.stream().map(f->{

            UUID id = UUID.fromString(f.getId());
            String name = f.getName();
            ZonedDateTime dateTime = updateDate;
            UUID parentId = f.getParentId()!=null?UUID.fromString(f.getParentId()):null;
            NodeType type = NodeType.valueOf(f.getType());
            int price = Objects.requireNonNullElse(f.getPrice(), 0);
            Set<Node> children = new HashSet<>();
            //children.addAll(get(id).getChildren());

            Node node = new Node();
            node.setId(id);
            node.setName(name);
            node.setDateTime(dateTime);
            node.setParentId(parentId);
            node.setType(type);
            node.setPrice(price);
            node.setChildren(children);

            return node;
        }).collect(Collectors.toList());

        logger.info(list.toString());

        repository.saveAll(list);

        Set<Node> forSave = new HashSet<>();
        forSave.addAll(list);

        list.stream().forEach(f->{
                    if(f.getParentId() == null) return;
                    Node node = list.stream().filter(n->n.getId().equals(f.getParentId())).findAny().orElse(null);
                    if (node == null) node = get(f.getParentId());
                    forSave.add(node);
                    node.addChild(f);
        });

        Set<Node> roots = new HashSet<>();

        list.stream().filter(f->f.getType().equals(NodeType.OFFER)).forEach(f-> {
            UUID parent = f.getParentId();
            Node current = f;
            while (parent!=null){
                current = get(parent);
                current.setDateTime(updateDate);
                parent = current.getParentId();
            }
            roots.add(current);
        });

        roots.stream().forEach(f->f.calculatePrice());

        return repository.saveAll(forSave);
    }

    /*@Transactional
    public Node update(int id, NodeTo obj) {
        Node node  = new Node();
        character.setName(obj.getName());
        character.setDescription(obj.getDescription());
        character.setCreated(obj.getCreated());
        character.setImage(imageService.get(obj.getImage()));
        character.setId(id);
        return repository.save(node);
    }*/

    public void delete(UUID id){
        Node f = get(id);
        UUID parent = f.getParentId();
        if(parent!=null) {
            Node parentNode = get(parent);
            parentNode.getChildren().remove(f);
            repository.save(parentNode);
        }

        Node root = f;

        while (parent!=null){
            root = get(parent);
            parent = root.getParentId();
        }

        repository.deleteById(id);

        root.calculatePrice();
    }
    /* for(int i = 0; i<list.size();i++){
                Node current = list.get(i);
                if(current.getParentId()!=null){
                    Node node = null;
                    for (int k = 0; k<list.size();k++) {
                        Node find = list.get(k);
                        UUID findUIID = find.getId();
                        UUID currentUUID = current.getParentId();
                        logger.info(findUIID + "=" + currentUUID);
                        if(findUIID.toString().equals(currentUUID.toString())){
                            node = find;
                            logger.info("t1" + node.toString());
                            break;
                        }
                    }
                    logger.info("t2" + node.toString());
                    if (node == null) node = get(current.getParentId());
                    node.addChild(current);
                    if(current.getType().equals(NodeType.OFFER)){
                        addPrice(current);
                    }
                }
        }*/
}
