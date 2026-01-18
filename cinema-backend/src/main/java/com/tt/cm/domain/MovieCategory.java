package com.tt.cm.domain;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
@Data
public class MovieCategory implements Serializable {

    //序列号
    private static final long serialVersionUID = 1L;

    //电影分类id
    private Long movieCategoryId;

    //电影分类名称
    @NotBlank(message = "电影分类名称不能为空")
    private String movieCategoryName;

    public MovieCategory() {
    }

    public MovieCategory(Long movieCategoryId, String movieCategoryName) {
        this.movieCategoryId = movieCategoryId;
        this.movieCategoryName = movieCategoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCategory that = (MovieCategory) o;
        return Objects.equals(movieCategoryId, that.movieCategoryId) && Objects.equals(movieCategoryName, that.movieCategoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieCategoryId, movieCategoryName);
    }

}
