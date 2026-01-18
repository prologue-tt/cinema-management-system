package com.tt.cm.service;

import com.tt.cm.domain.MovieCategory;
import com.tt.cm.domain.MovieToCategory;

import java.util.List;
import java.util.Map;

/**
 * 电影分类服务层
 */
public interface MovieCategoryService {

    List<MovieCategory> findAllCategorys();

    MovieCategory findCategoryById(Long id);

    int addCategory(MovieCategory movieCategory);

    int updateCategory(MovieCategory movieCategory);

    int deleteCategory(Long[] ids);

    int addMovieToCategory(MovieToCategory movieToCategory);

    int deleteMovieToCategory(MovieToCategory movieToCategory);

    Map<String, Integer> countUserMovieTypePreference(Long userId);
}
