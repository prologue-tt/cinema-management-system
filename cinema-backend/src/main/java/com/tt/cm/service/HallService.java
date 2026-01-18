package com.tt.cm.service;

import com.tt.cm.domain.Hall;

import java.util.List;

/**
 * 观影排座服务层

 */
public interface HallService {

    List<Hall> findAllHalls(Hall hall);

    Hall findHallById(Hall hall);

    int addHall(Hall hall);

    int updateHall(Hall hall);

    int deleteHall(Hall[] hall);
}
