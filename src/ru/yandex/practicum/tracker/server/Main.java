package ru.yandex.practicum.tracker.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
