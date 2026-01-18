package com.tt.cm.mapper;

import com.tt.cm.domain.Session;
import com.tt.cm.domain.vo.SysSessionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 电影场次持久层
 */
@Mapper
public interface SessionMapper {

    List<Session> findByVo(SysSessionVo sysSessionVo);

    List<Session> findSessionByMovieIdOrHallId(Session session);

    Session findSessionById(Long id);

    Session findOneSession(Long id);

    int addSession(Session session);

    int updateSession(Session session);

    int deleteSession(Long id);

    List<Session> findSessionByMovieId(Long movieId);

}
