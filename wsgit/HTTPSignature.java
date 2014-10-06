package kr.co.studysearch.studysearch.wsgit;

import org.bson.BSONObject;

import java.util.Map;

import lombok.Getter;

@Getter
public class HTTPSignature {
    private RequestMethod method;
    private String url;

    public HTTPSignature(RequestMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    public static HTTPSignature parseFrom(BSONObject bsonObject) {
        RequestMethod method = null;
        if(bsonObject.containsField("method")) {
            method = RequestMethod.valueOf((String) bsonObject.get("method"));
        }
        String url = (String) bsonObject.get("url");
        return new HTTPSignature(method, url);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof HTTPSignature))
            return false;
        if(obj == this)
            return true;
        HTTPSignature signature = (HTTPSignature) obj;
        if(signature.getMethod() != this.method)
            return false;
        if(!signature.getUrl().equals(this.url))
            return false;
        return true;
    }
}
