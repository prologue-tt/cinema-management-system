package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.common.utils.ApplicationContextUtils;
import com.tt.cm.domain.Session;
import com.tt.cm.domain.vo.SysSessionVo;
import com.tt.cm.service.SessionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SessionController extends BaseController {

    @Resource
    private SessionService sessionService;

    /**
     * 查询所有场次 带查询条件
     *
     * @param sysSessionVo 场次信息
     * @return 结果
     */
    @GetMapping("/sysSession")
    public R findByQuery(SysSessionVo sysSessionVo) {
        startPage();
        List<Session> list = sessionService.findByVo(sysSessionVo);
        return getResult(list);
    }

    /**
     * 通过id查询场次
     *
     * @param id 场次id
     * @return 结果
     */
    @GetMapping("/sysSession/find/{id}")
    public R findSessionById(@PathVariable Long id) {
        // 取消所有超时订单并释放占座资源
        ApplicationContextUtils.getBean("cancelTimeoutBill");
        return getResult(sessionService.findSessionById(id));
    }

    /**
     * 通过电影id,排座id查询场次
     *
     * @param session 场次信息
     * @return 结果
     */
    @GetMapping("/sysSession/isAbleEdit")
    public R findSessionByMovieIdOrHallId(Session session) {
        return getResult(sessionService.findSessionByMovieIdOrHallId(session));
    }

    /**
     * 添加场次
     *
     * @param session 场次信息
     * @return 结果
     */
    @PostMapping("/sysSession")
    public R addSession(@RequestBody Session session) {
        return getResult(sessionService.addSession(session));
    }

    /**
     * 更新场次
     *
     * @param session 场次信息
     * @return 结果
     */
    @PutMapping("/sysSession")
    public R updateSession(@RequestBody Session session) {
        return getResult(sessionService.updateSession(session));
    }

    /**
     * 删除场次
     *
     * @param ids 场次id
     * @return 结果
     */
    @DeleteMapping("/sysSession/{ids}")
    public R deleteSession(@PathVariable Long[] ids) {
        return getResult(sessionService.deleteSession(ids));
    }

}
