package css.bruno.phoneinthemiddle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private ArrayList<Integer> controllersIDS;
    private InputView inputView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputView = (InputView) findViewById(R.id.main);
        inputView.setFocusable(true);
        inputView.requestFocus();

        inputView.reset();
        final Handler handler = new Handler();
        Timer timer = new Timer(false);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (inputView.getKeyData() != null && inputView.getKeyData().size() > 0) {
                            Map<String, String> m = inputView.getKeyData();
                            m.put("TIME", String.valueOf(System.currentTimeMillis()));
                            byte[] ipAddr = new byte[]{(byte) 10, (byte) 0, (byte) 1, (byte) 16};

                            if (inputView.get_newInput()) (new UDPClient(ipAddr, 8888)).SendJSON(m);
                            inputView.set_newInput(false);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 32);

    }


    @Override
    public void onBackPressed() {
        //Do nothing
        //Avoids app exit
    }


}
