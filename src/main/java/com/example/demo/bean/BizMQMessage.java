package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizMQMessage implements Serializable {

    private Integer retrySize = 0;

    private String msgId;

    private String body;

    @Override
    public String toString() {
        return "IMMQMessage{" +
                "retrySize=" + retrySize +
                ", body='" + body + '\'' +
                '}';
    }
}
