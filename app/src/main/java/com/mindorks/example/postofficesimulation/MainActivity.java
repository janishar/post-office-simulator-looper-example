package com.mindorks.example.postofficesimulation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements Client.ClientCallback {

    private RecyclerView mPostFeedView;
    private Simulator mSimulator;
    private PostOffice mPostOffice;
    private PostListAdapter mPostListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostFeedView = (RecyclerView) findViewById(R.id.postFeedRecyclerView);
        mPostFeedView.setLayoutManager(new LinearLayoutManager(this));
        mPostListAdapter = new PostListAdapter(this, new LinkedList<PostListAdapter.FeedItem>());
        mPostFeedView.setAdapter(mPostListAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPostOffice = new PostOffice(new LinkedHashMap<Integer, Handler>());
        mPostOffice.start();
        mSimulator = new Simulator(mPostOffice, this);
        mSimulator.createClients(10).start();
    }

    @Override
    public void onNewPost(final Client receiver, final Client sender, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int position = mPostListAdapter.getFeedItemList().size();
                mPostListAdapter.getFeedItemList()
                        .add(new PostListAdapter.FeedItem(sender.getName(), receiver.getName(), message));
                mPostFeedView.getAdapter().notifyItemInserted(position);
                mPostFeedView.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mSimulator.stop();
        mPostOffice.quit();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }

    public static class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

        private LinkedList<FeedItem> feedItemList;
        private Context context;

        public PostListAdapter(Context context, LinkedList<FeedItem> feedItemList) {
            this.feedItemList = feedItemList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            return feedItemList.size();
        }

        public LinkedList<FeedItem> getFeedItemList() {
            return feedItemList;
        }

        public static class FeedItem {
            String senderName;
            String receiverName;
            String message;

            public FeedItem(String senderName, String receiverName, String message) {
                this.senderName = senderName;
                this.receiverName = receiverName;
                this.message = message;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView senderNameTxt;
            TextView receiverNameTxt;
            TextView msgTxt;

            public ViewHolder(View itemView) {
                super(itemView);
                senderNameTxt = (TextView) itemView.findViewById(R.id.senderNameTxt);
                receiverNameTxt = (TextView) itemView.findViewById(R.id.receiverNameTxt);
                msgTxt = (TextView) itemView.findViewById(R.id.msgTxt);
            }

            public void onBind(int position) {
                FeedItem feedItem = feedItemList.get(position);
                senderNameTxt.setText(feedItem.senderName);
                receiverNameTxt.setText(feedItem.receiverName);
                msgTxt.setText(feedItem.message);
            }
        }
    }
}
