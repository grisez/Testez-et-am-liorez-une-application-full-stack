package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;

import java.util.List;

public interface SessionService {
    Session create(Session session);

    void delete(Long id);

    List<Session> findAll();

    Session getById(Long id);

    Session update(Long id, Session session);

    void participate(Long id, Long userId);

    void noLongerParticipate(Long id, Long userId);
}
