package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static TaskManager taskManager = FileBackedTasksManager.start();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
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
            } else {
                response = "Некорректный метод";
            }
            httpExchange.sendResponseHeaders(200, 0);
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
            Gson gson = new Gson();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    int indexId = path.indexOf("id=");
                    if (indexId == -1) { //если параметр не найден
                        response = gson.toJson(taskManager.getAllTasks()); //все задачи
                    } else {
                        try {
                            long id = Long.parseLong(path.substring(indexId + 3));
                            Task task = taskManager.getTask(id);
                            if (task == null) {
                                response = "Такой задачи не найдено";
                            } else {
                                response = gson.toJson(task);
                            }
                        } catch (NumberFormatException ex) {
                            response = "Не верный формат ID";
                        }
                    }
                    break;
                case "POST":
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpExchange.getRequestBody()))) {
                        String body = br.readLine();
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        response = "Создана новая задача";
                    } catch (ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = exception.getMessage();
                    }
                    break;
                case "DELETE":
                    indexId = path.indexOf("id=");
                    if (indexId == -1) { //если параметр не найден
                        taskManager.removeTask(); //удалить все задачи
                        response = "Удалены все задачи";
                    } else {
                        try {
                            long id = Long.parseLong(path.substring(indexId + 3));
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = "Удалена задача";
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = exception.getMessage();
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                            response = "Не верный формат ID";
                        }
                    }
                    break;
                default:
                    response = "Некорректный метод";
            }

            httpExchange.sendResponseHeaders(200, 0);
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
            } else {
                response = "Некорректный метод";
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
