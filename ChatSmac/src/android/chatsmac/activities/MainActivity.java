package android.chatsmac.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.chatsmac.adapter.ChatAdapter;
import android.chatsmac.model.ChatMessage;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.example.chatsmac.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	protected static final int RESULT_SPEECH = 1;
	private EditText messageEditText;
	private ChatAdapter adapter;
	private ListView messagesContainer;
	String textDisplay;
	Button sendButton, microButton;

	public static void start(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	private void initViews() {
		messagesContainer = (ListView) findViewById(R.id.messagesContainer);
		messageEditText = (EditText) findViewById(R.id.messageEdit);
		sendButton = (Button) findViewById(R.id.chatSendButton);
		microButton = (Button) findViewById(R.id.chatMicroButton);

		adapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
		messagesContainer.setAdapter(adapter);

		ButtonListener listener = new ButtonListener();

		microButton.setOnClickListener(listener);
		sendButton.setOnClickListener(listener);

		messageEditText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (messageEditText.getText().toString().equals("")) {
					microButton.setVisibility(View.VISIBLE);
					sendButton.setVisibility(View.GONE);
				} else {
					microButton.setVisibility(View.GONE);
					sendButton.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	public void showMessage(ChatMessage message) {
		adapter.add(message);
		adapter.notifyDataSetChanged();
		scrollDown();
	}

	public void showMessage(List<ChatMessage> messages) {
		adapter.add(messages);
		adapter.notifyDataSetChanged();
		scrollDown();
	}

	private void scrollDown() {
		messagesContainer.setSelection(messagesContainer.getCount() - 1);
	}

	public static enum Mode {
		SINGLE, GROUP
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				JSONObject jsonObj = new JSONObject(result);
				textDisplay = jsonObj.getString("response");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			showMessage(new ChatMessage(textDisplay, Calendar.getInstance().getTime(),
					true));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.custom_actionbar);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				String request = text.get(0).replace(" ", "%20");
				Log.d("Http Get Response:", request);
				new HttpAsyncTask()
						.execute("http://tech.fpt.com.vn/AIML/api/bots/53cb764de4b04a9d44599aa6/chat?request="
								+ request
								+ "&token=eb086f7b-3b11-4ab9-984f-7f3f1f36147a");

				showMessage(new ChatMessage(text.get(0), Calendar.getInstance()
						.getTime(), false));
			}
			break;
		}

		}
	}

	private class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(findViewById(R.id.chatSendButton))) {

				String lastMsg = messageEditText.getText().toString();
				String request = lastMsg.replace(" ", "%20");
				Log.d("Http Get Response:", request);
				new HttpAsyncTask()
						.execute("http://tech.fpt.com.vn/AIML/api/bots/53cb764de4b04a9d44599aa6/chat?request="
								+ request
								+ "&token=eb086f7b-3b11-4ab9-984f-7f3f1f36147a");

				showMessage(new ChatMessage(lastMsg, Calendar.getInstance()
						.getTime(), false));
				messageEditText.setText("");
			} else {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
				} catch (ActivityNotFoundException a) {
					Toast t = Toast
							.makeText(
									getApplicationContext(),
									"Thiết bị của bạn không hỗ trợ chuyển giọng nói thành văn bản",
									Toast.LENGTH_SHORT);
					t.show();
				}
			}
		}
	}
}
