package ru.yandex.practicum.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static TaskManager taskManager = FileBackedTasksManager.start();
    private static final String ERROR_FORMAT_ID = "Не верный формат ID";
    private static final String ERROR_METHOD_INCORRECT = "Некорректный метод";
    private static final String ERROR_FORMAT_REQUEST = "Неверный формат запроса";
    private static final String INFO_TASK_CREATED = "Создана новая задача";
    private static final String INFO_TASK_DELETED = "Удалена задача";
    private static final String INFO_TASKS_DELETED = "Удалена все задачи";

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
            } else {
                response = ERROR_METHOD_INCORRECT;
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
            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllTasks()); //все задачи
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            Task task = taskManager.getTask(id);
                            if (task == null) {
                                response = "Такой задачи не найдено";
                            } else {
                                response = gson.toJson(task);
                            }
                        } catch (NumberFormatException ex) {
                            response = ERROR_FORMAT_ID;
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
                    } catch (ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = exception.getMessage();
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query == null) { //если параметр не найден
                        taskManager.removeTask(); //удалить все задачи
                        response = INFO_TASKS_DELETED;
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = exception.getMessage();
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                            response = ERROR_FORMAT_ID;
                        }
                    }
                    break;
                default:
                    response = ERROR_METHOD_INCORRECT;
            }

            httpExchange.sendResponseHeaders(200, 0);
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
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            Task task = taskManager.getTask(id);
                            if (task == null) {
                                response = "Такого эпика не найдено";
                            } else {
                                response = gson.toJson(task);
                            }
                        } catch (NumberFormatException ex) {
                            response = ERROR_FORMAT_ID;
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
                    } catch (ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = exception.getMessage();
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { //если параметр не найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = exception.getMessage();
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                            response = ERROR_FORMAT_ID;
                        }
                    } else {
                        response = ERROR_FORMAT_REQUEST;
                    }
                    break;
                default:
                    response = ERROR_METHOD_INCORRECT;
            }

            httpExchange.sendResponseHeaders(200, 0);
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
            Gson gson = new Gson();

            switch (method) {
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String query = httpExchange.getRequestURI().getQuery();
                    System.out.println(path);
                    System.out.println(query);
                    String epic = path.split("/")[3]; //path epic
                    if ("epic".equals(epic)) {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            //информация по подзадаче
                            response = gson.toJson(taskManager.getTask(id));
                        } catch (NumberFormatException ex) {
                            response = ERROR_FORMAT_ID;
                        }
                    } else {
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            //информация по списку подзадач эпика
                            ArrayList<SubTask> subTaskList = taskManager.getSubTasks(id);
                            if (subTaskList.isEmpty()) {
                                response = "Нет подзадач";
                            } else
                                response = gson.toJson(subTaskList);
                        } catch (NumberFormatException ex) {
                            response = ERROR_FORMAT_ID;
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
                    } catch (ManagerTaskException exception) {
                        exception.printStackTrace();
                        response = exception.getMessage();
                    }
                    break;
                case "DELETE":
                    query = httpExchange.getRequestURI().getQuery();
                    if (query != null) { //если параметр не найден
                        try {
                            long id = Long.parseLong(query.split("=")[1]);
                            taskManager.removeTask(id); //удалить задачу по ID
                            response = INFO_TASK_DELETED;
                        } catch (ManagerTaskException exception) {
                            exception.printStackTrace();
                            response = exception.getMessage();
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                            response = ERROR_FORMAT_ID;
                        }
                    } else {
                        response = ERROR_FORMAT_REQUEST;
                    }
                    break;
                default:
                    response = ERROR_METHOD_INCORRECT;
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
                response = ERROR_METHOD_INCORRECT;
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
