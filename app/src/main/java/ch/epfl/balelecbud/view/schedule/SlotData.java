package ch.epfl.balelecbud.view.schedule;

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

import ch.epfl.balelecbud.model.Slot;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.FlowUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

public final class SlotData extends RecyclerViewData<Slot, SlotHolder> {

    private static final String TAG = SlotData.class.getSimpleName();

    private final Activity mainActivity;
    private final List<Slot> subscribedConcertAtLaunch;

    private static Consumer<Intent> intentLauncher;

    SlotData(Activity mainActivity, List<Slot> subscribedConcertAtLaunch) {
        super();
        this.mainActivity = mainActivity;
        this.subscribedConcertAtLaunch = subscribedConcertAtLaunch;
        if (intentLauncher == null) {
            intentLauncher = mainActivity::startService;
        }
    }

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        MyQuery query = new MyQuery(Database.CONCERT_SLOTS_PATH, new LinkedList<>(), preferredSource);
        return getAppDatabase().query(query, Slot.class)
                .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
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
                        FlowUtils.packSubscribeIntentWithSlot(mainActivity, slot));
            } else {
                Log.d(TAG, "Notification switched: OFF");
                intentLauncher.accept(
                        FlowUtils.packCancelIntentWithSlot(mainActivity, slot));
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
