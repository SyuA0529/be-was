package webserver.myframework.view.content;

import webserver.myframework.bean.annotation.Component;
import webserver.myframework.model.Model;

import java.util.List;

@Component
public class MultipleObjectDynamicContent extends DynamicContent {

    @SuppressWarnings("unchecked")
    @Override
    public String render(Model model, String objectName, List<String> contentParts) throws NoSuchFieldException, IllegalAccessException {
        objectName = objectName.replace("list-", "");
        List<Object> parameters = (List<Object>) model.getParameter(objectName);
        StringBuilder stringBuilder = new StringBuilder();
        for (Object parameter : parameters) {
            writeParameterContent(stringBuilder, contentParts, parameter);
        }
        return stringBuilder.toString();
    }
}
