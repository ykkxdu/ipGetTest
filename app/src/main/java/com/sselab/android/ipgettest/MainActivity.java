package com.sselab.android.ipgettest;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.sselab.android.ipgettest.mail.JavaMail;
public class MainActivity extends AppCompatActivity {
    private EditText mEditText;
    String value=null;
    Handler handler;
    thread mthread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("d.................");
        mEditText= (EditText) findViewById(R.id.textView_id);
        mthread=new thread();
        mthread.start();
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mEditText.setText("ip:"+msg.obj.toString());
            }
        };
    }
    class  thread extends Thread
    {
        @Override
        public void run() {
            super.run();
            System.out.println("进入线程成功:");
            try {
              value= JavaMail.receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message message=handler.obtainMessage();
            message.obj=value;
            handler.sendMessage(message);
        }
    }
}


