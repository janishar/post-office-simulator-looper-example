package com.mindorks.example.postofficesimulation;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

/**
 * Created by janisharali on 31/12/16.
 */

public class PostOffice extends HandlerThread {

    private static final String TAG = PostOffice.class.getSimpleName();

    private LinkedHashMap<Integer, Handler> mClientDetailsMap;

    public PostOffice(LinkedHashMap<Integer, Handler> clientDetailsMap) {
        super(TAG);
        mClientDetailsMap = clientDetailsMap;
    }

    public synchronized void register(final Client client)
            throws InvalidRequestException, AlreadyExistsException {
        if (client == null) {
            throw new InvalidRequestException("The Client can't be null");
        }
        if (mClientDetailsMap.containsKey(client.getId())) {
            throw new AlreadyExistsException("The Client is already registered with this Id");
        }
        final WeakReference<Client> clientWeakReference = new WeakReference<>(client);
        Handler handler =
                new Handler(getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Client client = clientWeakReference.get();
                        if (client != null) {
                            if (msg.obj instanceof String) {
                                client.onPostReceived(new Post(msg.arg1, msg.arg2, (String) msg.obj));
                            } else {
                                client.onPostReceived(new Post(msg.arg1, msg.arg2, "No Body present"));
                            }
                        }
                    }
                };
        mClientDetailsMap.put(client.getId(), handler);
    }

    public synchronized void sendPost(Post post) throws InvalidRequestException, NotRegisteredException {
        if (post == null) {
            throw new InvalidRequestException("Post can't be null");
        }
        if (post.getReceiverId() == null || !mClientDetailsMap.containsKey(post.getReceiverId())) {
            throw new NotRegisteredException("Post receiver is not registered");
        }
        Handler handler = mClientDetailsMap.get(post.getReceiverId());
        Message message = new Message();
        message.arg1 = post.getSenderId();
        message.arg2 = post.getReceiverId();
        message.obj = post.getMessage();
        handler.sendMessage(message);
    }

    public static class InvalidRequestException extends Exception {
        public InvalidRequestException(String message) {
            super(message);
        }
    }

    public static class AlreadyExistsException extends Exception {
        public AlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class NotRegisteredException extends Exception {
        public NotRegisteredException(String message) {
            super(message);
        }
    }
}
