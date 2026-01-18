package com.tt.cm.mapper;

import com.tt.cm.domain.Ticket;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface TicketMapper {

    List<Ticket> findAllBills(Ticket ticket);

    Ticket findBillById(Long id);

    int addBill(Ticket ticket);

    int updateBill(Ticket ticket);

    int deleteBill(Long id);

    List<Ticket> findTimeoutBill();
    int updateReviewState(Long billId);

}
