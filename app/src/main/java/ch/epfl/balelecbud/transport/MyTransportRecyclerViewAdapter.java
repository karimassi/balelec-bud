package ch.epfl.balelecbud.transport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.transport.TransportListFragment.OnListFragmentInteractionListener;
import ch.epfl.balelecbud.transport.objects.Transport;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class MyTransportRecyclerViewAdapter extends RecyclerView.Adapter<MyTransportRecyclerViewAdapter.ViewHolder> {
    private final List<Transport> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyTransportRecyclerViewAdapter(OnListFragmentInteractionListener fragmentInteractionListener) {
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
        DatabaseListener<Transport> listener = new DatabaseListener<>(facade, mValues, Transport.class);
        getAppDatabaseWrapper().listen(DatabaseWrapper.TRANSPORT_PATH, listener);
        mListener = fragmentInteractionListener;
    }

    @NonNull
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
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final TextView typeView;
        final TextView lineView;
        final TextView directionView;
        final TextView timeView;
        Transport mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            typeView = view.findViewById(R.id.transportType);
            lineView = view.findViewById(R.id.transportLine);
            directionView = view.findViewById(R.id.transportDirection);
            timeView = view.findViewById(R.id.transportTime);
        }
    }
}
