package org.twitterwall;

import android.os.AsyncTask;
import android.widget.ListView;

class ScrollListViewToEndTask extends AsyncTask<Void,Void,Void> {

    	private ListView list_view;
    	
    	public ScrollListViewToEndTask(ListView list_view) {
    		this.list_view=list_view;
    		
    	}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (list_view.getCount()>list_view.getLastVisiblePosition()) {
				list_view.smoothScrollToPosition(list_view.getLastVisiblePosition()+1);
				// is a bit dangerous to call from here, but we as we expect <100 calls here I see no *big* deal
				new ScrollListViewToEndTask(list_view).execute();
			}

			super.onPostExecute(result);
		}
		
    	
    }
