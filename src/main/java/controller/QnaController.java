package controller;

import db.PostTable;
import db.UserTable;
import dto.PostFrontDto;
import model.Post;
import model.User;
import utils.ControllerUtils;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.response.HttpStatus;
import webserver.myframework.handler.argument.annotation.RequestParam;
import webserver.myframework.handler.request.annotation.Controller;
import webserver.myframework.handler.request.annotation.RequestMapping;
import webserver.myframework.model.Model;
import webserver.myframework.session.Session;

import java.util.Map;

@Controller("/qna")
public class QnaController {
    @RequestMapping("/write")
    public void getWritePostPage(HttpRequest httpRequest, HttpResponse httpResponse, Model model) {
        Session session = httpRequest.getSession(false);
        if(session == null) {
            httpResponse.sendRedirection("/user/login.html");
            return;
        }

        User user = UserTable.findUserById((String) session.getAttribute("userId"));
        model.addParameter("user", user);
        httpResponse.forward("/qna/write.html");
    }

    @RequestMapping(value = "/write", method = HttpMethod.POST)
    public void writePost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession(false);
        if(session == null) {
            httpResponse.sendRedirection("/user/login.html");
            return;
        }

        String body = httpRequest.getBodyToString();
        Map<String, String> parameterMap = ControllerUtils.getParameterMap(body, 3);
        PostTable.addPost(new Post(
                parameterMap.get("author"),
                parameterMap.get("title"),
                parameterMap.get("contents")));
        httpResponse.sendRedirection("/index.html");
    }

    @RequestMapping(value = "/show", method = HttpMethod.GET)
    public void showPost(@RequestParam("postId") long postId,
                         HttpRequest httpRequest,
                         HttpResponse httpResponse,
                         Model model) {
        Session session = httpRequest.getSession(false);
        if(session == null) {
            httpResponse.sendRedirection("/user/login.html");
            return;
        }

        Post post = PostTable.findByPostId(postId);
        if(post == null) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND);
            httpResponse.forward("notExist");
            return;
        }
        model.addParameter("postDto", new PostFrontDto(post));
        httpResponse.forward("/qna/show.html");
    }
}
