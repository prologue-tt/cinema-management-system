package com.tt.cm.controller;

import com.tt.cm.common.constant.MovieRankingList;
import com.tt.cm.common.response.R;
import com.tt.cm.domain.Movie;
import com.tt.cm.domain.vo.SysMovieVo;
import com.tt.cm.service.CommentService;
import com.tt.cm.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
@RestController
public class MovieController extends BaseController {

    @Resource
    private MovieService movieService;
    @Autowired
    private CommentService commentService;

    /**
     * 查询所有电影
     *
     * @param sysMovieVo 查询条件
     * @return 电影
     */
    @GetMapping("/sysMovie/find")
    public R findAllMovies(SysMovieVo sysMovieVo) {
        startPage();
        List<Movie> data = movieService.findAllMovies(sysMovieVo);
        return getResult(data);
    }

    /**
     * 通过id查询电影

     */
    @GetMapping("/sysMovie/find/{id}")
    public R findMovieById(@PathVariable Long id) {
        return getResult(movieService.findMovieById(id));
    }

    /**
     * 添加电影
     *
     * @param movie 电影信息
     * @return 结果
     */
    @PostMapping("/sysMovie")
    public R addMovie(@Validated @RequestBody Movie movie) {
        return getResult(movieService.addMovie(movie));
    }

    /**
     * 更新电影
     *
     * @param movie 电影信息
     * @return 结果
     */
    @PutMapping("/sysMovie")
    public R updateMovie(@Validated @RequestBody Movie movie) {
        return getResult(movieService.updateMovie(movie));
    }

    /**
     * 删除电影
     *
     * @param ids 电影id
     * @return 结果
     */
    @DeleteMapping("/sysMovie/{ids}")
    public R deleteMovie(@PathVariable Long[] ids) {
        return getResult(movieService.deleteMovie(ids));
    }

    /**
     * 查询电影榜单
     *
     * @param listId 榜单id
     * @return 榜单
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/sysMovie/find/rankingList/{listId}")
    public R findRankingList(@PathVariable Integer listId) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        if (listId <= 0 || listId > 4) {
            //暂时只支持4种榜单
            return R.error("抱歉，暂时只支持3种榜单，id为[1,3]");
        }
        Method getList = movieService.getClass().getMethod(MovieRankingList.listNames[listId - 1]);
        startPage();
        List<Movie> data = (List<Movie>) getList.invoke(movieService);
        return getResult(data);
    }

}
