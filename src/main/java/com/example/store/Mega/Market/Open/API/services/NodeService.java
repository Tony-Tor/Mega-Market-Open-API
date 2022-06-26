package com.example.store.Mega.Market.Open.API.services;

import com.example.store.Mega.Market.Open.API.model.Node;
import com.example.store.Mega.Market.Open.API.model.NodeType;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitImport;
import com.example.store.Mega.Market.Open.API.model.to.ShopUnitStatisticUnit;
import com.example.store.Mega.Market.Open.API.repository.NodeRepository;
import com.example.store.Mega.Market.Open.API.repository.StatisticsRepository;
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
    private final StatisticsRepository statisticsRepository;

    static private final Logger logger = LoggerFactory.getLogger(NodeService.class);

    public NodeService(NodeRepository repository, StatisticsRepository statisticsRepository) {
        this.repository = repository;
        this.statisticsRepository = statisticsRepository;
    }

    public Node get(UUID id) throws NotFoundException{
        return repository.findById(id).orElseThrow(
                ()->new NotFoundException(
                        String.format("Character not found by id: %s", id)
                )
        );
    }

    public List<Node> getAll() {
        return (List<Node>) repository.findAll();
    }


    public void createAll(List<ShopUnitImport> nodes, ZonedDateTime updateDate) throws NotFoundException, BadRequestException{

        List<Node> list = nodes.stream().map(f->{
            Node node = new Node();
            try {
                UUID id = UUID.fromString(f.getId());
                String name = f.getName();
                UUID parentId = f.getParentId()!=null?UUID.fromString(f.getParentId()):null;
                NodeType type = NodeType.valueOf(f.getType());
                int price = Objects.requireNonNullElse(f.getPrice(), 0);
                Set<Node> children = new HashSet<>();
                //children.addAll(get(id).getChildren());

                node.setId(id);
                node.setName(name);
                node.setDateTime(updateDate);
                node.setParentId(parentId);
                node.setType(type);
                node.setPrice(price);
                node.setChildren(children);
            } catch (Exception e){
                throw new BadRequestException("No valid json data: " + e.getMessage() + " " + e);
            }

            try{
                node.setStatistics(get(node.getId()).getStatistics());
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

        roots.forEach(f->f.calculatePrice(statisticsRepository));

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

        statisticsRepository.deleteAll(f.getStatistics());
        repository.deleteById(id);

        root.calculatePrice(statisticsRepository);
    }

    public List<ShopUnitStatisticUnit> getStatisticShop(ZonedDateTime dateTime){
        List<Node> list = getAll();
        return list.stream().flatMap(f->{
            List<ShopUnitStatisticUnit> shopList = f.getStatistics().stream().map(k->{
                ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit();
                statisticUnit.setId(f.getId().toString());
                statisticUnit.setName(f.getName());
                statisticUnit.setDateTime(k.getDate().toString());
                statisticUnit.setParentId(f.getParentId() + "");
                statisticUnit.setType(f.getType().toString());
                statisticUnit.setPrice(k.getPrice());
                return statisticUnit;
            }).collect(Collectors.toList());
            return shopList.stream();
        }).filter(f->{
            ZonedDateTime filter = ZonedDateTime.parse(f.getDateTime());
            ZonedDateTime dateTimeMinusDay = dateTime.minusHours(24);
            return filter.isBefore(dateTime)&&filter.isAfter(dateTimeMinusDay);
        }).collect(Collectors.toList());
    }

    public List<ShopUnitStatisticUnit> getStatisticFrom(UUID id, ZonedDateTime dateStart, ZonedDateTime dateEnd) throws NotFoundException{
        Node node = get(id);
        return node.getStatistics().stream().map(k->{
                ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit();
                statisticUnit.setId(node.getId().toString());
                statisticUnit.setName(node.getName());
                statisticUnit.setDateTime(k.getDate().toString());
                statisticUnit.setParentId(node.getParentId() + "");
                statisticUnit.setType(node.getType().toString());
                statisticUnit.setPrice(k.getPrice());
                return statisticUnit;
            }).filter(f->{
                ZonedDateTime filter = ZonedDateTime.parse(f.getDateTime());
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
