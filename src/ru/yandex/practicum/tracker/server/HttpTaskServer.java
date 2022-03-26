package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;
import ru.yandex.practicum.tracker.utils.SubTaskAdapter;
import ru.yandex.practicum.tracker.utils.TaskAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static TaskManager taskManager = FileBackedTasksManager.start();
    private static final String INFO_TASK_CREATED = "Создана новая задача";
    private static final String INFO_TASK_DELETED = "Удалена задача";
    private static final String INFO_TASKS_DELETED = "Удалена все задачи";
    private static final String INFO_TASK_NOT_FOUND = "Задача не найдена";
    private static final String INFO_TASKS_NOT_FOUND = "Задачи не найдены";

    public static void main(String[] args) throws IOException {
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
                Gson gson = new Gson();
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
                    .registerTypeAdapter(Task.class, new TaskAdapter())
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
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        response = INFO_TASK_CREATED;
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = exception.getMessage();
                        httpExchange.sendResponseHeaders(404, 0);
                    } catch (IOException exception) {
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
            Gson gson = new Gson();

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
                        Epic task = gson.fromJson(body, Epic.class);
                        taskManager.addTask(task);
                        response = INFO_TASK_CREATED;
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (ManagerTaskException exception) {
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
                    .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
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
                        SubTask task = gson.fromJson(body, SubTask.class);
                        taskManager.addTask(task);
                        response = INFO_TASK_CREATED;
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
                Gson gson = new Gson();
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
