package webserver.myframework.view.content;

import webserver.myframework.model.Model;
import webserver.myframework.utils.ReflectionUtils;

import java.util.List;

public abstract class DynamicContent {
   public abstract String render(Model model,
                                 String objectName,
                                 List<String> contentParts) throws NoSuchFieldException, IllegalAccessException;

    protected static void writeParameterContent(StringBuilder stringBuilder,
                                                List<String> contentParts,
                                                Object parameter) throws NoSuchFieldException, IllegalAccessException {
        for (String contentPart : contentParts) {
            if (contentPart.startsWith("[") && contentPart.endsWith("]")) {
                String fieldName = contentPart.replaceAll("[\\[\\]]", "");
                Object field = ReflectionUtils.getField(parameter, fieldName);
                stringBuilder.append(field);
                continue;
            }
            stringBuilder.append(contentPart);
        }
    }
}
