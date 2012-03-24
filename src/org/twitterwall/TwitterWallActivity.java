package org.twitterwall;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ligi.android.common.json.JSONHelper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TwitterWallActivity extends ListActivity {
		
	public final static int PAUSE_TIME=30000; // in ms
	private ProgressBar progress;
	private long pause_start;
	private Handler hndl;
	private String tag;
	private ImageLoader img_loader;
	
	class TwitterMessage {
		
		private String text;
		private String user_avatar;
		private String user_name;
		
	}
	
	Vector <TwitterMessage> messages=new Vector<TwitterMessage>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img_loader=ImageLoader.getInstance();
        img_loader.init(new ImageLoaderConfiguration.Builder(this).build());
		Bundle extras = getIntent().getExtras();
        tag="%23cbase";
        
        if ((extras!=null)&&(!extras.getString("PARAM").equals(""))) {
        	tag = extras.getString("PARAM");
        	tag.replace("#", "");
        }
        
        setContentView(R.layout.main);
        //WebView wv=(WebView)this.findViewById(R.id.webview);
        //wv.loadUrl("http://twittbee.com/"+tag+"/");
        
        progress=(ProgressBar)findViewById(R.id.progress_bar);
        progress.setMax(PAUSE_TIME);
        
        pause_start=System.currentTimeMillis();
        
        hndl=new Handler();
        
        
        new Thread(new ProgressUpdaterThread()).start();
        
        new GetTweetsAsync().execute();
    }       
    
    
    class GetTweetsAsync extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {

	        try {
	        	Log.i("TwitterWall","Connecting to twitter ");
	        	JSONArray arr=JSONHelper.getJSONObjectByURLString("http://search.twitter.com/search.json?q="+TwitterWallActivity.this.tag).getJSONArray("results");
	        	Log.i("TwitterWall","Connecting to twitter done");
	        	for (int i=0;i<arr.length();i++) {
					TwitterMessage msg=new TwitterMessage();
					JSONObject obj=arr.getJSONObject(i);
					msg.text=obj.getString("text");
					msg.user_avatar=obj.getString("profile_image_url");
					msg.user_name=obj.getString("from_user");
					messages.add(msg);
				}
					
			} catch (Exception e) {
				Log.i("TwitterWall","Connecting to err " + e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			TwitterWallActivity.this.setListAdapter(new MyAdapter());
			new ScrollListViewToEndTask(getListView()).execute();
			super.onPostExecute(result);
		}
    	
    }
    
        
    class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View res=TwitterWallActivity.this.getLayoutInflater().inflate(R.layout.row,null);
			
			ImageView img=(ImageView)res.findViewById(R.id.avatar_image);
			img_loader.displayImage(messages.get(position).user_avatar, img);
			TextView tv=(TextView)res.findViewById(R.id.text);
			tv.setText(Html.fromHtml(messages.get(position).text));
			
			TextView user_tv=(TextView)res.findViewById(R.id.uname);
			user_tv.setText(messages.get(position).user_name);
			
			return res;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
		
    }
    
    public void done() {
    	finish();
    }
    
    public class ProgressUpdaterThread implements Runnable {
		
		@Override
		public void run() {
			while ((System.currentTimeMillis()-pause_start)<PAUSE_TIME) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {		}
				
				hndl.post(new ProgressUpdater());
			}	
			done();
		}
	}

    public class ProgressUpdater implements Runnable {
		@Override
		public void run() {
			progress.setProgress(progress.getMax()-(int)(System.currentTimeMillis()-pause_start));
		}
	}
    
}