package com.lunapos.dtos;

import com.lunapos.enums.MessageTypeEnum;

public class Message {
    public MessageTypeEnum type;
    public Object data;

    public Message() {

    }
    
    public Message(MessageTypeEnum type, Object data) {
        this.type = type;
        this.data = data;
    }
}
