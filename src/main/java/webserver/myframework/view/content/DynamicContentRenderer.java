package webserver.myframework.view.content;

import webserver.myframework.bean.annotation.Component;
import webserver.myframework.model.Model;
import webserver.myframework.utils.StringUtils;

import java.util.List;

@Component
public class DynamicContentRenderer {
    private final SingleObjectDynamicContent singleObjectDynamicContent;
    private final MultipleObjectDynamicContent multipleObjectDynamicContent;

    public DynamicContentRenderer(SingleObjectDynamicContent singleObjectDynamicContent,
                                  MultipleObjectDynamicContent multipleObjectDynamicContent) {
        this.singleObjectDynamicContent = singleObjectDynamicContent;
        this.multipleObjectDynamicContent = multipleObjectDynamicContent;
    }

    public String renderContent(Model model, String objectName, String content) throws NoSuchFieldException, IllegalAccessException {
        if (objectName.startsWith("default")) {
            return content;
        }

        List<String> contentParts = StringUtils.splitStringByRegex(content, "(\\[[^\\]]+\\])");
        if (objectName.startsWith("list-")) {
            return multipleObjectDynamicContent.render(model, objectName, contentParts);
        }
        return singleObjectDynamicContent.render(model, objectName, contentParts);
    }
}
