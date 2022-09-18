package com.rumble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class STTActivity extends AppCompatActivity {

    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1; // 권한 변수

    boolean recording = false; // 현재 녹음 중인지 확인
    TextView recordTextView;
    ImageButton recordBtn;

    EditText contents; // 음성을 텍스트로 변환한 결과를 출력할 텍스트뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt_activity);

        CheckPermission(); // 녹음 권한 확인

        // UI
        recordBtn = findViewById(R.id.recordBtn);
        recordTextView = findViewById(R.id.recordTextView);
        contents = findViewById(R.id.contentsTextView);

        // 버튼 클릭 이벤트 리스터 등록
        recordBtn.setOnClickListener(click);

        //RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어
    }

    // 녹음 버튼 누를 시
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //녹음 버튼
                case R.id.recordBtn:
                    if (!recording) {   //녹음 시작
                        StartRecord();
                        Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                    } else {  //이미 녹음 중이면 녹음 중지
                        StopRecord();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);    //인식 결과를 담은 ArrayList
            String originText = contents.getText().toString();  //기존 text

            //인식 결과
            String newText = "";
            for (int i = 0; i < matches.size(); i++) {
                newText += matches.get(i);
            }

            contents.setText(originText + newText + " ");    //기존의 text에 인식 결과를 이어붙임
            speechRecognizer.startListening(intent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    // 권한 체크
    void CheckPermission() {
        // 안드로이드 버전이 6 이상
        if (Build.VERSION.SDK_INT >= 23) {
            // 권한 없을 시 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }
        }
    }

    // 녹음 시작
    void StartRecord() {
        recording = true;

        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        recordTextView.setText("음성 녹음 중지");

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;

        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        recordTextView.setText("음성 녹음 시작");

        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

}