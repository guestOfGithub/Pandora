package osp.leobert.android.pandorasample.kt

import osp.leobert.android.pandora.rv.IReactiveViewHolder
import osp.leobert.android.pandora.rv.ReactiveData
import osp.leobert.androidkt.pandora.rv.DataSet

/**
 * <p><b>Package:</b> osp.leobert.android.pandorasample.kt </p>
 * <p><b>Project:</b> Pandora </p>
 * <p><b>Classname:</b> TestKtVO </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2019/2/20.
 */
interface TestKtVO : DataSet.Data<TestKtVO>/*,ReactiveData<TestKtVO,IReactiveViewHolder<TestKtVO>> */{
    //D<TestKtVO,IViewHolder<TestKtVO>>

    fun getData(): String {
        return javaClass.name
    }

//    override fun setToViewHolder(viewHolder: IViewHolder<TestKtVO>) {
//        viewHolder.setData(this)
//    }

    class Impl1 : TestKtVO



    class Impl2 : TestKtVO
}