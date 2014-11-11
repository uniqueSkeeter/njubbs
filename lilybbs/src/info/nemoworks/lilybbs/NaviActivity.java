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
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NaviActivity extends ListActivity {

	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;

	private CharSequence mTitle;

	private String[] mPlanetTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_navi);

		this.mTitle = this.mDrawerTitle = this.getTitle();
		// 把标签组放入数组中
		this.mPlanetTitles = this.getResources().getStringArray(R.array.planets_array);
		this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
		this.mDrawerList = (ListView) this.findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		this.mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		this.mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, this.mPlanetTitles));
		this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		this.mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		this.mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {

			@Override
			public void onDrawerClosed(View view) {
				NaviActivity.this.getActionBar().setTitle(NaviActivity.this.mTitle);
				NaviActivity.this.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				NaviActivity.this.getActionBar().setTitle(NaviActivity.this.mDrawerTitle);
				NaviActivity.this.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);

		if (savedInstanceState == null) {
			this.selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.navi, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = this.mDrawerLayout.isDrawerOpen(this.mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (this.mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
			case R.id.action_websearch:
				// create intent to perform web search for this planet
				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, this.getActionBar().getTitle());
				// catch event that there's no activity to handle intent
				if (intent.resolveActivity(this.getPackageManager()) != null) {
					this.startActivity(intent);
				} else {
					Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			NaviActivity.this.selectItem(position);
		}
	}

	private void selectItem(int position) {
		System.out.println(position);
		if (position == 0) {
			new DownloadTask().execute("http://bbs.nju.edu.cn/bbstop10");
		}
		// update selected item and title, then close the drawer
		this.mDrawerList.setItemChecked(position, true);
		this.setTitle(this.mPlanetTitles[position]);
		this.mDrawerLayout.closeDrawer(this.mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		this.mTitle = title;
		this.getActionBar().setTitle(this.mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		this.mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		this.mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DownloadTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... urls) {
			try {
				return NaviActivity.this.parseTop10();
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

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(NaviActivity.this, android.R.layout.simple_list_item_1,
					results);

			NaviActivity.this.setListAdapter(adapter);

		}
	}

	private String[] parseTop10() throws ParserException {
		// System.out.println("INININININININNINNINININI!!!!!!!!!!!!!!!!");
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
