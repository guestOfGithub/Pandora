/*
 * MIT License
 *
 * Copyright (c) 2018 leobert-lan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package osp.leobert.android.pandora.rv;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import osp.leobert.android.pandora.Logger;


/**
 * <p><b>Package:</b> osp.leobert.android.pandorarv </p>
 * <p><b>Project:</b> Pandorarv </p>
 * <p><b>Classname:</b> DateVhMappingPool </p>
 * <p><b>Description:</b> a pool to restore and fetch the relationship between VO and VH </p>
 * Created by leobert on 2018/10/10.
 */
public class DateVhMappingPool {
    private final SparseArray<TypeCell> viewTypeCache = new SparseArray<>();
    private int maxSize = 5;

    //先备注一个bug
    // E/Pandora: missing viewType:180 ?
    //    java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
    //        at java.util.ArrayList.get(ArrayList.java:411)
    //        at osp.leobert.android.pandora.rv.TypeCell.getVhCreator(TypeCell.java:82)
    //        at osp.leobert.android.pandora.rv.DateVhMappingPool.createViewHolderV2(DateVhMappingPool.java:138)
    //        at osp.leobert.android.pandora.rv.DataSet.createViewHolderV2(DataSet.java:125)

    //设计缺陷，目前采用的int viewType 到 relation 的映射关系存在一个问题，
    //            int index = viewType / maxSize;
    //            int subIndex = viewType % maxSize;
    //            return viewTypeCache.valueAt(index).getVhCreator(subIndex).createViewHolder(parent);
    // 我们以这样的方式进行转化，但是如果执行过removeDVRelation，则有错误几率

    @Nullable
    private TypeCell internalErrorTypeCell;

    public synchronized void removeDVRelation(@NonNull Class<?> dataClz) {
        synchronized (viewTypeCache) {
            for (int i = 0; i < viewTypeCache.size(); i++) {
                try {
                    TypeCell typeCell = viewTypeCache.valueAt(i);
                    if (typeCell.workFor(dataClz.getName())) {
                        int key = viewTypeCache.keyAt(i);
                        viewTypeCache.remove(key);
                        i--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(Logger.TAG, "DateVhMappingPool removeDVRelation error", e);
                }
            }
        }
    }

    public synchronized void registerDVRelation(@NonNull Class<?> dataClz, @NonNull ViewHolderCreator viewHolderCreator) {
        this.registerDVRelation(new DataVhRelation<>(dataClz, viewHolderCreator));
    }

    public synchronized void registerDvRelation(DVRelation... dvRelations) {
        for (DVRelation dvRelation : dvRelations)
            registerDVRelation(dvRelation);
    }

    public synchronized void registerDVRelation(DVRelation<?> dvRelation) {
        if (dvRelation == null)
            return;
        synchronized (viewTypeCache) {
            int n = dvRelation.one2N();

            while (n > maxSize) {
                maxSize *= 2;
                for (int i = 0; i < viewTypeCache.size(); i++) {
                    viewTypeCache.valueAt(i).updateMaxSize(maxSize);
                }
            }

            int index = viewTypeCache.size();
            TypeCell typeCell = new TypeCell<>(index, dvRelation);
            typeCell.updateMaxSize(maxSize);
            viewTypeCache.put(index, typeCell);
        }
    }

    public void whenInternalError(@NonNull ViewHolderCreator viewHolderCreator) {
        this.internalErrorTypeCell = new TypeCell<>(Integer.MAX_VALUE, new DataVhRelation<>(DataSet.Data.class, viewHolderCreator));
    }

    @SuppressWarnings("unchecked")
    public <T> int getItemViewTypeV2(String key, T data) { //getItemViewType
        for (int i = 0; i < viewTypeCache.size(); i++) {//折半查找可能效率更高一点
            TypeCell typeCell = viewTypeCache.valueAt(i);
            if (typeCell == null) continue;
            if (typeCell.workFor(key)) {
                return typeCell.getItemViewType(data);
            }
        }
        if (Logger.DEBUG) {
            RuntimeException e = new RuntimeException("have you register for:" + key);
            Logger.e(Logger.TAG, "missing type register", e);
            throw e;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public int getViewTypeCount() {//getViewTypeCount
        int ret = 0;
        if (internalErrorTypeCell != null)
            ret += 1;
        for (int i = 0; i < viewTypeCache.size(); i++) {
            try {
                ret += viewTypeCache.valueAt(i).getSubTypeCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public IViewHolder createViewHolderV2(ViewGroup parent, int viewType) { // createViewHolder(ViewGroup parent, int viewType)
        try {
            int index = viewType / maxSize;
            int subIndex = viewType % maxSize;
            return viewTypeCache.valueAt(index).getVhCreator(subIndex).createViewHolder(parent);
        } catch (Exception e) {
            if (Logger.DEBUG) {
                if (internalErrorTypeCell != null)
                    return internalErrorTypeCell.getVhCreator(0).createViewHolder(parent);
                Logger.e(Logger.TAG, "missing viewType:" + viewType + " ?", e);
                throw e;
            } else {
                if (internalErrorTypeCell != null)
                    return internalErrorTypeCell.getVhCreator(0).createViewHolder(parent);
                return null;
            }
        }
    }


    public interface DVRelation<T> {
        String SINGLE_TYPE_TOKEN = "type_one";

        Class<T> getDataClz();

        int one2N();

        String subTypeToken(@NonNull T data);

        ViewHolderCreator getVhCreator(@NonNull String subTypeToken);
    }

    private static class DataVhRelation<T> implements DVRelation<T> {
        private Class<T> dataClz;
        private ViewHolderCreator vhCreator;

        DataVhRelation(@NonNull Class<T> dataClz, ViewHolderCreator vhCreator) {
            this.dataClz = dataClz;
            this.vhCreator = vhCreator;
        }

        @Override
        public final Class<T> getDataClz() {
            return dataClz;
        }

        @Override
        public final int one2N() {
            return 1;
        }

        @Override
        public final String subTypeToken(@NonNull T data) {
            return SINGLE_TYPE_TOKEN;
        }

        @Override
        public final ViewHolderCreator getVhCreator(@NonNull String subTypeToken) {
            return vhCreator;
        }
    }
}
