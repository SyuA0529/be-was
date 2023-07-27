package webserver.myframework.handler.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.response.HttpStatus;
import webserver.myframework.handler.argument.ArgumentResolver;
import webserver.myframework.handler.argument.ArgumentResolverImpl;
import webserver.myframework.model.ModelImpl;
import webserver.myframework.session.SessionManagerImpl;

import static org.assertj.core.api.Assertions.*;


@DisplayName("RequestHandlerImpl 테스트")
class RequestHandlerImplTest {
    ArgumentResolver argumentResolver = new ArgumentResolverImpl();
    @Nested
    @DisplayName("handle method")
    class Handle {
        @Test
        @DisplayName("필드의 메소드를 호출하고 결과로 응답에 uri를 설정한다")
        void invokeMethodOfFieldAndReturnString() throws ReflectiveOperationException {
            //given
            RequestHandlerImpl requestHandler = new RequestHandlerImpl(new TestClass(),
                    TestClass.class.getDeclaredMethod("testMethod", HttpRequest.class, HttpResponse.class),
                    argumentResolver);
            HttpResponse httpResponse = HttpResponse.getInstance();
            HttpRequest httpRequest = HttpRequest.builder(new SessionManagerImpl())
                    .uri("uri")
                    .build();

            //when
            requestHandler.handle(httpRequest, httpResponse, new ModelImpl());

            //then
            assertThat(httpResponse.getUri()).isEqualTo("string");
        }

        @Nested
        @DisplayName("호출했을 때 예외가 발생했다면")
        class hasExceptionWhenInvokeHandler0 {
            @Test
            @DisplayName("상태 코드가 500이다")
            void StatusCodeIs500() throws NoSuchMethodException {
                //given
                RequestHandlerImpl requestHandler = new RequestHandlerImpl(new TestClass(),
                        TestClass.class.getDeclaredMethod("testMethod", HttpRequest.class, HttpResponse.class),
                        argumentResolver);
                HttpResponse httpResponse = HttpResponse.getInstance();
                requestHandler.handle(null, httpResponse, new ModelImpl());

                //when
                //then
                assertThat(httpResponse.getStatus())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @SuppressWarnings("SameReturnValue")
    static class TestClass {
        @SuppressWarnings("unused")
        void testMethod(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpRequest.getUri();
            httpResponse.forward("string");
        }
    }
}
