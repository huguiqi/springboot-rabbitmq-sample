package com.example.demo.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class IMMQMessage<T>  implements Serializable {

    private Integer retrySize = 0;

    private T body;


}
