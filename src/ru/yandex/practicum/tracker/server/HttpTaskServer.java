package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;
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

    private final TaskManager taskManager;
    private HttpServer server;
    private final Gson gson;
    private static final String INFO_TASK_DELETED = "Удалена задача";
    private static final String INFO_TASKS_DELETED = "Удалены все задачи";
    private static final String INFO_TASK_NOT_FOUND = "Задача не найдена";
    private static final String INFO_TASKS_NOT_FOUND = "Задачи не найдены";

    public HttpTaskServer(int port, TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubTaskHandler());
        server.createContext("/tasks/history", new HistoryHandler());
        server.start();
    }

    // задачи по приоритету
    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();
            if ("GET".equals(method)) {
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

    // работа с задачей
    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllTasks()); // все задачи
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
                            response = "";
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        if (body == null) {
                            throw new IOException();
                        }
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            throw new IOException();
                        }
                        Task newTask = gson.fromJson(body, Task.class);
                        if (newTask.getTaskId() == null) { // если ID задачи нет в BODY
                            taskManager.addTask(newTask);
                        } else {
                            taskManager.updateTask(newTask);
                        }
                        response = gson.toJson(newTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (IOException | NumberFormatException | ManagerTaskException exception) {
                        response = "";
                        exception.printStackTrace();
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query == null) { // если параметр не найден
                        taskManager.removeTask(); // удалить все задачи
                        response = INFO_TASKS_DELETED;
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); // удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
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

    // работа с эпиком
    class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) { // если параметр не найден
                        response = gson.toJson(taskManager.getEpics()); // все задачи
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
                        } catch (ManagerTaskException exception) {
                            response = "";
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        if (body == null) {
                            throw new IOException();
                        }
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            throw new IOException();
                        }
                        Epic newTask = gson.fromJson(body, Epic.class);
                        if (newTask.getTaskId() == null) { // если ID задачи нет в BODY
                            taskManager.addTask(newTask);
                        } else {
                            taskManager.updateTask(newTask);
                        }
                        response = gson.toJson(newTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (IOException | NumberFormatException | ManagerTaskException exception) {
                        response = "";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { // если параметр найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); // удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
                            response = "";
                            exception.printStackTrace();
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

    // работа с подзадачей
    class SubTaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String query = httpExchange.getRequestURI().getQuery();
                    if (path.contains("epic")) {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            // информация по списку подзадач эпика
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
                            // информация по подзадаче
                            response = gson.toJson(taskManager.getTask(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException exception) {
                            response = INFO_TASK_NOT_FOUND;
                            httpExchange.sendResponseHeaders(404, 0);
                        } catch (NumberFormatException exception) {
                            response = "";
                            httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        if (body == null) {
                            throw new IOException();
                        }
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            throw new IOException();
                        }
                        SubTask newTask = gson.fromJson(body, SubTask.class);
                        ;
                        if (newTask.getTaskId() == null) { // если ID задачи нет в BODY
                            taskManager.addTask(newTask);
                        } else {
                            taskManager.updateTask(newTask);
                        }
                        response = gson.toJson(newTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (ManagerTaskException | NumberFormatException | IOException exception) {
                        response = "";
                        exception.printStackTrace();
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { // если параметр найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); // удалить задачу по ID
                            response = INFO_TASK_DELETED;
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerTaskException | NumberFormatException exception) {
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

    // история задач
    class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();

            if ("GET".equals(method)) {
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

    public void stop() {
        server.stop(0);
    }
}
