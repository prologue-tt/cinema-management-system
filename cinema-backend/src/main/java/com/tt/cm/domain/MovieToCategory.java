package com.tt.cm.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 存储电影与电影类别的多对多联系
 */
@Data
public class MovieToCategory implements Serializable {

    private static final Long serialVersionUID = 1L;

    private Long movieId;

    private Long movieCategoryId;


    public MovieToCategory() {
    }

    public MovieToCategory(Long movieId, Long movieCategoryId) {
        this.movieId = movieId;
        this.movieCategoryId = movieCategoryId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieToCategory that = (MovieToCategory) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(movieCategoryId, that.movieCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, movieCategoryId);
    }

}
