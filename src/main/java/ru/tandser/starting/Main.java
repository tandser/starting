package ru.tandser.starting;

import org.springframework.context.support.GenericXmlApplicationContext;
import ru.tandser.starting.jms.Producer;

import static java.lang.String.format;

public class Main {
    public static void main(String[] args) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:spring/dao.xml", "classpath:spring/jms.xml");
        context.refresh();
        Producer producer = context.getBean(Producer.class);
        for (int i = 1; i <= 10; i++) {
            producer.send(format("Message # %d from %s", i, producer.toString()));
        }
    }
}