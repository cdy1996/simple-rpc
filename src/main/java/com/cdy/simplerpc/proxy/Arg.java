package com.cdy.simplerpc.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Arg implements Serializable {
    public String type;
    public byte[] bytes;
}
