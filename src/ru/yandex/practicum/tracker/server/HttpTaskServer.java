package ru.yandex.practicum.tracker.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;
import ru.yandex.practicum.tracker.utils.DateFormat;
import ru.yandex.practicum.tracker.utils.DurationAdapter;
import ru.yandex.practicum.tracker.utils.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static TaskManager taskManager;
    private static final String INFO_TASK_CREATED = "Создана новая задача";
    private static final String INFO_TASK_UPDATED = "Обновлена задача";
    private static final String INFO_TASK_DELETED = "Удалена задача";
    private static final String INFO_TASKS_DELETED = "Удалена все задачи";
    private static final String INFO_TASK_NOT_FOUND = "Задача не найдена";
    private static final String INFO_TASKS_NOT_FOUND = "Задачи не найдены";

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        taskManager = Managers.getDefault();
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubTaskHandler());
        server.createContext("/tasks/history", new HistoryHandler());
        server.start();
    }

    //задачи по приоритету
    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            if ("GET".equals(method)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .create();
                response = gson.toJson(taskManager.getPrioritizedTasks());
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
                response = "";
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    //работа с задачей
    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();

            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllTasks()); //все задачи
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            Task task = taskManager.getTask(id);
                            response = gson.toJson(task);
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (NumberFormatException ex) {
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = "";
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        JsonElement jsonElement = JsonParser.parseString(body);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement id = jsonObject.get("taskId");
                        JsonElement name = jsonObject.get("taskName");
                        JsonElement description = jsonObject.get("taskDescription");
                        JsonElement status = jsonObject.get("taskStatus");
                        JsonElement duration = jsonObject.get("duration");
                        JsonElement startTime = jsonObject.get("startTime");
                        if (id == null) { //если ID задачи нет в BODY
                            taskManager.addTask(new Task(name.getAsString(),
                                    description.getAsString(),
                                    State.valueOf(status.getAsString()),
                                    Duration.ofMinutes(duration.getAsLong()),
                                    LocalDateTime.parse(startTime.getAsString(),
                                            DateFormat.getDateTimeFormat())));
                            response = INFO_TASK_CREATED;
                        } else {
                            taskManager.updateTask(new Task(name.getAsString(),
                                    description.getAsString(),
                                    id.getAsLong(),
                                    State.valueOf(status.getAsString()),
                                    Duration.ofMinutes(duration.getAsLong()),
                                    LocalDateTime.parse(startTime.getAsString(),
                                            DateFormat.getDateTimeFormat())));
                            response = INFO_TASK_UPDATED;
                        }
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (IOException | NumberFormatException | ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query == null) { //если параметр не найден
                        taskManager.removeTask(); //удалить все задачи
                        response = INFO_TASKS_DELETED;
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
                            exception.printStackTrace();
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
                    response = "";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    //работа с эпиком
    static class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();

            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) { //если параметр не найден
                        response = gson.toJson(taskManager.getEpics()); //все задачи
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            Task task = taskManager.getTask(id);
                            if (task == null) {
                                response = INFO_TASK_NOT_FOUND;
                                httpExchange.sendResponseHeaders(404, 0);
                            } else {
                                response = gson.toJson(task);
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        } catch (NumberFormatException ex) {
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        JsonElement jsonElement = JsonParser.parseString(body);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement id = jsonObject.get("taskId");
                        JsonElement name = jsonObject.get("taskName");
                        JsonElement description = jsonObject.get("taskDescription");
                        JsonElement status = jsonObject.get("taskStatus");
                        if (id == null) { //если ID задачи нет в BODY
                            taskManager.addTask(new Epic(name.getAsString(),
                                    description.getAsString(),
                                    State.valueOf(status.getAsString())));
                            response = INFO_TASK_CREATED;
                        } else {
                            taskManager.updateTask(new Epic(name.getAsString(),
                                    description.getAsString(),
                                    State.valueOf(status.getAsString()),
                                    id.getAsLong()));
                            response = INFO_TASK_UPDATED;
                        }
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (IOException | NumberFormatException | ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { //если параметр найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
                            exception.printStackTrace();
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    } else {
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
                    response = "";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    //работа с подзадачей
    static class SubTaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();

            switch (method) {
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String query = httpExchange.getRequestURI().getQuery();
                    if (path.contains("epic")) {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            //информация по списку подзадач эпика
                            ArrayList<SubTask> subTaskList = taskManager.getSubTasks(id);
                            if (subTaskList.isEmpty()) {
                                response = INFO_TASKS_NOT_FOUND;
                                httpExchange.sendResponseHeaders(404, 0);
                            } else {
                                response = gson.toJson(subTaskList);
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        } catch (NumberFormatException ex) {
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            //информация по подзадаче
                            response = gson.toJson(taskManager.getTask(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = INFO_TASK_NOT_FOUND;
                            httpExchange.sendResponseHeaders(404, 0);
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        JsonElement jsonElement = JsonParser.parseString(body);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement id = jsonObject.get("taskId");
                        JsonElement name = jsonObject.get("taskName");
                        JsonElement description = jsonObject.get("taskDescription");
                        JsonElement status = jsonObject.get("taskStatus");
                        JsonElement duration = jsonObject.get("duration");
                        JsonElement startTime = jsonObject.get("startTime");
                        JsonElement epicId = jsonObject.get("epicId");
                        if (id == null) { //если ID задачи нет в BODY
                            taskManager.addTask(new SubTask(name.getAsString(),
                                    description.getAsString(),
                                    State.valueOf(status.getAsString()),
                                    epicId.getAsLong(),
                                    Duration.ofMinutes(duration.getAsLong()),
                                    LocalDateTime.parse(startTime.getAsString(),
                                            DateFormat.getDateTimeFormat())));
                            response = INFO_TASK_CREATED;
                        } else {
                            taskManager.updateTask(new SubTask(name.getAsString(),
                                    description.getAsString(),
                                    id.getAsLong(),
                                    State.valueOf(status.getAsString()),
                                    epicId.getAsLong(),
                                    Duration.ofMinutes(duration.getAsLong()),
                                    LocalDateTime.parse(startTime.getAsString(),
                                            DateFormat.getDateTimeFormat())));
                            response = INFO_TASK_UPDATED;
                        }
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (ManagerTaskException | NumberFormatException | IOException exception) {
                        exception.printStackTrace();
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { //если параметр найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
                            exception.printStackTrace();
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    } else {
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
                    response = "";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    //история задач
    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            if ("GET".equals(method)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .create();
                response = gson.toJson(taskManager.getHistory());
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
                response = "";
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
