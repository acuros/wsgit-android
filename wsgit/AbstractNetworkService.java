package kr.co.studysearch.studysearch.wsgit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

abstract public class AbstractNetworkService extends Service {

    private SocketConnectingThread connectingThread = null;

    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        if (!isNetworkAvailable()) {
            return START_STICKY;
        }
        connectingThread = new SocketConnectingThread();
        connectingThread.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
                cm.getActiveNetworkInfo() != null;
        if(!isNetworkAvailable) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(AbstractNetworkService.this, "No Network!", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        return isNetworkAvailable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectingThread.interrupt();
    }

    private class SocketConnectingThread extends Thread {

        public void run() {
            Socket socket = new Socket();
            SocketAddress address = getSocketAddress();
            try {
                socket.connect(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
            NetworkManager.getInstance().setSocket(socket);
            if (!socket.isConnected()) {
                return;
            }

            InputStream is = null;
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (socket.isConnected()) {
                DataInputStream in =new DataInputStream(is);
                try {
                    byte[] sizeBytes = new byte[4];
                    in.read(sizeBytes, 0, 4);
                    int size = (sizeBytes[3]&0xff) << 24 | (sizeBytes[2]&0xff) << 16 | (sizeBytes[1]&0xff) << 8 | sizeBytes[0]&0xff;
                    byte[] bsonBytes = new byte[size];
                    System.arraycopy(sizeBytes, 0, bsonBytes, 0, 4);
                    in.readFully(bsonBytes, 4, bsonBytes.length - 4);
                    onNewBsonBytesLoaded(bsonBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract SocketAddress getSocketAddress();

    abstract public void onNewBsonBytesLoaded(byte [] bytes);
}
