package com.example.gurpreetsingh.project.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.gurpreetsingh.project.Contact;
import com.example.gurpreetsingh.project.R;

import java.util.List;

/**
 * Created by Gurpreet on 15-01-2017.
 */

public class EmergencyContactsAdapter extends
        RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {

    private List<Contact> mItems;
    private Activity activity;
    private ItemListener mListener;

    // This object helps you save/restore the open/close state of each view
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public EmergencyContactsAdapter(Activity activity) {
        mListener = (ItemListener)activity;
        this.activity = activity;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }

    public void clearList() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public Contact getItemAtPos(int pos) {
        if(mItems.size() > 0) {
            return mItems.get(pos);
        }
        else {
            return null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(EmergencyContactsAdapter.ViewHolder holder, int position) {
        // get your data object first.
        Contact contact = mItems.get(position);
        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
        // put an unique string id as value, can be any string which uniquely define the data

        // Save/restore the open/close state.
        // You need to provide a String id which uniquely defines the data object.
        viewBinderHelper.bind(holder.swipeRevealLayout, contact.getName());
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(Contact item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    public void delete(Contact item, int pos) {
        mItems.remove(item);
        notifyItemRemoved(pos);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, SwipeRevealLayout.SwipeListener {

        private Contact contact;
        private SwipeRevealLayout swipeRevealLayout;
        private TextView tvContactName;
        private TextView tvContactNumber;
        private TextView tvContactLetter;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipeLayoutItem);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
            tvContactLetter = (TextView) itemView.findViewById(R.id.tvContactLetter);
        }

        public void setData(Contact contact) {
            this.contact = contact;
            tvContactName.setText(contact.getName());
            tvContactLetter.setText(contact.getNumber());
            tvContactLetter.setText(contact.getName().charAt(0));
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(contact, getAdapterPosition());
            }
        }

        @Override
        public void onClosed(SwipeRevealLayout view) {

        }

        @Override
        public void onOpened(SwipeRevealLayout view) {
            if (mItems.size() > 0) {
               delete(contact, getAdapterPosition());
            }
        }

        @Override
        public void onSlide(SwipeRevealLayout view, float slideOffset) {

        }
    }

    public interface ItemListener {
        void onItemClick(Contact contact, int position);
    }
}
