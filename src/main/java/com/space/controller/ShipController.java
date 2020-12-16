package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {
    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String planet,
                                    @RequestParam(required = false) ShipType shipType,
                                    @RequestParam(required = false) Long after,
                                    @RequestParam(required = false) Long before,
                                    @RequestParam(required = false) Boolean isUsed,
                                    @RequestParam(required = false) Double minSpeed,
                                    @RequestParam(required = false) Double maxSpeed,
                                    @RequestParam(required = false) Integer minCrewSize,
                                    @RequestParam(required = false) Integer maxCrewSize,
                                    @RequestParam(required = false) Double minRating,
                                    @RequestParam(required = false) Double maxRating,
                                    @RequestParam(required = false) Integer pageNumber,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) ShipOrder order) {
        List<Ship> allShips = shipService.getAllShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.getPageListShips(shipService.sortShips(allShips, order), pageNumber, pageSize);
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String planet,
                                 @RequestParam(required = false) ShipType shipType,
                                 @RequestParam(required = false) Long after,
                                 @RequestParam(required = false) Long before,
                                 @RequestParam(required = false) Boolean isUsed,
                                 @RequestParam(required = false) Double minSpeed,
                                 @RequestParam(required = false) Double maxSpeed,
                                 @RequestParam(required = false) Integer minCrewSize,
                                 @RequestParam(required = false) Integer maxCrewSize,
                                 @RequestParam(required = false) Double minRating,
                                 @RequestParam(required = false) Double maxRating) {
        List<Ship> allShips = shipService.getAllShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return allShips.size();
    }

    @RequestMapping(path = "rest/ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (!shipService.isShipValid(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship newShip = shipService.createShip(ship);
        return new ResponseEntity<>(newShip, HttpStatus.OK);
    }

    @RequestMapping(path = "rest/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        ResponseEntity<Ship> entity = getShipById(id);
        Ship oldShip = entity.getBody();
        if (oldShip == null) {
            return entity;
        }
        Ship result;
        try {
            result = shipService.editShip(oldShip, ship);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship>  getShipById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipService.getShipById(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(path = "rest/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable Long id) {
        ResponseEntity<Ship> entity = getShipById(id);
        Ship ship = entity.getBody();
        if (ship == null) {
            return entity;
        }
        shipService.deleteShip(ship);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
