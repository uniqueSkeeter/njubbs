package info.nemoworks.lilybbs;

import java.util.ArrayList;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class TopActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_top);
		new DownloadTask().execute("http://bbs.nju.edu.cn/bbstop10");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.top, menu);
		return true;
	}

	private class DownloadTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... urls) {
			try {
				return TopActivity.this.parseTop10();
			} catch (ParserException e) {
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

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(TopActivity.this, android.R.layout.simple_list_item_1,
					results);

			TopActivity.this.setListAdapter(adapter);

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

	private String[] parseTop10() throws ParserException {
		final String DW_HOME_PAGE_URL = "http://bbs.nju.edu.cn/bbstop10";
		ArrayList<String> pTitleList = new ArrayList<String>();
		// 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
		Parser htmlParser = new Parser(DW_HOME_PAGE_URL);
		htmlParser.setEncoding("UTF-8");
		String postTitle = "";
		// 获取指定的 div 节点，即 <div> 标签，并且该标签包含有属性 id 值为“tab1”
		NodeList toptable = htmlParser.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableTag.class),
				new HasAttributeFilter("width", "640")));

		if (toptable != null && toptable.size() > 0) {
			// 获取指定 div 标签的子节点中的 <li> 节点
			NodeList itemTopList = toptable.elementAt(0).getChildren()
					.extractAllNodesThatMatch((new NodeClassFilter(TableRow.class)), true);

			if (itemTopList != null && itemTopList.size() > 0) {
				for (int i = 0; i < itemTopList.size() - 1; ++i) {
					// 在 <li> 节点的子节点中获取 Link 节点
					NodeList linkItem = itemTopList.elementAt(i).getChildren()
							.extractAllNodesThatMatch(new NodeClassFilter(TableColumn.class), true);
					if (linkItem != null && linkItem.size() > 0) {
						// 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
						postTitle = ((LinkTag) ((linkItem.elementAt(7)).getChildren().elementAt(0))).getLinkText();
						System.out.println(postTitle);
						pTitleList.add(postTitle);
					}
				}
			}
		}
		String[] results = new String[pTitleList.size()];
		pTitleList.toArray(results);
		return results;
	}
}
