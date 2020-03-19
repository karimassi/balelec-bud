package ch.epfl.balelecbud.transport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.transport.TransportListFragment.OnListFragmentInteractionListener;
import ch.epfl.balelecbud.transport.objects.Transport;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

public class MyTransportRecyclerViewAdapter extends RecyclerView.Adapter<MyTransportRecyclerViewAdapter.ViewHolder> {

    //default implementation is Firebase
    static private DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();

    //Used to insert mocks
    @VisibleForTesting
    public static void setTransportDatabase(DatabaseWrapper databaseWrapper) {
        database = databaseWrapper;
    }

    private final List<Transport> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyTransportRecyclerViewAdapter(OnListFragmentInteractionListener fragmentInteractionListenerl) {
        mValues = new LinkedList<>();
        RecyclerViewAdapterFacade facade = new RecyclerViewAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                MyTransportRecyclerViewAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                MyTransportRecyclerViewAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                MyTransportRecyclerViewAdapter.this.notifyItemRemoved(position);
            }
        };
        DatabaseListener<Transport> listener = new DatabaseListener(facade, mValues, Transport.class);
        database.listen(DatabaseWrapper.TRANSPORT_PATH, listener);
        mListener = fragmentInteractionListenerl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.typeView.setText(mValues.get(position).getTypeString());
        holder.lineView.setText(mValues.get(position).getLineString());
        holder.directionView.setText(mValues.get(position).getDirection());
        holder.timeView.setText(mValues.get(position).getTimeString());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView typeView;
        public final TextView lineView;
        public final TextView directionView;
        public final TextView timeView;
        public Transport mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeView = (TextView) view.findViewById(R.id.transportType);
            lineView = (TextView) view.findViewById(R.id.transportLine);
            directionView = (TextView) view.findViewById(R.id.transportDirection);
            timeView = (TextView) view.findViewById(R.id.transportTime);
        }

    }
}
