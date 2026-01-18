package com.tt.cm.domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Data
public class Comment {
    private Long commentId;
    private Integer star;
    private String commentContent;
    private Date commentTime;

    @NotNull(message = "评论电影不能为空")
    private Long movieId;
    @NotNull(message = "评论所属订单不能为空")
    private Long billId;
    @NotNull(message = "评论用户订单不能为空")
    private Long userId;
    //多表连接

    private Movie movie;
    private User user;
    private Ticket ticket;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return Objects.equals(commentId, that.commentId) && Objects.equals(star, that.star) && Objects.equals(commentContent, that.commentContent) && Objects.equals(commentTime, that.commentTime) && Objects.equals(movieId, that.movieId) && Objects.equals(billId, that.billId) && Objects.equals(userId, that.userId) && Objects.equals(movie, that.movie) && Objects.equals(user, that.user) && Objects.equals(ticket, that.ticket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, star, commentContent, commentTime, movieId, billId, userId, movie, user, ticket);
    }
}
