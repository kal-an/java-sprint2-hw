import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.server.HttpTaskServer;
import ru.yandex.practicum.tracker.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//класс тестирования эндпоинт
class HTTPTaskManagerTest {

    static KVServer kvServer;
    static HttpClient client;

    @BeforeAll
    public static void createServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer.start();
        client = HttpClient.newHttpClient();
    }

    //задачи по приоритету
    @Test
    public void checkTasksEndpoint() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        //GET
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //Other
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    //задачи
    @Test
    public void checkTaskEndpoint() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = "{\"taskName\": \"Задача 1\",\"taskDescription\": \"Собрание в 14:00\",\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\":\"19.03.2022 14:00\"}";
        //POST
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //POST
        json = "";
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //GET
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //DELETE
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //Other
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    //подзадачи
    @Test
    public void checkSubTaskEndpoint() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = "{\"epicId\": 2,\"taskName\": \"Подзадача 1\",\"taskDescription\": \"За продуктами\",\"taskStatus\": \"NEW\",\"duration\": 5,\"startTime\":\"14.03.2022 18:40\"}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //POST
        json = "";
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //DELETE
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //Other
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //GET
        url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
    }

    //эпики
    @Test
    public void checkEpicEndpoint() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = "{\"taskName\": \"Эпик 1\",\"taskDescription\": \"Отпраздновать новый год\",\"taskStatus\": \"NEW\"}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //POST
        json = "";
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //GET
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //Other
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());

        //DELETE
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    //задачи по приоритету
    @Test
    public void checkHistoryEndpoint() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");
        //GET
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        //Other
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }
}