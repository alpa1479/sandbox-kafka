package edu.sandbox.springbootkafka.producer.service;

import edu.sandbox.springbootkafka.producer.model.Message;

public interface MessageSender {

    void send(Message message);
}
