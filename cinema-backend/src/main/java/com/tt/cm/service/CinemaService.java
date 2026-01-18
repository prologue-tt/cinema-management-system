package com.tt.cm.service;

import com.tt.cm.domain.Cinema;


/**
 * 电影院服务层接口

 */
public interface CinemaService {

    Cinema findCinema();

    int updateCinema(Cinema cinema);

    Cinema findCinemaById(Long id);

}
