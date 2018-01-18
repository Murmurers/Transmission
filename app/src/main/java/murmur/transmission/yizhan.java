package murmur.transmission;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class yizhan extends AppCompatActivity {
    private Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yizhan);
        bt1=(Button) findViewById(R.id.fujin);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(yizhan.this,fujin.class);
                startActivity(intent);

            }
        });
    }
}
