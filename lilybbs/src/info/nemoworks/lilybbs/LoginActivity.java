package info.nemoworks.lilybbs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);

		Button b = (Button) this.findViewById(R.id.loginbutton);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText username = (EditText) LoginActivity.this.findViewById(R.id.login);
				EditText password = (EditText) LoginActivity.this.findViewById(R.id.password);

				if (username.getText().toString().length() > 0 && password.getText().toString().length() > 0) {
					if (username.getText().toString().equals("wr") && password.getText().toString().equals("wr")) {

						Intent intent = new Intent(LoginActivity.this, NaviActivity.class);
						LoginActivity.this.startActivity(intent);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
