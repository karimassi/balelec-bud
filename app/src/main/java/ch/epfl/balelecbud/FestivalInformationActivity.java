package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.FestivalInformation.FestivalInformationAdapter;

public class FestivalInformationActivity extends BasicActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter festivalInfoAdapter;

    private static List<String> DUMMY_INFORMATION = new LinkedList<>();
    static {
        DUMMY_INFORMATION.add("Date: 12.05.2020");
        DUMMY_INFORMATION.add("Location: EPFL");
        DUMMY_INFORMATION.add("Hours: 5pm-5am");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_info);

        recyclerView = findViewById(R.id.festivalInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        festivalInfoAdapter = new FestivalInformationAdapter(DUMMY_INFORMATION);
        recyclerView.setAdapter(festivalInfoAdapter);
    }
}