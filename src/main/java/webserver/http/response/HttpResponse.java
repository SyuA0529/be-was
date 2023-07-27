package webserver.http.response;


import webserver.http.ContentType;
import webserver.http.HttpHeaders;

import java.util.List;

public abstract class HttpResponse {
    public static final String NOT_RENDER_URI = "NOT_RENDER_URI";

    public abstract void forward(String uri);

    public abstract String getUri();

    public abstract void setStatus(HttpStatus httpStatus);

    public abstract HttpStatus getStatus();

    public abstract HttpHeaders getHeaders();

    public abstract void setHeader(String headerName, String value);

    public abstract void addCookie(Cookie cookie);

    public abstract List<Cookie> getCookies();

    public abstract byte[] getBody();

    public abstract void setBody(byte[] body);

    public abstract void setContentType(ContentType contentType);

    public abstract ContentType getContentType();

    public abstract void sendRedirection(String uri);

    public static HttpResponse getInstance() {
        return new HttpResponseImpl();
    }
}
