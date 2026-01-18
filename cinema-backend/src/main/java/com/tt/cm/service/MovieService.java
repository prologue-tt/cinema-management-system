package com.tt.cm.service;

import com.tt.cm.domain.Movie;
import com.tt.cm.domain.vo.SysMovieVo;

import java.util.List;

public interface MovieService {

    List<Movie> findAllMovies(SysMovieVo sysMovieVo);


    Movie findMovieById(Long id);

    Movie findOneMovie(Long id);

    int addMovie(Movie movie);

    int updateMovie(Movie movie);

    int deleteMovie(Long[] ids);

    List<Movie> totalBoxOfficeList();

    List<Movie> domesticBoxOfficeList();

    List<Movie> foreignBoxOfficeList();

}
