package kr.co.studysearch.studysearch.wsgit;

import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class NetworkManager {
    static private NetworkManager instance;
    private Socket socket;
    private Queue<Map<String, Object>> queue;

    private NetworkManager() {
        if(instance != null)
            throw new IllegalStateException("Cannot be constructed when instance is already exists");
        queue = new LinkedList<Map<String, Object>>();
    }

    public static NetworkManager getInstance() {
        if(instance == null)
            instance = new NetworkManager();
        return instance;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        if(!socket.isConnected())
            return;
        for(Map<String, Object> map : queue) {
            sendRequest(map);
        }
    }

    public void sendRequest(RequestMethod method, String url) {
        sendRequest(method, url, null);
    }

    public void sendRequest(RequestMethod method, String url, Map<String, Object> parameters) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("url", url);
        map.put("method", method.toString());
        if(parameters != null)
            map.putAll(parameters);
        sendRequest(map);
    }

    public void sendRequest(Map<String, Object> map) {
        if(socket == null) {
            queue.add(map);
            return;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        map.put("headers", headers);
        new SendingThread(socket, new BasicBSONObject(map)).start();
    }

    static private class SendingThread extends Thread {
        private Socket socket;
        private BSONObject bsonObject;

        public SendingThread(Socket socket, BSONObject bsonObject) {
            super();
            this.socket = socket;
            this.bsonObject = bsonObject;
        }

        @Override
        public void run() {
            try {
                BSONEncoder encoder = new BasicBSONEncoder();
                socket.getOutputStream().write(encoder.encode(bsonObject));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
