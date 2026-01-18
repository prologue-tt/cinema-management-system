package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.domain.Hall;
import com.tt.cm.service.HallService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 观影排座控制层

 */
@RestController
public class HallController extends BaseController {

    @Resource
    private HallService hallService;

    /**
     * 查询影院排座
     *
     * @param hall 排座信息
     * @return 结果
     */
    @GetMapping("/sysHall")
    public R findAllHalls(Hall hall) {
        startPage();
        return getResult(hallService.findAllHalls(hall));
    }

    /**
     * 通过id查询查询影院排座
     *
     * @param hallId 排座id
     * @return 结构
     */
    @GetMapping("/sysHall/{hallId}")
    public R findHallById(@PathVariable Long hallId) {
        Hall hall = new Hall();
        hall.setHallId(hallId);
        return getResult(hallService.findHallById(hall));
    }

    /**
     * 添加排座
     *
     * @param hall 影院排座
     * @return 结果
     */
    @PostMapping("/sysHall")
    public R addHall(@Validated @RequestBody Hall hall) {
        return getResult(hallService.addHall(hall));
    }

    /**
     * 更新排座
     *
     * @param hall 影院排座
     * @return 结果
     */
    @PutMapping("/sysHall")
    public R updateHall(@Validated @RequestBody Hall hall) {
        int rows = hallService.updateHall(hall);
        return getResult(rows);
    }

    /**
     * 删除排座
     *
     * @param halls 影院排座
     * @return 结果
     */
    @PostMapping("/sysHall/delete")
    public R deleteHall(@RequestBody Hall[] halls) {
        return getResult(hallService.deleteHall(halls));
    }

}
