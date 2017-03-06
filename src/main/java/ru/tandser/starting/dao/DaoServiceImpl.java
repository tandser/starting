package ru.tandser.starting.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Message;
import javax.jms.TextMessage;

import static java.lang.String.format;

@Repository
@Transactional(readOnly = true)
public class DaoServiceImpl implements DaoService {

    private static final String TEMPLATE = "%08x : %s";

    private static final String INFO     = "svd";
    private static final String WARN     = "ign";
    private static final String ERROR    = "rbk";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SimpleJdbcInsert insertIntoHeaders;
    private SimpleJdbcInsert insertIntoPayloads;

    @Autowired
    public DaoServiceImpl(JdbcTemplate jdbcTemplate) {
        insertIntoHeaders = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("headers")
                .usingGeneratedKeyColumns("id");

        insertIntoPayloads = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("payloads")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Message message) throws Exception {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;

                MapSqlParameterSource headers = new MapSqlParameterSource();
                headers.addValue("jmscorrelationid", textMessage.getJMSCorrelationID());
                headers.addValue("jmsdeliverymode",  textMessage.getJMSDeliveryMode());
                headers.addValue("jmsdestination",   String.valueOf(textMessage.getJMSDestination()));
                headers.addValue("jmsexpiration",    textMessage.getJMSExpiration());
                headers.addValue("jmsmessageid",     textMessage.getJMSMessageID());
                headers.addValue("jmspriority",      textMessage.getJMSPriority());
                headers.addValue("jmsredelivered",   textMessage.getJMSRedelivered());
                headers.addValue("jmsreplyto",       String.valueOf(textMessage.getJMSReplyTo()));
                headers.addValue("jmstimestamp",     textMessage.getJMSTimestamp());
                headers.addValue("jmstype",          textMessage.getJMSType());

                Number headersId = insertIntoHeaders.executeAndReturnKey(headers);

                MapSqlParameterSource payloads = new MapSqlParameterSource();
                payloads.addValue("headers_id", headersId.intValue());
                payloads.addValue("body",       textMessage.getText());

                insertIntoPayloads.execute(payloads);

                logger.info(format(TEMPLATE, message.hashCode(), INFO));
            } else {
                logger.warn(format(TEMPLATE, message.hashCode(), WARN));
            }
        } catch (Exception exc) {
            logger.error(format(TEMPLATE, message.hashCode(), ERROR), exc);
            throw exc;
        }
    }
}