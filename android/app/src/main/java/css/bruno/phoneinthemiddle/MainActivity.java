package css.bruno.phoneinthemiddle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private InputView inputView;
    private EditText editTextIP;
    private Button buttonStart, buttonStop;
    private TimerTask timerTask;
    private Timer timer;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputView = (InputView) findViewById(R.id.main);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        inputView.setFocusable(true);
        inputView.requestFocus();
        inputView.reset();

        handler = new Handler();
        timer = new Timer(false);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                timer.cancel();
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.validIP(editTextIP.getText().toString())){

                    buttonStart.setEnabled( false);
                    buttonStop.setEnabled(true);
                    editTextIP.setEnabled(false);
                    inputView.findFocus();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    if (inputView.getKeyData() != null && inputView.getKeyData().size() > 0) {
                                        Map<String, String> m = inputView.getKeyData();
                                        m.put("TIME", String.valueOf(System.currentTimeMillis()));
                                        byte[] ipAddr = Helper.ipStrToBytes(editTextIP.getText().toString());

                                        if (inputView.get_newInput()) UDPClient.SendJSON(ipAddr, 8888, m);
                                        inputView.set_newInput(false);

                                    }
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask, 0, 32);
                }
            }
        });



    }


    @Override
    public void onBackPressed() {
        //Do nothing
        //Avoids app exit
    }


}
