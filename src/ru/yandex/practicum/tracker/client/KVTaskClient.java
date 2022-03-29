package ru.yandex.practicum.tracker.client;

import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient httpClient;
    private String url;
    private String apiKey;
    private static final int PORT = 8078;
    private static final String HOST = "localhost";

    public static void main(String[] args) {

        String url = "http://" + HOST + ":" + PORT;
        KVTaskClient kvTaskClient = new KVTaskClient(url);
        kvTaskClient.register();
        String key = "data";
        String json = "[{\"taskName\": \"Задача 1\",\"taskDescription\": \"Собрание в 14:00\",\"taskId\": 56,\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\": \"15.03.2022 10:30\"},{\"taskName\": \"Задача 2\",\"taskDescription\": \"Вынести мусор\",\"taskId\": 57,\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\": \"15.03.2022 12:30\"},{\"subTasks\": [59,60,61],\"taskName\": \"Эпик 1\",\"taskDescription\": \"Отпраздновать новый год\",\"taskId\": 58,\"taskStatus\": \"IN_PROGRESS\",\"duration\": 15,\"startTime\": \"14.03.2022 02:30\"},{\"epicId\": 58,\"taskName\": \"Подзадача 1\",\"taskDescription\": \"Купить подарки\",\"taskId\": 59,\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\": \"15.03.2022 13:30\"},{\"epicId\": 58,\"taskName\": \"Подзадача 2\",\"taskDescription\": \"Пригласить друзей\",\"taskId\": 60,\"taskStatus\": \"IN_PROGRESS\",\"duration\": 5,\"startTime\": \"15.03.2022 15:30\"},{\"epicId\": 58,\"taskName\": \"Подзадача 3\",\"taskDescription\": \"За продуктами\",\"taskId\": 61,\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\": \"14.03.2022 02:30\"},{\"subTasks\": [],\"taskName\": \"Эпик 2\",\"taskDescription\": \"Убраться в квартире\",\"taskId\": 62,\"taskStatus\": \"NEW\",\"duration\": 0,\"startTime\": \"29.03.2022 16:38\"}]";
        kvTaskClient.put(key, json);
        key = "history";
        json = "[60,62,57,56]";
        kvTaskClient.put(key, json);

        key = "data";
        String jsonResponse = kvTaskClient.load(key);
        System.out.println(jsonResponse);
        key = "history";
        jsonResponse = kvTaskClient.load(key);
        System.out.println(jsonResponse);

        key = "history";
        json = "[60,57,56]";
        kvTaskClient.put(key, json);
        jsonResponse = kvTaskClient.load(key);
        System.out.println(jsonResponse);
    }

    public KVTaskClient(String url) {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
    }

    void register() {
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiKey = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    String load(String key) {
        String jsonResponse = null;
        URI uri = URI.create(url + "/load/" + key + "?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                if (!jsonElement.isJsonArray()) {
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                } else {
                    jsonResponse = jsonElement.getAsJsonArray().toString();
                }
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return jsonResponse;
    }
}
