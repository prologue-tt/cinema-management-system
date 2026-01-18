package com.tt.cm.domain;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long movieId;

    //电影名称
    @NotBlank(message = "电影名称不能为空")
    private String movieName;

    //电影时长
    private Integer movieLength;

    //电影海报
    private String moviePoster;

    private String movieArea;

    //上映日期
    private Date releaseDate;
    //电影总票房
    private Double movieBoxOffice;
    //电影简介
    private String movieIntroduction;
    //电影图集
    private String moviePictures;
    private String director;
    //电影的类别
    private List<MovieCategory> movieCategoryList;
    private List<Comment> movieCommentList;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(movieId, movie.movieId) && Objects.equals(movieName, movie.movieName) && Objects.equals(movieLength, movie.movieLength) && Objects.equals(moviePoster, movie.moviePoster) && Objects.equals(movieArea, movie.movieArea) && Objects.equals(releaseDate, movie.releaseDate) && Objects.equals(movieBoxOffice, movie.movieBoxOffice) && Objects.equals(movieIntroduction, movie.movieIntroduction) && Objects.equals(moviePictures, movie.moviePictures) && Objects.equals(director, movie.director) && Objects.equals(movieCategoryList, movie.movieCategoryList) && Objects.equals(movieCommentList, movie.movieCommentList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, movieName, movieLength, moviePoster, movieArea, releaseDate, movieBoxOffice, movieIntroduction, moviePictures, director, movieCategoryList, movieCommentList);
    }



}
