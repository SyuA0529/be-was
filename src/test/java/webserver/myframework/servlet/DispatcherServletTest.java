package webserver.myframework.servlet;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import webserver.http.ContentType;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.http.response.Cookie;
import webserver.http.response.HttpResponse;
import webserver.myframework.handler.argument.ArgumentResolver;
import webserver.myframework.handler.argument.ArgumentResolverImpl;
import webserver.myframework.handler.request.RequestHandlerImpl;
import webserver.myframework.handler.request.RequestHandlerResolver;
import webserver.myframework.handler.request.RequestHandlerResolverImpl;
import webserver.myframework.handler.request.RequestInfo;
import webserver.myframework.handler.request.annotation.Controller;
import webserver.myframework.handler.request.annotation.RequestMapping;
import webserver.myframework.handler.request.exception.DuplicateRequestHandlerException;
import webserver.myframework.session.Session;
import webserver.myframework.session.SessionManager;
import webserver.myframework.session.SessionManagerImpl;
import webserver.myframework.view.ViewResolverImpl;
import webserver.myframework.view.content.DynamicContentRenderer;
import webserver.myframework.view.content.MultipleObjectDynamicContent;
import webserver.myframework.view.content.SingleObjectDynamicContent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DispatcherServlet 테스트")
class DispatcherServletTest {
    String RESOURCE_URI = "src/main/resources";
    DispatcherServlet dispatcherServlet;
    DynamicContentRenderer dynamicContentRenderer =
            new DynamicContentRenderer(new SingleObjectDynamicContent(), new MultipleObjectDynamicContent());
    static ArgumentResolver argumentResolver = new ArgumentResolverImpl();

    @BeforeEach
    void setUp() throws ReflectiveOperationException, DuplicateRequestHandlerException {
        RequestHandlerResolver handlerResolver = new RequestHandlerResolverImpl();
        handlerResolver.registerHandler(
                getTestRequestInfo("/exist"), getTestHandler("existHandler"));
        handlerResolver.registerHandler(
                getTestRequestInfo("/notExist"), getTestHandler("notExistHandler"));
        handlerResolver.registerHandler(
                getTestRequestInfo("/createSession"), getTestHandler("createSessionHandler"));
        dispatcherServlet = new DispatcherServlet(handlerResolver, new ViewResolverImpl(dynamicContentRenderer));
    }

    private static RequestInfo getTestRequestInfo(String uri) {
        return RequestInfo.builder()
                .uri(uri)
                .httpMethod(HttpMethod.GET)
                .build();
    }

    private static RequestHandlerImpl getTestHandler(String methodName) throws NoSuchMethodException {
        return new RequestHandlerImpl(
                new TestController(),
                TestController.class.getMethod(methodName, HttpRequest.class, HttpResponse.class),
                argumentResolver);
    }

    @Nested
    @DisplayName("handleRequest method")
    class HandleRequest {
        @Nested
        @DisplayName("uri에 매칭되는 핸들러가 없는 경우")
        class HandlerMatchedURINotExist {
            @Nested
            @DisplayName("uri에 매칭되는 파일이 없는 경우")
            class FileMatchedURINotExist {
                @Test
                @DisplayName("404 에러 페이지를 랜더링한다")
                void render404ErrorPage() throws IOException {
                    //given
                    HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                            .uri("/notExistFile").build();
                    HttpResponse httpResponse = HttpResponse.getInstance();

                    //when
                    dispatcherServlet.handleRequest(httpRequest, httpResponse);

                    //then
                    assertThat(httpResponse.getBody())
                            .isEqualTo(Files.readAllBytes(Path.of(RESOURCE_URI + "/templates/errors/404.html")));
                }
            }

            @Nested
            @DisplayName("uri에 매칭되는 파일이 존재하는 경우")
            class FileMatchedURIExist {
                @Test
                @DisplayName("헤딩하는 파일을 랜더링한다")
                void renderFileMatchedURI() throws IOException {
                    //given
                    HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                            .uri("/user/login.html").build();
                    HttpResponse httpResponse = HttpResponse.getInstance();

                    //when
                    dispatcherServlet.handleRequest(httpRequest, httpResponse);

                    //then
                    assertThat(httpResponse.getBody())
                            .isEqualTo(Files.readAllBytes(Path.of(RESOURCE_URI + "/templates/user/login.html")));
                }
            }
        }

        @Nested
        @DisplayName("http method와 일치하는 핸들러가 없는 경우")
        class HandlerMatchedMethodNotExist {
            @Test
            @DisplayName("405 에러 페이지를 랜더링한다")
            void render405ErrorPage() throws IOException {
                //given
                HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                        .uri("/exist")
                        .method(HttpMethod.POST).build();
                HttpResponse httpResponse = HttpResponse.getInstance();

                //when
                dispatcherServlet.handleRequest(httpRequest, httpResponse);

                //then
                assertThat(httpResponse.getBody())
                        .isEqualTo(Files.readAllBytes(Path.of(RESOURCE_URI + "/templates/errors/405.html")));
            }
        }

        @Nested
        @DisplayName("http method와 일치하는 핸들러가 있는 경우")
        class HandlerMatchedMethodExist {
            @Nested
            @DisplayName("응답의 URI에 해당하는 파일이 존재하는 경우")
            class FileMatchedResponseURIExist {
                @Test
                @DisplayName("핸들러에게 요청을 위임한다")
                void delegateRequestToHandler() throws IOException {
                    HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                            .uri("/exist")
                            .method(HttpMethod.GET).build();
                    HttpResponse httpResponse = HttpResponse.getInstance();

                    //when
                    dispatcherServlet.handleRequest(httpRequest, httpResponse);

                    //then
                    assertThat(httpResponse.getContentType()).isEqualTo(ContentType.CSS);
                    assertThat(httpResponse.getBody())
                            .isEqualTo(Files.readAllBytes(Path.of(RESOURCE_URI + "/static/css/styles.css")));
                }
            }

            @Nested
            @DisplayName("요청에 대해 세션이 존재하는 경우")
            class WhenSessionExistAboutRequest {
                @Test
                @DisplayName("응답에 세션 id를 갖는 쿠키를 추가한다")
                void addCookieHasSessionIdToResponse() throws IOException {
                    //given
                    SessionManager sessionManager = new SessionManagerImpl();
                    HttpRequest httpRequest = HttpRequest.builder(sessionManager)
                            .uri("/createSession")
                            .method(HttpMethod.GET).build();
                    HttpResponse httpResponse = HttpResponse.getInstance();

                    //when
                    dispatcherServlet.handleRequest(httpRequest, httpResponse);

                    //then
                    List<Cookie> cookies = httpResponse.getCookies();
                    assertThat(cookies.size()).isEqualTo(1);
                    assertThat(cookies.get(0).getName()).isEqualTo(Session.SESSION_KEY);
                }
            }
            
            @Nested
            @DisplayName("응답의 URI에 해당하는 파일이 존재하지 않는 경우")
            class FileMatchedResponseURINotExist {
                @Test
                @DisplayName("404 에러 페이지를 렌더링한다")
                void render404ErrorPage() throws IOException {
                    HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                            .uri("/notExist")
                            .method(HttpMethod.GET).build();
                    HttpResponse httpResponse = HttpResponse.getInstance();

                    //when
                    dispatcherServlet.handleRequest(httpRequest, httpResponse);

                    //then
                    assertThat(httpResponse.getBody())
                            .isEqualTo(Files.readAllBytes(Path.of(RESOURCE_URI + "/templates/errors/404.html")));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @Controller
    public static class TestController {
        @RequestMapping("/exist")
        public void existHandler(HttpRequest request, HttpResponse response) {
            response.forward("/css/styles.css");
        }

        @RequestMapping("/notExist")
        public void notExistHandler(HttpRequest request, HttpResponse response) {
            response.forward("/notExist/notExist");
        }

        @RequestMapping("/createSession")
        public void createSessionHandler(HttpRequest request, HttpResponse response) {
            request.getSession();
        }
    }
}
