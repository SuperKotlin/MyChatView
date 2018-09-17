package com.zhuyong.customchatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MyChatView view1;
    private TextView tv_text;
    private List<List<MyModel>> mListAll = new ArrayList<>();

    /**
     * 获取随机数
     *
     * @param range
     * @param startsfrom
     * @return
     */
    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = (MyChatView) findViewById(R.id.view1);
        tv_text = (TextView) findViewById(R.id.tv_text);

        for (int i = 0; i < 3; i++) {
            List<MyModel> item = new ArrayList<>();
            for (int i1 = 0; i1 < 15; i1++) {
                item.add(new MyModel(i1, getRandom(1000, 500)));
            }
            mListAll.add(item);
        }

        view1.setData(mListAll);

        view1.setOnClickListener(new MyChatView.OnClickListener() {
            @Override
            public void click(int position) {
                update(position);
            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 设置默认选中第几组数据
                 */
                view1.setPosition(new Random().nextInt(15));
            }
        });
    }

    private void update(int position) {
        tv_text.setText("");
        tv_text.append("第" + (position + 1) + "组:\n");
        for (int i = 0; i < mListAll.size(); i++) {
            tv_text.append("第" + i + "个数据：" + mListAll.get(i).get(position).getVal() + "\n");
        }

    }

}
