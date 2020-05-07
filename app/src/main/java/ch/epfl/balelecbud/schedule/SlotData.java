package ch.epfl.balelecbud.schedule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.schedule.models.Slot;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.Database;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.intents.FlowUtil;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

public class SlotData extends RecyclerViewData<Slot, SlotHolder> {

    private static final String TAG = SlotData.class.getSimpleName();

    private final Activity mainActivity;
    private final List<Slot> subscribedConcertAtLaunch;

    private static Consumer<Intent> intentLauncher;

    public SlotData(Activity mainActivity, List<Slot> subscribedConcertAtLaunch) {
        super();
        this.mainActivity = mainActivity;
        this.subscribedConcertAtLaunch = subscribedConcertAtLaunch;
        if (intentLauncher == null) {
            intentLauncher = mainActivity::startService;
        }
    }

    @Override
    public void reload() {
        MyQuery query = new MyQuery(Database.CONCERT_SLOTS_PATH, new LinkedList<>());
        getAppDatabase().queryWithType(query, Slot.class).whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, SlotHolder viewHolder) {
        final Slot slot = data.get(index);
        viewHolder.timeSlotView.setText(slot.getTimeSlot());
        viewHolder.artistNameView.setText(slot.getArtistName());
        viewHolder.sceneNameView.setText(slot.getSceneName());

        CompletableFuture<File> imageDownload = getAppStorage().getFile("artists_images/" + slot.getImageFileName());
        imageDownload.whenComplete((file, t) -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            viewHolder.artistImageView.setImageBitmap(bitmap);
            viewHolder.artistImageView.setVisibility(View.VISIBLE);
        });

        viewHolder.subscribeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Notification switched: ON");
                intentLauncher.accept(
                        FlowUtil.packSubscribeIntentWithSlot(mainActivity, slot));
            } else {
                Log.d(TAG, "Notification switched: OFF");
                intentLauncher.accept(
                        FlowUtil.packCancelIntentWithSlot(mainActivity, slot));
            }
        });

        if (this.subscribedConcertAtLaunch.contains(slot))
            viewHolder.subscribeSwitch.setChecked(true);
    }

    @VisibleForTesting
    public static void setIntentLauncher(Consumer<Intent> launcher) {
        intentLauncher = launcher;
    }
}
