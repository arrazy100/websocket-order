package com.lunapos.dtos;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.EncodeException;

public class MessageEncoder implements Encoder.Text<Message> {
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(Message message) throws EncodeException {
        try {
            return mapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig)
    {

    }

    @Override
    public void destroy() {
        
    }
}
