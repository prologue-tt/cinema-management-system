package com.tt.cm.service.impl;

import com.tt.cm.domain.Movie;
import com.tt.cm.domain.vo.SysMovieVo;
import com.tt.cm.mapper.MovieMapper;
import com.tt.cm.service.MovieService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 电影服务层实现类
 */
@Service
public class MovieServiceImpl implements MovieService {

    @Resource
    private MovieMapper movieMapper;

    @Override
    public List<Movie> findAllMovies(SysMovieVo sysMovieVo) {
        return movieMapper.findAllMovies(sysMovieVo);
    }

    @Override
    public Movie findMovieById(Long id) {
        return movieMapper.findMovieById(id);
    }

    @Override
    public Movie findOneMovie(Long id) {
        return movieMapper.findOneMovie(id);
    }

    @Override
    public int addMovie(Movie movie) {
        return movieMapper.addMovie(movie);
    }

    @Override
    public int updateMovie(Movie movie) {
        return movieMapper.updateMovie(movie);
    }

    @Override
    public int deleteMovie(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += movieMapper.deleteMovie(id);
        }
        return rows;
    }

    /**
     * 总票房榜
     *
     * @return
     */
    @Override
    public List<Movie> totalBoxOfficeList() {
        return movieMapper.totalBoxOfficeList();
    }

    /**
     * 国内票房榜 已上映的国内电影里，按票房取前10 国内电影 movieArea in (港台+大陆)
     *
     * @return
     */
    @Override
    public List<Movie> domesticBoxOfficeList() {
        return movieMapper.domesticBoxOfficeList();
    }

    /**
     * 国外票房榜 已上映的国外电影里，按票房取前10 国外电影 movieArea not in (港台+大陆)
     *
     * @return
     */
    @Override
    public List<Movie> foreignBoxOfficeList() {
        return movieMapper.foreignBoxOfficeList();
    }

}
