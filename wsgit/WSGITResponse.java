package kr.co.studysearch.studysearch.wsgit;

import org.bson.BSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WSGITResponse {
    private Map<String, String> status = new HashMap<String, String>();
    private HTTPSignature signature;
    private BSONObject parameters;

    public WSGITResponse(BSONObject bsonObject) {
        BSONObject status = (BSONObject) bsonObject.get("status");
        this.status.put("code", new String((byte[])status.get("code"), Charset.forName("UTF-8")));
        this.status.put("reason", new String((byte[])status.get("code"), Charset.forName("UTF-8")));
        this.signature = HTTPSignature.parseFrom(bsonObject);
        if(bsonObject.containsField("response"))
            parameters = (BSONObject)bsonObject.get("response");
    }
}
