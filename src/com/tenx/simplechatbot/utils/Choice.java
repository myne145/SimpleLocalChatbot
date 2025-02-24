package com.tenx.simplechatbot.utils;

public class Choice {
    private final int index;
    private final String role;
    private final String message;

    public Choice(int index, String role, String message) {
        this.index = index;
        this.role = role;
        this.message = message;
    }
}
