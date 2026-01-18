package com.tt.cm.mapper;

import com.tt.cm.domain.Cinema;
import org.apache.ibatis.annotations.Mapper;

/**
 * 影院持久层
 */
@Mapper
public interface CinemaMapper {

    Cinema findCinema();

    int updateCinema(Cinema cinema);

    Cinema findCinemaById(Long id);

}
