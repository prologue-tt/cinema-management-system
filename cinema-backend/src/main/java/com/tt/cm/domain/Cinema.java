package com.tt.cm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class Cinema implements Serializable {

    private final static Long serialVersionUID = 1L;

    private Long cinemaId;

    @NotBlank(message = "影院名称不能为空")
    private String cinemaName;

    private String hallCategoryList;

    private String cinemaPicture;

    private String cinemaAddress;

    private String cinemaPhone;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private String workStartTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private String workEndTime;

    //当前影院上映的所有电影
    private List<Movie> movieList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cinema cinema = (Cinema) o;
        return Objects.equals(cinemaId, cinema.cinemaId) && Objects.equals(cinemaName, cinema.cinemaName) && Objects.equals(hallCategoryList, cinema.hallCategoryList) && Objects.equals(cinemaPicture, cinema.cinemaPicture) && Objects.equals(cinemaAddress, cinema.cinemaAddress) && Objects.equals(cinemaPhone, cinema.cinemaPhone) && Objects.equals(workStartTime, cinema.workStartTime) && Objects.equals(workEndTime, cinema.workEndTime) && Objects.equals(movieList, cinema.movieList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cinemaId, cinemaName, hallCategoryList, cinemaPicture, cinemaAddress, cinemaPhone, workStartTime, workEndTime, movieList);
    }

}
