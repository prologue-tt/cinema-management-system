package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.domain.MovieCategory;
import com.tt.cm.domain.MovieToCategory;
import com.tt.cm.service.MovieCategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 电影分类控制层
 */
@RestController
public class MovieCategoryController extends BaseController {

    @Resource
    private MovieCategoryService movieCategoryService;

    /**
     * 查询所有分类
     *
     * @return 电影分类
     */
    @GetMapping("/sysMovieCategory/find")
    public R findAllCategorys() {
        startPage();
        List<MovieCategory> data = movieCategoryService.findAllCategorys();
        return getResult(data);
    }

    /**
     * 通过id查询分类
     *
     * @param id 分类id
     * @return 分类
     */
    @GetMapping("/sysMovieCategory/{id}")
    public R findCategoryById(@PathVariable Long id) {
        return getResult(movieCategoryService.findCategoryById(id));
    }

    /**
     * 添加电影分类
     *
     * @param movieCategory 电影分类信息
     * @return 结果
     */
    @PostMapping("/sysMovieCategory")
    public R addCategory(@Validated @RequestBody MovieCategory movieCategory) {
        return getResult(movieCategoryService.addCategory(movieCategory));
    }

    /**
     * 更新电影分类
     *
     * @param movieCategory 电影分类信息
     * @return 结果
     */
    @PutMapping("/sysMovieCategory")
    public R updateCategory(@Validated @RequestBody MovieCategory movieCategory) {
        return getResult(movieCategoryService.updateCategory(movieCategory));
    }

    /**
     * 删除电影分类
     *
     * @param ids 电影分类id
     * @return 结果
     */
    @DeleteMapping("/sysMovieCategory/{ids}")
    public R deleteCategory(@PathVariable Long[] ids) {
        return getResult(movieCategoryService.deleteCategory(ids));
    }

    /**
     * 添加电影到指定分类
     *
     * @param movieId         电影id
     * @param movieCategoryId 分类id
     * @return 结果
     */
    @PostMapping("/sysMovieToCategory/{movieId}/{movieCategoryId}")
    public R addMovieToCategory(@PathVariable Long movieId, @PathVariable Long movieCategoryId) {
        return getResult(movieCategoryService.addMovieToCategory(new MovieToCategory(movieId, movieCategoryId)));
    }

    /**
     * 删除电影从指定分类
     *
     * @param movieId         电影id
     * @param movieCategoryId 分类id
     * @return 结果
     */
    @DeleteMapping("/sysMovieToCategory/{movieId}/{movieCategoryId}")
    public R deleteMovieToCategory(@PathVariable Long movieId, @PathVariable Long movieCategoryId) {
        return getResult(movieCategoryService.deleteMovieToCategory(new MovieToCategory(movieId,
                movieCategoryId)));
    }
    // GET请求：传入用户ID，返回类型偏好统计
    @GetMapping("/sysMovieToCategorys/{userId}")
    public R getUserMovieTypePreference(@PathVariable Long userId) {
        Map<String, Integer> data = movieCategoryService.countUserMovieTypePreference
                (userId);
        // 统一返回格式（可自定义Result类，包含code、msg、data）
        return  getResult(data);
    }

}
