package com.tt.cm.mapper;

import com.tt.cm.domain.MovieCategory;
import com.tt.cm.domain.MovieToCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface MovieCategoryMapper {

    List<MovieCategory> findAllCategorys();

    MovieCategory findCategoryById(Long id);

    List<MovieCategory> findOneMovieCategorys(Long id);

    int addCategory(MovieCategory movieCategory);

    int updateCategory(MovieCategory movieCategory);

    int deleteCategory(Long id);

    int addMovieToCategory(MovieToCategory movieToCategory);

    int deleteMovieToCategory(MovieToCategory movieToCategory);

        // 统计用户电影类型偏好：返回 key=类型名，value=次数 的Map
    List<Map<String, Object>> countUserMovieTypePreference(Long userId);

}
