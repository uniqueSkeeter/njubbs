package info.nemoworks.lilybbs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ReadActivity extends Activity {

	private TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_viewfile);
		this.tv = (TextView) this.findViewById(R.id.view_contents);
		String content = this.getIntent().getStringExtra("num");
		new DownloadTask().execute(content);
	}

	class DownloadTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... urls) {
			try {
				return ReadActivity.this.getContent(urls[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Uses the logging framework to display the output of the fetch operation in the log fragment.
		 */
		@Override
		protected void onPostExecute(String[] results) {
			// Log.i(TAG, result);

			ReadActivity.this.tv.setText(results[0]);

		}
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

	private String[] getContent(String num) throws IOException {
		int number = Integer.parseInt(num) + 1;
		URL url = new URL("http://bbs.nju.edu.cn/bbstop10");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = conn.getInputStream(); // 通过输入流获得网站数据
		byte[] getData = readInputStream(inputStream); // 获得网站的二进制数据
		String data = new String(getData, "gb2312");
		data = data.split("第 " + number + " 名")[1].split("\">")[0].split("href=\"")[1];
		url = new URL("http://bbs.nju.edu.cn/" + data);
		conn = (HttpURLConnection) url.openConnection();
		inputStream = conn.getInputStream(); // 通过输入流获得网站数据
		getData = readInputStream(inputStream); // 获得网站的二进制数据
		data = new String(getData, "gb2312");
		data = "发信人" + data.split("textarea")[1].split("发信人")[1].split("</")[0];
		System.out.println(data);

		String result[] = new String[1];
		result[0] = data;
		return result;
	}

	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}

		bos.close();
		return bos.toByteArray();
	}

}
