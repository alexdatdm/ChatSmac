package android.chatsmac.activities;

import android.app.Activity;
import android.content.Intent;
import android.example.chatsmac.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	EditText user;
	EditText pass;
	Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		user = (EditText) findViewById(R.id.user);
		pass = (EditText) findViewById(R.id.pass);
		login = (Button) findViewById(R.id.login);

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((user.getText().toString().equals("admin"))
						&& (pass.getText().toString().equals("1234"))) {
					Intent i = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(i);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Login fail",
							Toast.LENGTH_SHORT).show();
				}

			};
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().hide();
		return true;
	}

}
