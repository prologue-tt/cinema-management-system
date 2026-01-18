package com.tt.cm.service;

import com.tt.cm.domain.Session;
import com.tt.cm.domain.vo.SysSessionVo;

import java.util.List;

/**
 * 电影场次服务层
 */
public interface SessionService {

    List<Session> findByVo(SysSessionVo sysSessionVo);

    List<Session> findSessionByMovieIdOrHallId(Session session);

    Session findSessionById(Long id);

    Session findOneSession(Long id);

    int addSession(Session session);

    int updateSession(Session session);

    int deleteSession(Long[] id);

    List<Session> findSessionByMovieId(Long movieId);

}
