/*
 * Copyright (c) 2013-2016 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 * This file is part of UPES Academics.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shalzz.attendance.controllers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.clockbyte.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.google.android.gms.ads.AdSize;
import com.shalzz.attendance.R;
import com.shalzz.attendance.adapter.DayListAdapter;
import com.shalzz.attendance.fragment.DayFragment;
import com.shalzz.attendance.loader.DayAsyncTaskLoader;
import com.shalzz.attendance.model.local.Day;

import java.util.Date;

public class DayController implements LoaderManager.LoaderCallbacks<Day> {

    private Context mContext;
    private DayFragment mView;
    private DayListAdapter mAdapter;
    private final AdmobExpressRecyclerAdapterWrapper adapterWrapper;

    public DayController(Context context, DayFragment view)  {
        mContext = context;
        mView = view;
        mAdapter = new DayListAdapter();
        String unit_id ;
        int currentNightMode = mContext.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            unit_id = mContext.getString(R.string.native_black_timetable_ad);
        else
            unit_id = mContext.getString(R.string.native_white_timetable_ad);

        adapterWrapper = new AdmobExpressRecyclerAdapterWrapper(mContext,
                unit_id,
                new AdSize(AdSize.FULL_WIDTH, 80));

        adapterWrapper.setAdapter(mAdapter);

        adapterWrapper.setLimitOfAds(1);
        adapterWrapper.setFirstAdIndex(3);
        mView.mRecyclerView.setAdapter(adapterWrapper);
    }

    public void destroyAds() {
        adapterWrapper.destroyAds();
    }

    @Override
    public Loader<Day> onCreateLoader(int id, Bundle args) {
        Date date = args != null ? (Date) args
                .getSerializable(DayFragment.ARG_DATE) : new Date();
        return new DayAsyncTaskLoader(mContext, date);
    }

    @Override
    public void onLoadFinished(Loader<Day> loader, Day data) {
        if (data.getPeriods().size() == 0) {
            mView.mEmptyView.setVisibility(View.VISIBLE);
            mAdapter.clear();
        } else {
            mView.mEmptyView.setVisibility(View.GONE);
            mAdapter.update(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Day> loader) {
        // Loader reset, throw away our data,
        // unregister any listeners, etc.
        mAdapter.clear();
        // Of course, unless you use destroyLoader(),
        // this is called when everything is already dying
        // so a completely empty onLoaderReset() is
        // totally acceptable
    }
}
