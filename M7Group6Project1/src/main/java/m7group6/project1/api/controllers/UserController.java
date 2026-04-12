package m7group6.project1.api.controllers;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import m7group6.project1.api.ApiError;
import m7group6.project1.app.ServiceLocator;
import m7group6.project1.exceptions.InvalidDBInputException; // unchecked
import m7group6.project1.service.LibraryService;
import m7group6.project1.service.dto.UserDto;
import m7group6.project1.util.JsonUtil;

public final class UserController {

    private UserController() {
    }

    public static void register() {

        path("/api/users", () -> {

            // POST /api/users
            post("", (req, res) -> {
                try {
                    CreateUser body = JsonUtil.fromJson(req.body(), CreateUser.class);

                    if (body == null || body.name == null || body.name.trim().isEmpty()) {
                        res.status(422);
                        return JsonUtil.toJson(ApiError.of(422, "ValidationError", "Name is required", req.pathInfo()));
                    }

                    int id = svc().addUser(new UserDto(0, body.name.trim()));

                    res.status(201);
                    res.header("Location", "/api/users/" + id);

                    return JsonUtil.toJson(halUser(id, body.name.trim()));

                } catch (InvalidDBInputException e) {
                    res.status(422);
                    return JsonUtil.toJson(ApiError.of(422, "InvalidDBInput", e.getMessage(), req.pathInfo()));

                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError", "Unexpected error while creating user", req.pathInfo()));
                }
            });
        });
    }

    // ----- DTO for request body -----
    static final class CreateUser {
        public String name;
    }

    // ----- HAL builder -----
    private static Map<String, Object> halUser(int id, String name) {
        Map<String, Object> m = new HashMap<>();
        m.put("userId", id);
        m.put("name", name);
        m.put("_links", Map.of(
                "self", Map.of("href", "/api/users/" + id),
                "collection", Map.of("href", "/api/users")
        ));
        return m;
    }

    private static LibraryService svc() {
        return ServiceLocator.libraryService();
    }
}