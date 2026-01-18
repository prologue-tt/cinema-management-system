package com.tt.cm.controller;
import com.tt.cm.common.response.R;
import com.tt.cm.domain.Comment;
import com.tt.cm.service.TicketService;
import com.tt.cm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class CommentController extends  BaseController {

    @Resource
    private CommentService commentService;
    @Autowired
    private TicketService ticketService;

    /**
     * 添加评论
     */
    @PostMapping("/comment")
    public R addSysComment(@RequestBody Comment comment) {

        int commentAdded= commentService.addSysComment(comment);//添加成功返回1
        if(commentAdded>0) {
            Long billId = comment.getBillId();
            ticketService.updateReviewState(billId);
        }
        return getResult(commentAdded);
    }

    /**
     * 根据电影ID查询所有评论
     */
    @GetMapping("/comments/{movieId}")
    public R findAllCommentsByMovieId(@PathVariable Long movieId) {
    //    List<SysComment> comments = sysCommentService.findAllCommentsByMovieId(movieId);
        return getResult(commentService.findAllCommentsByMovieId(movieId));
    }

    /**
     * 根据订单ID查询评论
     */
    @GetMapping("/comment/{billId}")
    public R findCommentByBillId(@PathVariable Long billId) {
        return getResult(commentService.findCommentByBillId(billId));
    }

    /**
     * 根据用户ID查询该用户的所有评论
     */
    @GetMapping("/comment/{userId}")
    public R findCommentsByUserId(@PathVariable Long userId) {

        return getResult(commentService.findCommentsByUserId(userId));
    }

    /**
     * 根据电影ID查询评论的平均星级
     */
    @GetMapping("/comment/{movieId}/average-star")
    public R findAverageStarByMovieId(@PathVariable Long movieId) {
        Double averageStar = commentService.findAverageStarByMovieId(movieId);
        return getResult(averageStar);
    }

    /**
     * 根据评论ID查询评论详情
     */
    @GetMapping("/{commentId}")
    public R findCommentById(@PathVariable Long commentId) {
        return getResult(commentService.findCommentById(commentId));
    }

    /**
     * 更新评论
     */
    @PutMapping("/comment")
    public R updateSysComment(@Validated @RequestBody Comment comment) {
        return getResult(commentService.updateSysComment(comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/commentDelete/{commentId}")
    public R deleteSysComment(@PathVariable Long commentId) {
        return getResult(commentService.deleteSysComment(commentId));
    }
}
