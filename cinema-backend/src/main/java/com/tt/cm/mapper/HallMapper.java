package com.tt.cm.mapper;

import com.tt.cm.domain.Hall;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 排座持久层
 */
@Mapper
public interface HallMapper {

    List<Hall> findAllHalls(Hall hall);

    Hall findHallById(Hall hall);

    int addHall(Hall hall);

    int updateHall(Hall hall);

    int deleteHall(Hall hall);
}
