package com.tt.cm.service.impl;

import com.tt.cm.domain.Ticket;
import com.tt.cm.mapper.TicketMapper;
import com.tt.cm.service.TicketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单服务层实现类
 */
@Service
public class TicketServiceImpl implements TicketService {

    @Resource
    private TicketMapper ticketMapper;

    @Override
    public List<Ticket> findAllBills(Ticket ticket) {
        return ticketMapper.findAllBills(ticket);
    }

    @Override
    public Ticket findBillById(Long id) {
        return ticketMapper.findBillById(id);
    }

    @Override
    public Object addBill(Ticket ticket) {
        int rows = ticketMapper.addBill(ticket);
        return rows > 0 ? ticket : rows;
    }

    @Override
    public int updateBill(Ticket ticket) {
        return ticketMapper.updateBill(ticket);
    }

    @Override
    public int updateReviewState(Long billId) {
        return ticketMapper.updateReviewState(billId);
    }
    @Override
    public int deleteBill(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += ticketMapper.deleteBill(id);
        }
        return rows;
    }

    @Override
    public List<Ticket> findTimeoutBill() {
        return ticketMapper.findTimeoutBill();
    }

}
