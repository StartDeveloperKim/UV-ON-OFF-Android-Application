package com.example.hello.layout;

public enum ButtonContent {
    SUBSCRIBE("구독"), UNSUBSCRIBE("취소"), REMOVE("제거");

    private final String content;

    ButtonContent(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
