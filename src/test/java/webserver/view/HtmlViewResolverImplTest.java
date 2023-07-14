package webserver.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;


@DisplayName("HtmlViewResolverImpl 테스트")
class HtmlViewResolverImplTest {
    ViewResolver viewResolver = new HtmlViewResolverImpl();

    @Nested
    @DisplayName("resolve method")
    class Resolve {
        @Nested
        @DisplayName("viewPath에 해당하는 파일이 존재한다면")
        class isFileExist {
            @Test
            @DisplayName("viewPath에 해당하는 파일의 경로를 갖는 View 객체를 반환한다")
            void returnView() throws NoSuchFieldException, IllegalAccessException {
                //given
                //when
                View indexView = viewResolver.resolve("/index.html");

                //then
                Field htmlField = indexView.getClass().getDeclaredField("html");
                htmlField.setAccessible(true);
                Object result = htmlField.get(indexView);
                assertThat(result).isInstanceOf(File.class);
                assertThat(((File) result).getName()).isEqualTo("index.html");
            }
        }

        @Nested
        @DisplayName("viewPath에 해당하는 파일이 존재하지 않는다면")
        class isFileNotExist {
            @Test
            @DisplayName("404.html 파일의 경로를 갖는 View 객체를 반환한다")
            void return404View() throws NoSuchFieldException, IllegalAccessException {
                //given
                //when
                View notFoundView = viewResolver.resolve("/notExist");

                //then
                Field htmlField = notFoundView.getClass().getDeclaredField("html");
                htmlField.setAccessible(true);
                Object result = htmlField.get(notFoundView);
                assertThat(result).isInstanceOf(File.class);
                assertThat(((File) result).getName()).isEqualTo("404.html");
            }
        }
    }
}