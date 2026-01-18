package com.tt.cm.service;

import com.tt.cm.domain.Comment;

import java.util.List;

public interface CommentService {

    /**
     * 添加评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int addSysComment(Comment comment);

    /**
     * 根据电影ID查询所有评论
     * @param movieId 电影ID
     * @return 评论列表
     */
    List<Comment> findAllCommentsByMovieId(Long movieId);

    /**
     * 根据订单ID查询评论
     * @param billId 订单ID
     * @return 评论对象
     */
    Comment findCommentByBillId(Long billId);

    /**
     * 根据用户ID查询该用户的所有评论
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> findCommentsByUserId(Long userId);

    /**
     * 根据电影ID查询评论的平均星级
     * @param movieId 电影ID
     * @return 平均星级
     */
    Double findAverageStarByMovieId(Long movieId);

    /**
     * 根据评论ID查询评论详情
     * @param commentId 评论ID
     * @return 评论对象
     */
    Comment findCommentById(Long commentId);

    /**
     * 更新评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int updateSysComment(Comment comment);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 影响行数
     */
    int deleteSysComment(Long commentId);
}
