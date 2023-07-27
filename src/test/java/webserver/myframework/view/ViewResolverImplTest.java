package webserver.myframework.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import webserver.myframework.model.ModelImpl;
import webserver.myframework.view.content.DynamicContentRenderer;
import webserver.myframework.view.content.MultipleObjectDynamicContent;
import webserver.myframework.view.content.SingleObjectDynamicContent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;


@DisplayName("ViewResolverImpl 테스트")
class ViewResolverImplTest {
    final DynamicContentRenderer dynamicContentRenderer =
            new DynamicContentRenderer(new SingleObjectDynamicContent(), new MultipleObjectDynamicContent());
    final ViewResolver viewResolver = new ViewResolverImpl(dynamicContentRenderer);

    @Nested
    @DisplayName("resolve method")
    class Resolve {
        @Nested
        @DisplayName("templates에 viewPath에 해당하는 파일이 존재한다면")
        class IsFileExistInTemplates {
            @Test
            @DisplayName("templates에서 viewPath에 해당하는 파일의 경로를 갖는 View 객체를 반환한다")
            void returnView() throws NoSuchFieldException, IllegalAccessException, IOException {
                //given
                //when
                View indexView = viewResolver.resolve("/index.html", new ModelImpl());

                //then
                Field htmlField = indexView.getClass().getDeclaredField("viewFile");
                htmlField.setAccessible(true);
                Object result = htmlField.get(indexView);
                assertThat(result).isInstanceOf(File.class);
                assertThat(((File) result).getName()).isEqualTo("index.html");
            }
        }

        @Nested
        @DisplayName("static에 viewPath에 해당하는 파일이 존재한다면")
        class IsFileExistInStatic {
            @Test
            @DisplayName("statics에서 viewPath에 해당하는 파일의 경로를 갖는 View 객체를 반환한다")
            void returnView() throws NoSuchFieldException, IllegalAccessException, IOException {
                //given
                //when
                View indexView = viewResolver.resolve("/favicon.ico", new ModelImpl());

                //then
                Field htmlField = indexView.getClass().getDeclaredField("viewFile");
                htmlField.setAccessible(true);
                Object result = htmlField.get(indexView);
                assertThat(result).isInstanceOf(File.class);
                assertThat(((File) result).getName()).isEqualTo("favicon.ico");
            }
        }

        @Nested
        @DisplayName("viewPath에 해당하는 파일이 존재하지 않는다면")
        class isFileNotExist {
            @Test
            @DisplayName("404.html 파일의 경로를 갖는 View 객체를 반환한다")
            void return404View() throws NoSuchFieldException, IllegalAccessException, IOException {
                //given
                //when
                View notFoundView = viewResolver.resolve("/notExist", new ModelImpl());

                //then
                Field htmlField = notFoundView.getClass().getDeclaredField("viewFile");
                htmlField.setAccessible(true);
                Object result = htmlField.get(notFoundView);
                assertThat(result).isInstanceOf(File.class);
                assertThat(((File) result).getName()).isEqualTo("404.html");
            }
        }
    }


}
