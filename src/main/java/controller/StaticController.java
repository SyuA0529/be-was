package controller;

import db.PostTable;
import db.UserTable;
import dto.PostFrontDto;
import model.User;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.myframework.handler.request.annotation.Controller;
import webserver.myframework.handler.request.annotation.RequestMapping;
import webserver.myframework.model.Model;
import webserver.myframework.session.Session;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Controller
public class StaticController {
    @RequestMapping("/index.html")
    public void index(HttpRequest httpRequest, HttpResponse httpResponse, Model model) {
        Session session = httpRequest.getSession(false);
        if(session != null) {
            User user = UserTable.findUserById((String) session.getAttribute("userId"));
            model.addParameter("user", user);
        }
        model.addParameter("postDtos", getAllPostDtos(model));
        httpResponse.forward("/index.html");
    }

    private static List<PostFrontDto> getAllPostDtos(Model model) {
        return PostTable.findAll().stream()
                .map(PostFrontDto::new)
                .collect(Collectors.toList());
    }
}
