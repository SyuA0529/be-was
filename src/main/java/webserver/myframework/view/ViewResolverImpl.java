package webserver.myframework.view;

import webserver.myframework.bean.annotation.Component;
import webserver.myframework.model.Model;
import webserver.myframework.view.content.DynamicContentRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ViewResolverImpl implements ViewResolver {
    private static final String RESOURCE_PREFIX = "src/main/resources";
    private static final String DYNAMIC_VIEW = "/templates";
    private static final String STATIC_VIEW = "/static";
    private static final String DEFAULT_VIEW_PATH = "/errors/404.html";

    private final DynamicContentRenderer dynamicContentRenderer;

    public ViewResolverImpl(DynamicContentRenderer dynamicContentRenderer) {
        this.dynamicContentRenderer = dynamicContentRenderer;
    }


    @Override
    public View resolve(String viewUri, Model model) throws IOException {
        File dynamicFile = new File(getViewPath(viewUri, false));
        if(dynamicFile.exists() && dynamicFile.isFile() && dynamicFile.canRead()) {
            return getTemplateResourceView(model, dynamicFile);
        }

        File staticFile = new File(getViewPath(viewUri, true));
        if(staticFile.exists() && staticFile.isFile() && staticFile.canRead()) {
            return new StaticView(staticFile);
        }

        return new StaticView(new File(RESOURCE_PREFIX + DYNAMIC_VIEW + DEFAULT_VIEW_PATH));
    }

    private View getTemplateResourceView(Model model, File dynamicFile) throws IOException {
        try(FileInputStream fileInputStream = new FileInputStream(dynamicFile)) {
            String fileStart = new String(fileInputStream.readNBytes(16), StandardCharsets.UTF_8);
            if (fileStart.startsWith("{DYNAMIC_RENDER}")) {
                return new DynamicView(dynamicFile, model, dynamicContentRenderer);
            }
            return new StaticView(dynamicFile);
        }
    }

    private static String getViewPath(String viewUri, boolean isStatic) {
        return RESOURCE_PREFIX +
               (isStatic ? STATIC_VIEW : DYNAMIC_VIEW) +
               (viewUri.startsWith("/") ? "" : "/") +
               viewUri;
    }
}
