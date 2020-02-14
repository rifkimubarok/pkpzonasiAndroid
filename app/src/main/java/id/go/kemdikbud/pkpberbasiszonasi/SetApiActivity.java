package id.go.kemdikbud.pkpberbasiszonasi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;

import id.go.kemdikbud.pkpberbasiszonasi.R;

public class SetApiActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_api);
        editText = (EditText) findViewById(R.id.text_api);
        button = (Button) findViewById(R.id.btn_set_api);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString();
                ApiService apiService = new ApiService(getApplicationContext());
                apiService.setEndPointAPI(url);
                String endpoint = apiService.getEndPointAPI();
                Toast.makeText(getApplicationContext(),endpoint,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
