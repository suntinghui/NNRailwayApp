/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package cn.trinea.android.view.autoscrollviewpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.android.volley.toolbox.NetworkImageView;
import com.zhc.eid.R;
import com.zhc.eid.client.Constants;
import com.zhc.eid.client.net.ImageCacheManager;

import java.util.List;

/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List<Integer> imageURLList;

    private int size;
    private boolean isInfiniteLoop;

    public ImagePagerAdapter(Context context, List<Integer> imageURLList) {
        this.context = context;
        this.imageURLList = imageURLList;
        this.size = imageURLList.size();
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        return imageURLList.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new NetworkImageView(context);
            holder.imageView.setBackgroundResource(R.drawable.hotel_detail_1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imageView.setScaleType(ScaleType.FIT_XY);
        holder.imageView.setBackgroundResource(imageURLList.get(getPosition(position)));
        return view;
    }

    private static class ViewHolder {
        NetworkImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
