package controller;

import db.UserTable;
import model.User;
import service.UserService;
import utils.ControllerUtils;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.response.HttpStatus;
import webserver.myframework.handler.argument.annotation.RequestBody;
import webserver.myframework.handler.request.annotation.Controller;
import webserver.myframework.handler.request.annotation.RequestMapping;
import webserver.myframework.model.Model;
import webserver.myframework.session.Session;

import java.util.*;


@SuppressWarnings("unused")
@Controller("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/create", method = HttpMethod.POST)
    public void signUp(@RequestBody String body, HttpResponse httpResponse) {
        try {
            Map<String, String> parameterMap = ControllerUtils.getParameterMap(body, 4);
            userService.signUp(
                    parameterMap.get("userId"),
                    parameterMap.get("password"),
                    parameterMap.get("name"),
                    parameterMap.get("email"));
            httpResponse.sendRedirection("/index.html");
        } catch (IllegalArgumentException exception) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login", method = HttpMethod.POST)
    public void signIn(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Map<String, String> parameterMap = ControllerUtils.getParameterMap(httpRequest.getBodyToString(), 2);
            User user = userService.signIn(parameterMap.get("userId"), parameterMap.get("password"));
            if (user == null) {
                httpResponse.sendRedirection("/user/login_failed.html");
                return;
            }
            Session session = httpRequest.getSession();
            session.setAttribute("userId", user.getUserId());
            httpResponse.sendRedirection("/index.html");
        } catch (IllegalArgumentException exception) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/list")
    public void listUsers(HttpRequest httpRequest, HttpResponse httpResponse, Model model) {
        Session session = httpRequest.getSession(false);
        if(session == null) {
            httpResponse.sendRedirection("/user/login.html");
            return;
        }
        User user = UserTable.findUserById((String) session.getAttribute("userId"));
        model.addParameter("user", user);
        model.addParameter("users", new ArrayList<>(UserTable.findAll()));
        httpResponse.forward("/user/list.html");
    }
}
