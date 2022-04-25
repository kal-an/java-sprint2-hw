import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.server.HttpTaskServer;
import ru.yandex.practicum.tracker.server.KVServer;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;
import ru.yandex.practicum.tracker.utils.DurationAdapter;
import ru.yandex.practicum.tracker.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

//класс тестирования эндпоинт
class HTTPTaskManagerTest {

    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;
    private HttpTaskServer taskServer;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    @DisplayName("Создание менеджера, сервера")
    public void createTaskManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        TaskManager taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(8080, taskManager);

        task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(30),
                LocalDateTime.of(2020, 3, 15, 14, 0));
        task1.setTaskId(1L);

        task2 = new Task("Задача 2", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 4, 15, 10, 30));
        task2.setTaskId(2L);

        epic1 = new Epic("Эпик 1", "Отпраздновать новый год");
        epic1.setTaskId(3L);

        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                epic1.getTaskId(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2020, 12, 15, 13, 30));
        subTask1.setTaskId(4L);

        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 12, 17, 12, 0));
        subTask2.setTaskId(5L);

        epic2 = new Epic("Эпик 2", "Убраться в квартире");
        epic2.setTaskId(6L);
    }

    public HttpResponse<String> sendPostRequest(String json, String url) throws IOException,
            InterruptedException {
        URI uri = URI.create(url);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendGetRequest(String url) throws IOException,
            InterruptedException {
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendDeleteRequest(String url) throws IOException,
            InterruptedException {
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendPutRequest(String json, String url) throws IOException,
            InterruptedException {
        URI uri = URI.create(url);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void stop() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    @DisplayName("Эндпоинт для задач по приоритету")
    public void checkTasksEndpoint() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/";
        // GET
        HttpResponse<String> response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        // Other
        response = sendDeleteRequest(url);
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт добавления задачи")
    public void checkEndpointAddTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/task/";
        task1.setTaskId(null);
        String json = gson.toJson(task1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        json = "";
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт обновления задачи")
    public void checkEndpointUpdateTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/task/";
        task1.setTaskId(null);
        String json = gson.toJson(task1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        Task task = gson.fromJson(response.body(), Task.class);

        json = gson.toJson(task);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        task1.setTaskId(43534L);
        json = gson.toJson(task1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для получения задачи")
    public void checkEndpointGetTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/task/";
        task1.setTaskId(null);
        String json = gson.toJson(task1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Task task = gson.fromJson(response.body(), Task.class);

        url = "http://localhost:8080/tasks/task/?id=" + task.getTaskId();
        response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/task/?id=6876";
        response = sendGetRequest(url);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт не существует для задач")
    public void checkEndpointPutTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/task/";
        HttpResponse<String> response = sendPutRequest("json", url);
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для удаления задачи")
    public void checkEndpointDeleteTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/task/";
        task1.setTaskId(null);
        String json = gson.toJson(task1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Task task = gson.fromJson(response.body(), Task.class);

        url = "http://localhost:8080/tasks/task/?id=" + task.getTaskId();
        response = sendDeleteRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/task/?id=4564564345";
        response = sendDeleteRequest(url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт добавления эпика")
    public void checkEndpointAddEpic() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        json = "";
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт обновления эпика")
    public void checkEndpointUpdateEpic() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        json = gson.toJson(epic);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        epic1.setTaskId(435345L);
        json = gson.toJson(epic1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для получения эпика")
    public void checkEndpointGetEpic() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Epic epic = gson.fromJson(response.body(), Epic.class);

        url = "http://localhost:8080/tasks/epic/?id=" + epic.getTaskId();
        response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/epic/?id=6786";
        response = sendGetRequest(url);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт не существует для эпика")
    public void checkEndpointPutEpic() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        HttpResponse<String> response = sendPutRequest("json", url);
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для удаления эпика")
    public void checkEndpointDeleteEpic() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Epic epic = gson.fromJson(response.body(), Epic.class);

        url = "http://localhost:8080/tasks/epic/?id=" + epic.getTaskId();
        response = sendDeleteRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/epic/?id=4564564345";
        response = sendDeleteRequest(url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт добавления подзадач")
    public void checkEndpointAddSubtask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        url = "http://localhost:8080/tasks/subtask/";
        subTask1.setTaskId(null);
        subTask1.setEpicId(epic.getTaskId());
        json = gson.toJson(subTask1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        json = "";
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт обновления подзадач")
    public void checkEndpointUpdateSubTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        url = "http://localhost:8080/tasks/subtask/";
        subTask1.setTaskId(null);
        subTask1.setEpicId(epic.getTaskId());
        json = gson.toJson(subTask1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        subTask1.setTaskId(subTask.getTaskId());
        json = gson.toJson(subTask1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        subTask1.setTaskId(576434L);
        json = gson.toJson(subTask1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для получения подзадачи")
    public void checkEndpointGetSubTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/subtask/";
        subTask1.setTaskId(null);
        subTask1.setEpicId(3L);
        json = gson.toJson(subTask1);

        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/subtask/?id=4";
        response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/subtask/epic/?id=3";
        response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/epic/?id=6786";
        response = sendGetRequest(url);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для удаления подзадачи")
    public void checkEndpointDeleteSubTask() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/epic/";
        epic1.setTaskId(null);
        String json = gson.toJson(epic1);
        HttpResponse<String> response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        url = "http://localhost:8080/tasks/subtask/";
        subTask1.setTaskId(null);
        subTask1.setEpicId(epic.getTaskId());
        json = gson.toJson(subTask1);
        response = sendPostRequest(json, url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        url = "http://localhost:8080/tasks/subtask/?id=" + subTask.getTaskId();
        response = sendDeleteRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        url = "http://localhost:8080/tasks/subtask/?id=4564564345";
        response = sendDeleteRequest(url);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    @DisplayName("Эндпоинт для истории задач")
    public void checkHistoryEndpoint() throws IOException, InterruptedException {
        String url = "http://localhost:8080/tasks/history";
        // GET
        HttpResponse<String> response = sendGetRequest(url);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(response.body().isEmpty());

        // Other
        response = sendDeleteRequest(url);
        Assertions.assertEquals(405, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }
}