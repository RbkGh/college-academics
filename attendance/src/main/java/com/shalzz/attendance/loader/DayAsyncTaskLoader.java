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

package com.shalzz.attendance.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.model.PeriodModel;

import java.util.List;

public class DayAsyncTaskLoader extends AsyncTaskLoader<List<PeriodModel>> {

    private DatabaseHandler mDb;
    private String mDay;
    private List<PeriodModel> mPeriods;

    public DayAsyncTaskLoader(Context context, String day) {
        super(context);
        mDay = day;
    }

    @Override
    protected void onStartLoading() {
        if (mPeriods != null) {
            // Use cached data
            deliverResult(mPeriods);
        }
        if (takeContentChanged() || mPeriods == null) {
            // Something has changed or we have no data,
            // so kick off loading it
            forceLoad();
        }
    }

    @Override
    public List<PeriodModel> loadInBackground() {
        if(mDb == null)
            mDb = new DatabaseHandler(getContext());
        return mDb.getAllPeriods(mDay, this);
    }

    @Override
    public void deliverResult(List<PeriodModel> data) {
        // We’ll save the data for later retrieval
        mPeriods = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(List<PeriodModel> data) {
        super.onCanceled(data);
        if(mDb != null) {
            mDb.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mDb != null) {
            mDb.close();
        }
        mDb = null;
    }
}
