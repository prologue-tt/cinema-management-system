package com.tt.cm.service.impl;

import com.tt.cm.domain.Comment;
import com.tt.cm.mapper.CommentMapper;
import com.tt.cm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public int addSysComment(Comment comment) {
        // 可以添加评论前的业务逻辑，如参数校验等
        return commentMapper.addSysComment(comment);
    }

    @Override
    public List<Comment> findAllCommentsByMovieId(Long movieId) {
        return commentMapper.findAllCommentsByMovieId(movieId);
    }

    @Override
    public Comment findCommentByBillId(Long billId) {
        return commentMapper.findCommentByBillId(billId);
    }

    @Override
    public List<Comment> findCommentsByUserId(Long userId) {
        return commentMapper.findCommentsByUserId(userId);
    }

    @Override
    public Double findAverageStarByMovieId(Long movieId) {
        Double averageStar = commentMapper.findAverageStarByMovieId(movieId);
        // 如果没有评论，返回0.0
        return averageStar != null ? averageStar : 0.0;
    }

    @Override
    public Comment findCommentById(Long commentId) {
        return commentMapper.findCommentById(commentId);
    }

    @Override
    public int updateSysComment(Comment comment) {
        // 可以添加更新前的业务逻辑，如权限校验等
        return commentMapper.updateSysComment(comment);
    }

    @Override
    public int deleteSysComment(Long commentId) {
        // 可以添加删除前的业务逻辑，如权限校验等
        return commentMapper.deleteSysComment(commentId);
    }
}
