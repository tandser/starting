package ru.tandser.starting.dao;

import javax.jms.Message;

public interface DaoService {

    void add(Message message) throws Exception;
}