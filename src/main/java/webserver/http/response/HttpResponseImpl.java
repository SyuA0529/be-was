package webserver.http.response;

import webserver.http.ContentType;
import webserver.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;


public class HttpResponseImpl extends HttpResponse {
    private static final String redirectionHeader = "Location";
    private String uri = HttpResponse.NOT_RENDER_URI;
    private HttpStatus httpStatus = HttpStatus.OK;
    private ContentType contentType = ContentType.HTML;
    private final HttpHeaders headers = new HttpHeaders();
    private final List<Cookie> cookies = new ArrayList<>();
    private byte[] body = new byte[0];

    @Override
    public void forward(String uri) {
        this.uri = uri;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getStatus() {
        return this.httpStatus;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public void setHeader(String headerName, String value) {
        headers.addHeader(headerName, value);
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    @Override
    public void sendRedirection(String uri) {
        setStatus(HttpStatus.FOUND);
        setHeader(redirectionHeader, uri);
    }
}
