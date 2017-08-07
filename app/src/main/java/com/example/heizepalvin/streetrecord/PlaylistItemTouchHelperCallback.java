package com.example.heizepalvin.streetrecord;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by soyounguensoo on 2017-07-19.
 */

public class PlaylistItemTouchHelperCallback extends ItemTouchHelper.Callback {



    public interface OnItemMoveListener{
        void onItemMove (int fromPosition, int toPosition);

    }

    public interface OnListItemClickListener{
        void onListItemClick(View itemView, int position);


    }

    public OnListItemClickListener mListener;

    private final OnItemMoveListener mItemMoveListener;

    public PlaylistItemTouchHelperCallback(OnItemMoveListener mItemMoveListener) {
        this.mItemMoveListener = mItemMoveListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        Log.e("onMove","onMove");
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
