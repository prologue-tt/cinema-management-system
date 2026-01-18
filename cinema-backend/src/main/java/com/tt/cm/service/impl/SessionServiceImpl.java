package com.tt.cm.service.impl;

import com.tt.cm.domain.Session;
import com.tt.cm.domain.vo.SysSessionVo;
import com.tt.cm.mapper.SessionMapper;
import com.tt.cm.service.SessionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    @Resource
    private SessionMapper sessionMapper;

    @Override
    public List<Session> findByVo(SysSessionVo sysSessionVo) {
        return sessionMapper.findByVo(sysSessionVo);
    }

    @Override
    public List<Session> findSessionByMovieIdOrHallId(Session session) {
        return sessionMapper.findSessionByMovieIdOrHallId(session);
    }

    @Override
    public Session findSessionById(Long id) {
        return sessionMapper.findSessionById(id);
    }

    @Override
    public Session findOneSession(Long id) {
        return sessionMapper.findOneSession(id);
    }

    @Override
    public int addSession(Session session) {
        return sessionMapper.addSession(session);
    }

    @Override
    public int updateSession(Session session) {
        return sessionMapper.updateSession(session);
    }

    @Override
    public int deleteSession(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += sessionMapper.deleteSession(id);
        }
        return rows;
    }

    @Override
    public List<Session> findSessionByMovieId(Long movieId) {
        return sessionMapper.findSessionByMovieId(movieId);
    }
}
