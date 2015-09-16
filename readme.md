如果要使用圖片Cache, 
Android官網[LRUCache](http://developer.android.com/intl/zh-cn/reference/android/util/LruCache.html)和[Cache Image](http://developer.android.com/intl/zh-cn/training/displaying-bitmaps/cache-bitmap.html)都建議使用LRU Cache, 
他可以限定cache的大小並且把最少使用的cache清除掉。

所以我們用ListView呈現從網路上抓下來的圖片
```java
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
```
[圖片來源](https://newevolutiondesigns.com/50-fresh-hd-wallpapers)

LRUCache的使用方法很簡單

```java
		private LruCache<String, Bitmap> mLruCache;
		//宣告的時候, 可以設定chache多大
    mLruCache = new LruCache<String, Bitmap>(cacheSize){
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getByteCount() / 1024;
				}
		};
    //如果要使用的時候
    mLruCache.put(key, bmp);
    //然後取出
    Bitmap b = mLruCache.get(key);
```
類似Map的存取方式, 比較不同的是他會自動清除內部的資料。

接著利用[如何使用HandlerThread](http://givemepass-blog.logdown.com/posts/296790-how-to-use-handlerthread)來宣告一個Thread, 用來處理從網路上下載的圖片。

```java
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
```
前面先把HandlerThread宣告出來, 接著在宣告出LRUCache, 用來存放我們的Map,
利用Runtime.getRuntime().maxMemory()方法可以得知硬體最大cache放到多少,
這樣就不會發生OOM。

從官網這篇[Loading Large Bitmaps Efficiently](http://developer.android.com/intl/zh-cn/training/displaying-bitmaps/load-bitmap.html), 建議利用縮圖的方式來進行讀圖。

```java
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
```
因此改寫了從網路上抓取以及縮圖的兩個方法。

接下來是Adapter重頭戲部分。

```java
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
```
這邊利用網路讀圖, 然後呈現出來, 
當中利用了三個小技巧, 
一個是當讀完圖的時候, 不是直接設定到ImageView而是去刷新整個頁面, 
這樣一來就會統一讀取cache, 如果直接設定ImageView,
當上下滑動快速的時候, 就會不斷的更新之前滑過的部分, 直到最新的image。

第二個技巧就是利用HandlerThread去進行抓圖任務, HandlerThread的好處就是執行緒安全,
他會一個接一個任務去抓, 因此不會出現同時兩個任務共同存取cache,
但是壞處是滑過的地方會循序, 這邊還可以用其他方法改進。

第三個技巧就是利用Map讓正在Queue等待的Task, 不會重覆,
如果少了這個Queue, 我們印出的訊息就會長這樣。

```java
load pic6
load pic7
load pic8
load pic9
load pic10
load pic7
load pic6
load pic5
load pic4
load pic3
load pic2
load pic1
load pic0
load pic0
load pic1
load pic2
```
同時出現0、1、2已經在Queue內了還會重覆出現。

<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/01.png">
<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/02.png">
<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/03.png">
