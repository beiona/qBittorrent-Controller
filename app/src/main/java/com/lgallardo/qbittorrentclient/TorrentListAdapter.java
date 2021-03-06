/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

package com.lgallardo.qbittorrentclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

class TorrentListAdapter extends ArrayAdapter<String> {

    private static HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private String[] torrentsNames;
    private Torrent[] torrentsData;
    private Context context;

    public TorrentListAdapter(Context context, String[] torrentsNames, Torrent[] torrentsData) {
        super(context, R.layout.row, R.id.name, torrentsNames);

        this.context = context;
        this.torrentsNames = torrentsNames;
        this.torrentsData = torrentsData;

    }

    @Override
    public int getCount() {
        return (torrentsNames != null) ? torrentsNames.length : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        View row = super.getView(position, convertView, parent);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.row, parent, false);

        if (torrentsData != null) {

            String file = torrentsData[position].getName();

            String state = torrentsData[position].getState();

            TextView name = (TextView) row.findViewById(R.id.name);
            name.setText(file);

            TextView info = (TextView) row.findViewById(R.id.info);

            info.setText("" + torrentsData[position].getInfo());

            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
                icon.setImageResource(R.drawable.paused);
            }

            if ("stalledUP".equals(state)) {
                icon.setImageResource(R.drawable.stalledup);
            }

            if ("stalledDL".equals(state)) {
                icon.setImageResource(R.drawable.stalleddl);
            }

            if ("downloading".equals(state)) {
                icon.setImageResource(R.drawable.downloading);
            }

            if ("uploading".equals(state) || "forcedUP".equals(state)) {
                icon.setImageResource(R.drawable.uploading);
            }

            if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
                icon.setImageResource(R.drawable.queued);
            }

            if ("checkingDL".equals(state) || "checkingUP".equals(state)) {
                icon.setImageResource(R.drawable.ic_action_recheck);
            }

            if ("error".equals(state) || "missingFiles".equals(state) || "unknown".equals(state)) {
                icon.setImageResource(R.drawable.error);
            }

            if(MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
                // Set progress bar
                ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

                double percentage = torrentsData[position].getProgress();

                progressBar.setProgress((int) (percentage*100));

//                Log.d("Debug", "[TorrentListAdapter] percentage: " + percentage);

                percentageTV.setText(Common.ProgressForUiTruncated(percentage) + "%");
            }

            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light)); //default color

            if (mSelection.get(position) != null) {
                row.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it blue
            }
        } else {

            TextView name = (TextView) row.findViewById(R.id.name);
            name.setText(context.getString(R.string.no_results));

            // Hide progress bar
            if(MainActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {

                ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

                progressBar.setVisibility(View.GONE);
                percentageTV.setVisibility(View.GONE);

            }

            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            TextView info = (TextView) row.findViewById(R.id.info);

            icon.setVisibility(View.GONE);
            info.setVisibility(View.GONE);

            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light)); //default color

        }

        return (row);
    }


    public void setNewSelection(int position, boolean value) {

        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    public void setNames(String[] names) {
        this.torrentsNames = null;
        this.torrentsNames = names;
    }

    public Torrent[] getData() {
        return this.torrentsData;
    }

    public void setData(Torrent[] data) {
        this.torrentsData = data;
    }

    @Override
    public boolean isEnabled(int position) {
        if (areAllItemsEnabled()) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        if (torrentsData != null && !com.lgallardo.qbittorrentclient.ItemstFragment.mSwipeRefreshLayout.isRefreshing()) {
            return true;
        } else {
            return false;
        }
    }
}