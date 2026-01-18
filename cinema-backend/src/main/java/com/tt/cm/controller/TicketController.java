package com.tt.cm.controller;

import com.tt.cm.common.exception.DataNotFoundException;
import com.tt.cm.common.response.R;
import com.tt.cm.common.utils.ApplicationContextUtils;
import com.tt.cm.domain.Movie;
import com.tt.cm.domain.Session;
import com.tt.cm.domain.Ticket;
import com.tt.cm.domain.vo.SysBillVo;
import com.tt.cm.service.TicketService;
import com.tt.cm.service.MovieService;
import com.tt.cm.service.SessionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单控制层

 */
@RestController
public class TicketController extends BaseController {

    @Resource
    private TicketService ticketService;

    @Resource
    private SessionService sessionService;

    @Resource
    private MovieService movieService;

    /**
     * 查询所有订单
     *
     * @param ticket 订单信息
     * @return 订单列表
     */
    @GetMapping("/sysBill")
    public R findAllBills(Ticket ticket) {
        startPage();
        // 取消所有超时订单并释放占座资源
        ApplicationContextUtils.getBean("cancelTimeoutBill");
        List<Ticket> data = ticketService.findAllBills(ticket);
        return getResult(data);
    }

    /**
     * 查询订单
     *
     * @param id 订单id
     * @return 订单信息
     */
    @GetMapping("/sysBill/{id}")
    public R findBillById(@PathVariable Long id) {
        return getResult(ticketService.findBillById(id));
    }

    /**
     * 添加订单场次
     *
     * @param sysBillVo 订单信息
     * @return 结果
     */
    @PostMapping("/sysBill")
    public R addBill(@Validated @RequestBody SysBillVo sysBillVo) {
        // 获取当前场次信息
        Session curSession = sessionService.findOneSession(sysBillVo.getTicket().getSessionId());
        if (curSession == null) {
            throw new DataNotFoundException("添加订单的场次没找到");
        }
        System.out.println(curSession.getSessionSeats());
        curSession.setSessionSeats(sysBillVo.getSessionSeats());

        int addSallNums = sysBillVo.getTicket().getSeats().split(",").length;
        curSession.setSallNums(curSession.getSallNums() + addSallNums);
        // 更新场次座位信息
        sessionService.updateSession(curSession);

        Object obj = ticketService.addBill(sysBillVo.getTicket());
        if (obj instanceof Integer) {
            return getResult(obj);
        }
        return getResult(obj);
    }

    /**
     * 支付订单
     *
     * @param ticket 订单
     * @return 结果
     */
    @PutMapping("/sysBill")
    public R pay(@RequestBody Ticket ticket) {
        int rows = ticketService.updateBill(ticket);
        if (rows > 0 && ticket.getPayState()) {
            //更新场次的座位状态
            Session curSession = sessionService.findOneSession(ticket.getSessionId());
            if (curSession == null) {
                throw new DataNotFoundException("支付订单的场次没找到");
            }
            //更新电影票房
            Movie curMovie = movieService.findOneMovie(curSession.getMovieId());
            if (curMovie == null) {
                throw new DataNotFoundException("支付订单的电影没找到");
            }
            //订单的座位数
            int seatNum = ticket.getSeats().split(",").length;
            double price = curSession.getSessionPrice();
            curMovie.setMovieBoxOffice(curMovie.getMovieBoxOffice() + seatNum * price);
            movieService.updateMovie(curMovie);
        }
        return getResult(rows);
    }

    /**
     * 取消订单
     *
     * @param sysBillVo 订单信息
     * @return 结果
     */
    @PutMapping("/sysBill/cancel")
    public R cancel(@RequestBody SysBillVo sysBillVo) {
        // 订单取消，更新订单状态
        int rows = ticketService.updateBill(sysBillVo.getTicket());
        if (rows > 0 && sysBillVo.getTicket().getCancelState()) {
            // 订单取消座位不再占用，更新场次的座位状态
            Session curSession = sessionService.findOneSession(sysBillVo.getTicket().getSessionId());
            // 取消的订单座位数
            int cancelSallNums = sysBillVo.getTicket().getSeats().split(",").length;
            curSession.setSallNums(curSession.getSallNums() - cancelSallNums);
            if (curSession == null) {
                throw new DataNotFoundException("添加订单的场次没找到");
            }
            curSession.setSessionSeats(sysBillVo.getSessionSeats());
            sessionService.updateSession(curSession);
        }
        return getResult(rows);
    }

    /**
     * 删除订单
     *
     * @param ids 订单id
     * @return 结构
     */
    @DeleteMapping("/sysBill/{ids}")
    public R deleteBill(@PathVariable Long[] ids) {
        return getResult(ticketService.deleteBill(ids));
    }

}
