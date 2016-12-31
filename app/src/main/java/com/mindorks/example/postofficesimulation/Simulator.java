package com.mindorks.example.postofficesimulation;

import java.util.Random;

/**
 * Created by janisharali on 31/12/16.
 */

public class Simulator implements Runnable {

    private PostOffice mPostOffice;
    private Client.ClientCallback mCallback;
    private Random mRandom;
    private Thread mThread;
    private boolean controller;

    public Simulator(PostOffice postOffice, Client.ClientCallback callback) {
        mPostOffice = postOffice;
        mCallback = callback;
        mRandom = new Random(System.currentTimeMillis());
        mThread = new Thread(this);
        controller = true;
    }

    public Simulator createClients(int num) {
        for (int i = 0; i < num; i++) {
            try {
                mPostOffice.register(new Client("BOT " + i, mCallback));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public synchronized void start() {
        if (!mThread.isAlive())
            mThread.start();
    }

    @Override
    public void run() {
        controller = true;
        while (controller) {

            int client1Id = mRandom.nextInt(Client.sClientMap.size());
            int client2Id = mRandom.nextInt(Client.sClientMap.size());
            while (client1Id == client2Id) {
                client2Id = mRandom.nextInt(Client.sClientMap.size());
            }

            try {
                mPostOffice.sendPost(new Post(client1Id, client2Id, getRandomMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRandomMessage() {
        int val = mRandom.nextInt(10);

        switch (val) {
            case 0:
                return "Happy Christmas!";
            case 1:
                return "How are you buddy?";
            case 2:
                return "I am so proud of you!";
            case 3:
                return "It's holiday hahaha!";
            case 4:
                return ":P";
            case 5:
                return "LOL!";
            case 6:
                return "Wow!";
            case 7:
                return "Bugger off!";
            case 8:
                return "I love you!";
            case 9:
                return "Go to hell :>";
            default:
                return "Hmm";
        }
    }

    public void stop() {
        controller = false;
        Client.disposeAll();
    }
}
