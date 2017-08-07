package com.example.heizepalvin.streetrecord;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.graphics.Color.BLACK;
import static com.example.heizepalvin.streetrecord.PlaylistActivity.insertMode;
import static com.example.heizepalvin.streetrecord.PlaylistActivity.seleteList;

/**
 * Created by soyounguensoo on 2017-07-18.
 */

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView artist;
    public ImageView albumImg;
    public ImageView play;
    public ImageButton more;
    public ImageView reorder;
    public LinearLayout playlistItem;
    public ImageView playing;

    public PlaylistItemTouchHelperCallback.OnListItemClickListener mListener;
    public void setOnListItemClickListener(PlaylistItemTouchHelperCallback.OnListItemClickListener onListItemClickListener){
        mListener = onListItemClickListener;
    }

    public PlaylistViewHolder(final View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.playlistActTitle);
        artist = (TextView) itemView.findViewById(R.id.playlistActArtist);
        albumImg = (ImageView) itemView.findViewById(R.id.playlistActImg);
        play = (ImageView) itemView.findViewById(R.id.playlistActPlay);
        reorder = (ImageView) itemView.findViewById(R.id.playlistActReorder);
        playlistItem = (LinearLayout) itemView.findViewById(R.id.playlistItemLinear);
        playing = (ImageView) itemView.findViewById(R.id.playlistActPlaying);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListItemClick(playlistItem,getAdapterPosition());
            }
        });


    }


}
