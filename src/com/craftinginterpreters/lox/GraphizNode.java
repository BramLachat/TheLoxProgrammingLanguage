package com.craftinginterpreters.lox;

import java.util.UUID;

public class GraphizNode {
    private String uuid;
    private String content;

    public GraphizNode(String uuid, String content) {
        this.uuid = uuid;
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public String getContent() {
        return content;
    }
}
