package ch.bretscherhochstrasser.android.poc.mytimetable.common;

import android.app.IntentService;
import android.content.Intent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * IntentService that does not handle Intents until startIntentDelivery is called.
 */
public abstract class DelayedStartIntentService extends IntentService {

    private Queue<QueuedStartIntent> startIntentQueue = new ConcurrentLinkedQueue<QueuedStartIntent>();
    private boolean deliveryStarted = false;

    public DelayedStartIntentService(String name) {
        super("DelayedStart[" + name + "]");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (deliveryStarted) {
            super.onStart(intent, startId);
        } else {
            QueuedStartIntent queuedStartIntent = new QueuedStartIntent();
            queuedStartIntent.intent = intent;
            queuedStartIntent.startId = startId;
            startIntentQueue.add(queuedStartIntent);
        }
    }

    /**
     * Starts the delivery of Intents. All queued intents are delivered now.
     */
    protected void startIntentDelivery() {
        deliveryStarted = true;
        while (!startIntentQueue.isEmpty()) {
            QueuedStartIntent queuedStartIntent = startIntentQueue.poll();
            super.onStart(queuedStartIntent.intent, queuedStartIntent.startId);
        }
    }

    private static class QueuedStartIntent {
        private Intent intent;
        private int startId;
    }
}