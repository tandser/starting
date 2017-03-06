package ru.tandser.starting.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tandser.starting.dao.DaoService;

import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class Consumer implements MessageListener {

    private DaoService daoService;

    @Autowired
    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            daoService.add(message);
        } catch (Exception ignored) {}
    }
}