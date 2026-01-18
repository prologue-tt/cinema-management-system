package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.domain.Cinema;
import com.tt.cm.domain.Movie;
import com.tt.cm.domain.Session;
import com.tt.cm.mapper.MovieMapper;
import com.tt.cm.service.CinemaService;
import com.tt.cm.service.MovieService;
import com.tt.cm.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 电影院管理控制层
 */
@RestController
public class CinemaController extends BaseController {

    @Resource
    private CinemaService cinemaService;

    @Resource
    private SessionService sessionService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieMapper movieMapper;

    /**
     * 查询影院
     * @return 影院
     */
    @GetMapping("/sysCinema")
    public R findCinema() {
        return getResult(cinemaService.findCinema());
    }
    /**
     * 更新影院信息
     *
     * @param cinema 影院信息
     * @return 结果
     */
    @PutMapping("/sysCinema/update")
    public R updateCinema(@Validated @RequestBody Cinema cinema) {
        return getResult(cinemaService.updateCinema(cinema));
    }

    /**
     * 通过id查询影院
     * @param cinemaId 电影院id
     * @param movieId  电影id
     * @return 影院
     */
    @GetMapping(value = {"/sysCinema/find/{cinemaId}/{movieId}", "/sysCinema/find/{cinemaId}"})
    public R findCinemaById(@PathVariable Long cinemaId, @PathVariable(required = false) Long movieId) {
        Cinema cinema = cinemaService.findCinemaById(cinemaId);

        if (movieId == null || movieId == 0) {
            movieId = cinema.getMovieList().size() > 0 ? cinema.getMovieList().get(0).getMovieId() : 0;
        }
        List<Session> sessions = null;
        Movie movie=null;
        if (movieId != null && movieId != 0) {
            movie = movieService.findMovieById(movieId);
            sessions = sessionService.findSessionByMovieId(movieId);
        }

//        List<SysMovie> movies = null;
//        movies=sysMovieMapper.findMovieByCinemaId(cinemaId);

        HashMap<String, Object> response = new HashMap<>();
        response.put("cinema", cinema);
        response.put("sessions", sessions);
        response.put("movie",movie);
    //    response.put("movies", movies);
        return getResult(response);
    }

}
