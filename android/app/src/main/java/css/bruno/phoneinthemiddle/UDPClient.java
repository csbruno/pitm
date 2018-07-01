package css.bruno.phoneinthemiddle;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class UDPClient {

    public static void SendJSON(final byte[] ipAddr, final int port, final Map<String, String> hashMap) {

        final String msg = (new JSONObject(hashMap).toString());

        AsyncTask<Void, Void, Void> async_cient;

        async_cient = new AsyncTask<Void, Void, Void>() {
            DatagramSocket datagramSocket;
            @Override
            protected Void doInBackground(Void... params) {

                try {
                   datagramSocket = new DatagramSocket(port);

                    InetAddress addr = InetAddress.getByAddress(ipAddr);

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