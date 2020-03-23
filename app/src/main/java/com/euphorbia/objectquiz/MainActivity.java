package com.euphorbia.objectquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView TimeCounter;
    private TextView text;
    private TextView Quiz;
    private TextView Answer;
    private ImageView imageView;
    private EditText QuizCount;
    private EditText QuizTimeCount;
    private Button startButton;

    private SeekBar sk;

    private Thread Qthread = null;
    private Thread Athread = null;

    int max = 91; // 문제수

    private int[] imgs = new int[max];

    int j = 0; // j번째 문제

    int k = 0; // 사용자가 입력한 문제수

    int l = 0; // 사용자가 입력한 문제 제한시간

    int count = 0;

    ArrayList<Integer> QuizList = new ArrayList<>();

    String[] name = new String[]{"각도기", "슬레이트", "컴퍼스", "셔틀콕", "아코디언", "연필깎이", "포스트잇", "멀티탭", "나침반", "소화기", "마카롱", "스테이플러", "성냥(성냥개비)", "리모컨", "지구본", "하모니카", "선글라스", "타이어", "다트", "빨대", "빨래집게", "텀블러(보온병)", "손전등", "돋보기", "코르크마개", "실로폰",
            "분필", "당구(당구공)", "골프공", "타로", "양초", "인라인 스케이트", "모기향", "자물쇠", "금고", "클립", "핀셋", "압정", "나사(못x)", "드라이버", "립스틱", "수정테이프", "드론", "셀카봉", "확성기", "다리미", "레고", "와플", "열기구", "볼링핀", "탬버린", "여권", "유모차", "건전지", "모래시계", "USB", "트로피", "메달",
            "현미경", "아령", "콘센트", "캐리어", "큐브", "주사위", "젠가", "도미노", "퍼즐", "체스", "저금통", "이쑤시개", "온도계", "QR코드", "바코드",
            "옷걸이", "주사기", "바늘", "디퓨저", "넥타이", "오카리나", "회전목마", "체중계", "저울", "물총", "(고무)장화", "고글", "카펫", "로봇청소기", "정글짐", "화투", "리코더", "벼루"};

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < max; i++) {
            imgs[i] = getResources().getIdentifier("image" + i, "drawable", getPackageName());
        }

        MobileAds.initialize(this, "@string/admob_app_id");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("@string/banner_ad_unit_id");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        TimeCounter = findViewById(R.id.TimeCounter);
        text = findViewById(R.id.text);
        Quiz = findViewById(R.id.Quiz);
        Answer = findViewById(R.id.Answer);
        imageView = findViewById(R.id.imageView);
        QuizCount = findViewById(R.id.QuizCount);
        QuizTimeCount = findViewById(R.id.QuizTimeCount);
        startButton = findViewById(R.id.startButton);

        sk = findViewById(R.id.sk);
        sk.setMax(5);

        sk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QuizCount.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "문제수를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(QuizCount.getText().toString()) > max) {
                    Toast.makeText(getApplicationContext(), max + 1 + "보다 작은 수를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(QuizCount.getText().toString()) == 0) {
                    Toast.makeText(getApplicationContext(), "0보다 큰 수를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (QuizTimeCount.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "제한시간을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(QuizTimeCount.getText().toString()) == 0) {
                    Toast.makeText(getApplicationContext(), "0보다 큰 수를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(QuizCount.getWindowToken(), 0);

                count = 0;

                QuizCount.setVisibility(View.GONE);
                QuizTimeCount.setVisibility(View.GONE);
                startButton.setVisibility(View.GONE);

                k = Integer.parseInt(QuizCount.getText().toString());
                l = Integer.parseInt(QuizTimeCount.getText().toString());

                j = randomRange(0, max - 1);
                QuizList.add(j);
                imageView.setImageResource(imgs[j]);
                text.setText("이것은 무엇일까요?");
                sk.setMax(l);
                Qthread = new Thread(new QtimeThread());
                Qthread.start();

            }
        });


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Quiz.setText(count + 1 + "/" + k);

            String to = Integer.toString(msg.arg1);
            TimeCounter.setText(to);

            if (msg.arg1 == 0) {
                Qthread.interrupt();
                Answer.setText(name[j]);
                text.setText("정답 공개!");
                sk.setMax(2);
                Athread = new Thread(new AtimeThread());
                Athread.start();


            }
        }
    };

    public class QtimeThread implements Runnable {
        @Override
        public void run() {
            int i = l;

            while (true) {
                Message msg = new Message();
                msg.arg1 = i--;
                sk.setProgress(i + 1);
                handler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return됨
                }

            }
        }

    }

    Handler Ahandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String to = Integer.toString(msg.arg1);
            TimeCounter.setText(to);

            if (msg.arg1 == 0) {

                Answer.setText("");
                Athread.interrupt();

                count++;
                if (count >= k) {

                    text.setText("문제를 맞춰보세요!");
                    TimeCounter.setText("");
                    Answer.setText("");
                    Quiz.setText("");
                    QuizCount.setVisibility(View.VISIBLE);
                    QuizTimeCount.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                    QuizCount.setText("");
                    QuizTimeCount.setText("");
                    imageView.setImageResource(0);
                    QuizList.clear();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    return;
                }


                j = randomRange(0, max - 1);

                while (true) {

                    if (!QuizList.contains(j)) { // 포함되어있지 않다면
                        QuizList.add(j);
                        break;
                    } else { // 포함되어 있다면
                        j = randomRange(0, max - 1);
                    }
                }

                imageView.setImageResource(imgs[j]);

                text.setText("이것은 무엇일까요?");

                sk.setMax(l);
                Qthread = new Thread(new QtimeThread());
                Qthread.start();


            }
        }
    };

    public class AtimeThread implements Runnable {
        @Override
        public void run() {
            int i = 2;

            while (true) {
                Message msg = new Message();
                msg.arg1 = i--;
                sk.setProgress(i + 1);
                Ahandler.sendMessage(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return됨
                }

            }
        }

    }

    public static int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
}