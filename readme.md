�p�G�n�ϥιϤ�Cache, 
Android�x��[LRUCache](http://developer.android.com/intl/zh-cn/reference/android/util/LruCache.html)�M[Cache Image](http://developer.android.com/intl/zh-cn/training/displaying-bitmaps/cache-bitmap.html)����ĳ�ϥ�LRU Cache, 
�L�i�H���wcache���j�p�åB��̤֨ϥΪ�cache�M�����C

�ҥH�ڭ̥�ListView�e�{�q�����W��U�Ӫ��Ϥ�
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
[�Ϥ��ӷ�](https://newevolutiondesigns.com/50-fresh-hd-wallpapers)

LRUCache���ϥΤ�k��²��

```java
		private LruCache<String, Bitmap> mLruCache;
		//�ŧi���ɭ�, �i�H�]�wchache�h�j
    mLruCache = new LruCache<String, Bitmap>(cacheSize){
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getByteCount() / 1024;
				}
		};
    //�p�G�n�ϥΪ��ɭ�
    mLruCache.put(key, bmp);
    //�M����X
    Bitmap b = mLruCache.get(key);
```
����Map���s���覡, ������P���O�L�|�۰ʲM����������ơC

���ۧQ��[�p��ϥ�HandlerThread](http://givemepass-blog.logdown.com/posts/296790-how-to-use-handlerthread)�ӫŧi�@��Thread, �ΨӳB�z�q�����W�U�����Ϥ��C

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
�e������HandlerThread�ŧi�X��, ���ۦb�ŧi�XLRUCache, �ΨӦs��ڭ̪�Map,
�Q��Runtime.getRuntime().maxMemory()��k�i�H�o���w��̤jcache���h��,
�o�˴N���|�o��OOM�C

�q�x���o�g[Loading Large Bitmaps Efficiently](http://developer.android.com/intl/zh-cn/training/displaying-bitmaps/load-bitmap.html), ��ĳ�Q���Y�Ϫ��覡�Ӷi��Ū�ϡC

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
�]����g�F�q�����W����H���Y�Ϫ���Ӥ�k�C

���U�ӬOAdapter���Y�������C

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
�o��Q�κ���Ū��, �M��e�{�X��, 
���Q�ΤF�T�Ӥp�ޥ�, 
�@�ӬO��Ū���Ϫ��ɭ�, ���O�����]�w��ImageView�ӬO�h��s��ӭ���, 
�o�ˤ@�ӴN�|�Τ@Ū��cache, �p�G�����]�wImageView,
��W�U�ưʧֳt���ɭ�, �N�|���_����s���e�ƹL������, ����̷s��image�C

�ĤG�ӧޥ��N�O�Q��HandlerThread�h�i���ϥ���, HandlerThread���n�B�N�O������w��,
�L�|�@�ӱ��@�ӥ��ȥh��, �]�����|�X�{�P�ɨ�ӥ��Ȧ@�P�s��cache,
���O�a�B�O�ƹL���a��|�`��, �o���٥i�H�Ψ�L��k��i�C

�ĤT�ӧޥ��N�O�Q��Map�����bQueue���ݪ�Task, ���|����,
�p�G�֤F�o��Queue, �ڭ̦L�X���T���N�|���o�ˡC

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
�P�ɥX�{0�B1�B2�w�g�bQueue���F�ٷ|���ХX�{�C

<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/01.png">
<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/02.png">
<img class="left" src="https://dl.dropboxusercontent.com/u/24682760/Android_AS/LRUCacheDemo/03.png">
