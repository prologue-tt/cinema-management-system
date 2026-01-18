package com.tt.cm.domain.vo;

import com.tt.cm.domain.Ticket;
import lombok.Data;

import java.io.Serializable;

/**
 * 接收前端增加订单的数据
 */
@Data
public class SysBillVo implements Serializable {

    //订单信息
    private Ticket ticket;

    //若成功更新后场次的座位信息
    private String sessionSeats;

}
