package com.example.store.Mega.Market.Open.API.services;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.NodeType;
import com.example.store.Mega.Market.Open.API.model.to.SystemItemImport;
import com.example.store.Mega.Market.Open.API.model.to.SystemItemHistoryUnit;
import com.example.store.Mega.Market.Open.API.repository.NodeRepository;
import com.example.store.Mega.Market.Open.API.repository.HistoryRepository;
import com.example.store.Mega.Market.Open.API.utils.exceptions.BadRequestException;
import com.example.store.Mega.Market.Open.API.utils.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NodeService {

    private final NodeRepository repository;
    private final HistoryRepository historyRepository;

    static private final Logger logger = LoggerFactory.getLogger(NodeService.class);

    public NodeService(NodeRepository repository, HistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public Node get(UUID id) throws NotFoundException{

        Node node = repository.findById(id).orElseThrow(
                ()->new NotFoundException(
                        String.format("Character not found by id: %s", id)
                )
        );

        /*if(node.getChildren().size() == 0){
            node.setChildren(null);
        }*/

        return node;
    }

    public List<Node> getAll() {
        return (List<Node>) repository.findAll();
    }


    public void createAll(List<SystemItemImport> nodes, ZonedDateTime updateDate) throws NotFoundException, BadRequestException{

        List<Node> list = nodes.stream().map(f->{
            Node node = new Node();
            try {
                UUID id = UUID.fromString(f.getId());
                String name = f.getUrl();
                UUID parentId = f.getParentId()!=null?UUID.fromString(f.getParentId()):null;
                NodeType type = NodeType.valueOf(f.getType());
                int price = Objects.requireNonNullElse(f.getSize(), 0);
                Set<Node> children = new HashSet<>();
                //children.addAll(get(id).getChildren());

                node.setId(id);
                node.setUrl(name);
                node.setDate(updateDate);
                node.setParentId(parentId);
                node.setType(type);
                node.setSize(price);
                node.setChildren(children);
            } catch (Exception e){
                throw new BadRequestException("No valid json data: " + e.getMessage() + " " + e);
            }

            try{
                node.setHistories(get(node.getId()).getHistories());
            }catch (NotFoundException e){
                // костыль №???
            }

            return node;
        }).collect(Collectors.toList());

        logger.info(list.toString());

        repository.saveAll(list);

        Set<Node> forSave = new HashSet<>(list);

        list.forEach(f->{
                    if(f.getParentId() == null) return;
                    Node node = list.stream().filter(n->n.getId().equals(f.getParentId())).findAny().orElse(null);
                    if (node == null) node = get(f.getParentId());
                    forSave.add(node);
                    node.addChild(f);
        });

        Set<Node> roots = new HashSet<>();

        list.stream().filter(f->f.getType().equals(NodeType.FILE)).forEach(f-> {
            UUID parent = f.getParentId();
            Node current = f;
            while (parent!=null){
                current = get(parent);
                current.setDate(updateDate);
                parent = current.getParentId();
            }
            roots.add(current);
        });

        roots.forEach(f->f.calculatePrice(historyRepository));

        repository.saveAll(forSave);
    }

    public void delete(UUID id) throws NotFoundException{
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

        f.deleteStatistic(historyRepository);

        root.calculatePrice(historyRepository);


    }

    public List<SystemItemHistoryUnit> getStatisticShop(ZonedDateTime dateTime){
        List<Node> list = getAll();
        return list.stream().flatMap(f->{
            List<SystemItemHistoryUnit> shopList = f.getHistories().stream().map(k->{
                SystemItemHistoryUnit statisticUnit = new SystemItemHistoryUnit();
                statisticUnit.setId(f.getId().toString());
                statisticUnit.setUrl(f.getUrl());
                statisticUnit.setDate(k.getDate().toString());
                statisticUnit.setParentId(f.getParentId() + "");
                statisticUnit.setType(f.getType().toString());
                statisticUnit.setSize(k.getPrice());
                return statisticUnit;
            }).collect(Collectors.toList());
            return shopList.stream();
        }).filter(f->{
            ZonedDateTime filter = ZonedDateTime.parse(f.getDate());
            ZonedDateTime dateTimeMinusDay = dateTime.minusHours(24);
            return filter.isBefore(dateTime)&&filter.isAfter(dateTimeMinusDay);
        }).collect(Collectors.toList());
    }

    public List<SystemItemHistoryUnit> getStatisticFrom(UUID id, ZonedDateTime dateStart, ZonedDateTime dateEnd) throws NotFoundException{
        Node node = get(id);
        return node.getHistories().stream().map(k->{
                SystemItemHistoryUnit statisticUnit = new SystemItemHistoryUnit();
                statisticUnit.setId(node.getId().toString());
                statisticUnit.setUrl(node.getUrl());
                statisticUnit.setDate(k.getDate().toString());
                statisticUnit.setParentId(node.getParentId() + "");
                statisticUnit.setType(node.getType().toString());
                statisticUnit.setSize(k.getPrice());
                return statisticUnit;
            }).filter(f->{
                ZonedDateTime filter = ZonedDateTime.parse(f.getDate());
                return filter.isBefore(dateEnd)&&filter.isAfter(dateStart);
            }).collect(Collectors.toList());
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
