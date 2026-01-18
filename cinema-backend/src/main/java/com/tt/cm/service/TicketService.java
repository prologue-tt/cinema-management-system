package com.tt.cm.service;

import com.tt.cm.domain.Ticket;

import java.util.List;
public interface TicketService {

    List<Ticket> findAllBills(Ticket ticket);

    Ticket findBillById(Long id);

    Object addBill(Ticket ticket);

    int updateBill(Ticket ticket);

    int deleteBill(Long[] ids);
    int updateReviewState(Long billId);
    List<Ticket> findTimeoutBill();

}
