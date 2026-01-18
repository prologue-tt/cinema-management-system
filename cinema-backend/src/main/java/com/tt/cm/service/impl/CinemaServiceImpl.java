package com.tt.cm.service.impl;

import com.tt.cm.domain.Cinema;
import com.tt.cm.mapper.CinemaMapper;
import com.tt.cm.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    private CinemaMapper cinemaMapper;

    @Override
    public Cinema findCinema() {
        return cinemaMapper.findCinema();
    }

    @Override
    public int updateCinema(Cinema cinema) {
        return cinemaMapper.updateCinema(cinema);
    }

    @Override
    public Cinema findCinemaById(Long id) {
        return cinemaMapper.findCinemaById(id);
    }
}
