package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ShipServiceImpl implements ShipService {
    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getAllShips(String name,
                                  String planet,
                                  ShipType shipType,
                                  Long after,
                                  Long before,
                                  Boolean isUsed,
                                  Double minSpeed,
                                  Double maxSpeed,
                                  Integer minCrewSize,
                                  Integer maxCrewSize,
                                  Double minRating,
                                  Double maxRating) {
        List<Ship> ships = shipRepository.findAll();
        List<Ship> filterShips = new ArrayList<>();
        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);
        for (Ship ship : ships) {
            if (name != null && !ship.getName().contains(name)) continue;
            if (planet != null && !ship.getPlanet().contains(planet)) continue;
            if (shipType != null && ship.getShipType() != shipType) continue;
            if (after != null && !afterDate.before(ship.getProdDate())) continue;
            if (before != null && !ship.getProdDate().before(beforeDate)) continue;
            if (isUsed != null && !(isUsed == ship.getUsed())) continue;
            if (minSpeed != null && !(minSpeed < ship.getSpeed())) continue;
            if (maxSpeed != null && !(maxSpeed > ship.getSpeed())) continue;
            if (minCrewSize != null && !(minCrewSize < ship.getCrewSize())) continue;
            if (maxCrewSize != null && !(maxCrewSize > ship.getCrewSize())) continue;
            if (minRating != null && !(minRating < ship.getRating())) continue;
            if (maxRating != null && !(maxRating > ship.getRating())) continue;
            filterShips.add(ship);
        }
        return filterShips;
    }

    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
        Comparator<Ship> comparator = null;
        if (order != null) {
            switch (order) {
                case ID:
                    comparator = Comparator.comparing(obj -> obj.getId());
                    break;
                case SPEED:
                    comparator = Comparator.comparing(obj -> obj.getSpeed());
                    break;
                case DATE:
                    comparator = Comparator.comparing(obj -> obj.getProdDate());
                    break;
                case RATING:
                    comparator = Comparator.comparing(obj -> obj.getRating());
                    break;
            }
            Collections.sort(ships, comparator);
        }
        return ships;
    }

    @Override
    public List<Ship> getPageListShips(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        int start = pageNumber * pageSize;
        int end = start + pageSize;
        if (end > ships.size()) end = ships.size();
        return ships.subList(start, end);
    }

    @Override
    public Ship getShipById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteShip(Ship ship) {
        shipRepository.delete(ship);
    }

    @Override
    public Ship createShip(Ship ship) {
        if (ship.getUsed() == null) ship.setUsed(false);

        ship.setRating(calculateRating(ship));

        return shipRepository.save(ship);
    }

    @Override
    public Ship editShip(Ship ship, Ship editShip) throws IllegalArgumentException {
        if (editShip.getName() != null) {
            if (isTextValid(editShip.getName())) {
                ship.setName(editShip.getName());
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (editShip.getPlanet() != null) {
            if (isTextValid(editShip.getPlanet())) {
                ship.setPlanet(editShip.getPlanet());
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (editShip.getShipType() != null) {
            ship.setShipType(editShip.getShipType());
        }
        if (editShip.getSpeed() != null) {
            if (isSpeedValid(editShip.getSpeed())) {
                ship.setSpeed(editShip.getSpeed());
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (editShip.getCrewSize() != null) {
            if (isСrewSizeValid(editShip.getCrewSize())) {
                ship.setCrewSize(editShip.getCrewSize());
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (editShip.getProdDate() != null) {
            if (isProdDateValid(editShip.getProdDate())) {
                ship.setProdDate(editShip.getProdDate());
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (editShip.getUsed() != null) {
            ship.setUsed(editShip.getUsed());
        }
        ship.setRating(calculateRating(ship));
        return shipRepository.save(ship);
    }

    @Override
    public boolean isShipValid(Ship ship) {
        return (ship != null && ship.getShipType() != null && isTextValid(ship.getName()) &&
                isTextValid(ship.getPlanet()) && isSpeedValid(ship.getSpeed()) &&
                isСrewSizeValid(ship.getCrewSize()) && isProdDateValid(ship.getProdDate()));
    }

    private double calculateRating(Ship ship){
        double k = ship.getUsed() ? 0.5 : 1.0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int y = calendar.get(Calendar.YEAR);

        double rating = 80 * ship.getSpeed() * k / (3019 - y + 1);

        return Math.round(rating * 100) / 100D;
    }

    private boolean isTextValid(String text) {
        return (text != null && !text.isEmpty() && text.length() <= 50);
    }

    private boolean isSpeedValid(Double speed) {
        return (speed != null && speed >= 0.01 && speed <= 0.99);
    }

    private boolean isСrewSizeValid(Integer  crewSize) {
        return (crewSize != null && crewSize >= 1 && crewSize <= 9999);
    }

    private boolean isProdDateValid(Date prodDate) {
        Calendar minDate = new GregorianCalendar(2799, 12 , 31);
        Calendar maxDate = new GregorianCalendar(3020, 0 , 1);
        return (prodDate.getTime() > 0 && prodDate.after(minDate.getTime()) && prodDate.before(maxDate.getTime()));
    }
}
