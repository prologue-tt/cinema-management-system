package com.tt.cm.mapper;

import com.tt.cm.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    // 添加评论
    int addSysComment(Comment comment);

    // 根据电影ID查询所有评论
    List<Comment> findAllCommentsByMovieId(Long movieId);

    // 根据订单ID查询评论
    Comment findCommentByBillId(Long billId);

    // 根据用户ID查询该用户的所有评论
    List<Comment> findCommentsByUserId(Long userId);

    // 根据电影ID查询评论的平均星级
    Double findAverageStarByMovieId(Long movieId);

    // 根据评论ID查询评论详情
    Comment findCommentById(Long commentId);

    // 更新评论
    int updateSysComment(Comment comment);

    // 删除评论
    int deleteSysComment(Long commentId);
}