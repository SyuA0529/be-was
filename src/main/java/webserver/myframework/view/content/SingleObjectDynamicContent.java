package webserver.myframework.view.content;

import webserver.myframework.bean.annotation.Component;
import webserver.myframework.model.Model;

import java.util.List;

@Component
public class SingleObjectDynamicContent extends DynamicContent{
    @Override
    public String render(Model model, String objectName, List<String> contentParts) throws NoSuchFieldException, IllegalAccessException {
        Object parameter = model.getParameter(objectName);
        StringBuilder stringBuilder = new StringBuilder();
        writeParameterContent(stringBuilder, contentParts, parameter);
        return stringBuilder.toString();
    }
}
