package css.bruno.phoneinthemiddle;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class UDPClient {

    DatagramSocket datagramSocket;
    private int port;
    private byte[] ipAddress;

    public UDPClient(byte[] ipAddr, int port) {
        this.port = port;
        this.ipAddress = ipAddr;
        openComm();

    }

    private boolean openComm() {
        if (datagramSocket != null && datagramSocket.isClosed()) {
            try {

                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }

    }

    public void SendJSON(final Map<String, String> hashMap) {

        final String msg = (new JSONObject(hashMap).toString());

        AsyncTask<Void, Void, Void> async_cient;

        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    if (datagramSocket == null) datagramSocket = new DatagramSocket(port);

                    InetAddress addr = InetAddress.getByAddress(ipAddress);

                    DatagramPacket dp;
                    dp = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, port);
                    datagramSocket.setBroadcast(true);

                    datagramSocket.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();

                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();

    }


}