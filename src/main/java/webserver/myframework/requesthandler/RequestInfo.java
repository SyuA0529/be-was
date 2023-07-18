package webserver.myframework.requesthandler;

import webserver.http.HttpMethod;

public abstract class RequestInfo {
    public abstract boolean isUri(String uri);

    public abstract boolean isHttpMethod(HttpMethod httpMethod);

    public static RequestInfo.Builder builder() {
        return new RequestInfoBuilderImpl();
    }

    public interface Builder {
        RequestInfo.Builder uri(String uri);
        RequestInfo.Builder httpMethod(HttpMethod httpMethod);
        RequestInfo build();
    }
}