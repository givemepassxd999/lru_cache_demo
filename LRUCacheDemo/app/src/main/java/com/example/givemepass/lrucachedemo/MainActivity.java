package com.example.givemepass.lrucachedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

	private String[] mImgsPath = {
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/01.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/02.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/03.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/04.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/05.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/06.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/07.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/08.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/09.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/10.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/11.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/12.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/13.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/14.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/15.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/16.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/17.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/18.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/19.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/20.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/21.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/22.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/23.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/24.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/25.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/26.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/27.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/28.jpg",
			"https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/29.jpg"
	};

	private ListView mListView;

	private BaseAdapter mAdapter;

	private LruCache<String, Bitmap> mLruCache;

	private HandlerThread mHandlerThread;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandlerThread = new HandlerThread("LRU Cache Handler");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 2;

		mLruCache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount() / 1024;
			}
		};
		mListView = (ListView) findViewById(R.id.list);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);

	}

	private class MyAdapter extends BaseAdapter {
		private Map<String, String> mLoadingMap;

		public MyAdapter() {
			mLoadingMap = new HashMap<String, String>();
		}

		@Override
		public int getCount() {
			return mImgsPath.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final Holder holder;
			if(null == v){
				v = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, null);
				holder = new Holder();
				holder.img = (ImageView) v.findViewById(R.id.img);
				v.setTag(holder);
			} else{
				holder = (Holder) v.getTag();
			}
			holder.img.setImageResource(R.drawable.default_img);
			final String key = position + "_cache";
			Bitmap b = mLruCache.get(key);
			if(b == null && !mLoadingMap.containsKey(key)) {
				mLoadingMap.put(key, mImgsPath[position]);
				Log.e("lru", "load pic" + position);
				mHandler.post(new Runnable() {
					Bitmap bmp;
					@Override
					public void run() {
						bmp = decodeBitmap(mImgsPath[position], 200);
						mLruCache.put(key, bmp);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								notifyDataSetChanged();
								mLoadingMap.remove(key);
							}
						});
					}
				});

			} else{
				Log.e("lru", "cache");
				holder.img.setImageBitmap(b);
			}
			return v;
		}
		class Holder{
			ImageView img;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeBitmap(String url, int maxWidth){

		Bitmap bitmap = null;
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inSampleSize = calculateInSampleSize(options, maxWidth, maxWidth);

			InputStream is = (InputStream) new URL(url).getContent();
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (MalformedInputException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
