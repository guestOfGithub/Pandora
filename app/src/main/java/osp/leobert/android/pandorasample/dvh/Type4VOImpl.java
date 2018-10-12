package osp.leobert.android.pandorasample.dvh;

import osp.leobert.android.pandorasample.AbsViewHolder;
import osp.leobert.android.pandorasample.DataSet;

/**
 * <p><b>Package:</b> osp.leobert.android.pandorasample.dvh </p>
 * <p><b>Project:</b> Pandora </p>
 * <p><b>Classname:</b> Type2VOImpl </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/10/11.
 */
public class Type4VOImpl implements Type4VO {
    private final int i;

    public Type4VOImpl(int i) {
        this.i = i;
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public void setToViewHolder(AbsViewHolder<DataSet.Data> viewHolder) {
        viewHolder.setData(this);
    }

    @Override
    public String toString() {
        return "Type4VOImpl{" +
                "i=" + i +
                '}';
    }
}