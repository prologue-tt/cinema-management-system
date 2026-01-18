package com.tt.cm.service.impl;

import com.tt.cm.domain.Hall;
import com.tt.cm.mapper.HallMapper;
import com.tt.cm.service.HallService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class HallServiceImpl implements HallService {

    @Resource
    private HallMapper hallMapper;

    @Override
    public List<Hall> findAllHalls(Hall hall) {
        return hallMapper.findAllHalls(hall);
    }

    @Override
    public Hall findHallById(Hall hall) {
        return hallMapper.findHallById(hall);
    }

    @Override
    public int addHall(Hall hall) {
        return hallMapper.addHall(hall);
    }

    @Override
    public int updateHall(Hall hall) {
        return hallMapper.updateHall(hall);
    }

    @Override
    public int deleteHall(Hall[] halls) {
        int rows = 0;
        for (Hall hall : halls) {
            rows += hallMapper.deleteHall(hall);
        }
        return rows;
    }
}
