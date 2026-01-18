package com.tt.cm.mapper;

import com.tt.cm.domain.Movie;
import com.tt.cm.domain.vo.SysMovieVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<Movie> findAllMovies(SysMovieVo sysMovieVo);

    Movie findMovieById(Long id);

    Movie findOneMovie(Long id);

    int addMovie(Movie movie);

    int updateMovie(Movie movie);

    int deleteMovie(Long id);

    List<Movie> findMovieByCinemaId(Long id);

    List<Movie> totalBoxOfficeList();

    List<Movie> domesticBoxOfficeList();

    List<Movie> foreignBoxOfficeList();

}
