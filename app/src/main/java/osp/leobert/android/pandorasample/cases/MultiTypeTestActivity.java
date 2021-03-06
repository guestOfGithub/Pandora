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

package osp.leobert.android.pandorasample.cases;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import osp.leobert.android.pandora.Pandora;
import osp.leobert.android.pandora.RealDataSet;
import osp.leobert.android.pandora.WrapperDataSet;
import osp.leobert.android.pandora.rv.DataSet;
import osp.leobert.android.pandora.rv.DateVhMappingPool;
import osp.leobert.android.pandora.rv.PandoraRealRvDataSet;
import osp.leobert.android.pandora.rv.PandoraWrapperRvDataSet;
import osp.leobert.android.pandora.rv.ViewHolderCreator;
import osp.leobert.android.pandorasample.R;
import osp.leobert.android.pandorasample.RvAdapter;
import osp.leobert.android.pandorasample.TimeUtil;
import osp.leobert.android.pandorasample.dvh.AbsViewHolder;
import osp.leobert.android.pandorasample.dvh.Type1VH;
import osp.leobert.android.pandorasample.dvh.Type1VO;
import osp.leobert.android.pandorasample.dvh.Type1VOImpl;
import osp.leobert.android.pandorasample.dvh.Type2VH;
import osp.leobert.android.pandorasample.dvh.Type2VOImpl;
import osp.leobert.android.pandorasample.dvh.Type3VH;
import osp.leobert.android.pandorasample.dvh.Type3VOImpl;
import osp.leobert.android.pandorasample.dvh.Type4VH;
import osp.leobert.android.pandorasample.dvh.Type4VOImpl;
import osp.leobert.android.pandorasample.dvh.Type5VH;
import osp.leobert.android.pandorasample.dvh.Type5VOImpl;
import osp.leobert.android.pandorasample.menu.MenuVH2;
import osp.leobert.android.pandorasample.menu.MenuVO2;
import osp.leobert.android.pandorasample.menu.SectionVH2;

/**
 * 多数据类型测试
 */
public class MultiTypeTestActivity extends AppCompatActivity {

    private static final class SectionHeader implements MenuVO2 {
        private final String name;
        @Level
        private final int level;

        public SectionHeader(@Level int level, String name) {
            this.level = level;
            this.name = name;
        }

        @Override
        public int level() {
            return level;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setToViewHolder(AbsViewHolder<DataSet.Data> viewHolder) {
            viewHolder.setData(this);
        }
    }

    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;

    PandoraWrapperRvDataSet<DataSet.Data> dataSet;

    PandoraRealRvDataSet<DataSet.Data> dataSetSection1;
    PandoraRealRvDataSet<DataSet.Data> dataSetSection2;
    PandoraRealRvDataSet<DataSet.Data> dataSetSection3;
    RvAdapter<PandoraWrapperRvDataSet<DataSet.Data>> adapter;

    RealDataSet<DataSet.Data> dataSet1;


    RvAdapter<PandoraRealRvDataSet<DataSet.Data>> adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.rv);
        recyclerView2 = findViewById(R.id.rv2);
        initDataSet();

        adapter = new RvAdapter<>(dataSet, getClass().getSimpleName());
        adapter2 = new RvAdapter<>(dataSetSection1, getClass().getSimpleName());

        Pandora.bind2RecyclerViewAdapter(dataSet.getDataSet(), adapter);
        Pandora.bind2RecyclerViewAdapter(dataSetSection1.getRealDataSet(), adapter2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(adapter2);

        DividerItemDecoration decoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.color.colorAccent));
        recyclerView.addItemDecoration(decoration);
        recyclerView2.addItemDecoration(decoration);

        Button btn1 = findViewById(R.id.b1);
        btn1.setText("向Section1\r\n中添加");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataIntoSection1();
            }
        });

        Button btn2 = findViewById(R.id.b2);
        btn2.setText("向Section2\r\n中添加");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataIntoSection2();
            }
        });

        Button btn3 = findViewById(R.id.b3);
        btn3.setText("向Section3\r\n中添加");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataIntoSection3();
            }
        });

    }

    private void initDataSet() {
        WrapperDataSet<DataSet.Data> wrapperDataSet = Pandora.wrapper();
        dataSet = new PandoraWrapperRvDataSet<>(wrapperDataSet);

//        dataSet.retrieveAdapterByDataIndex2(1)

        dataSetSection1 = new PandoraRealRvDataSet<>(Pandora.<DataSet.Data>real());
        dataSetSection1.setAlias("sec1");
        dataSetSection2 = new PandoraRealRvDataSet<>(Pandora.<DataSet.Data>real());
        dataSetSection2.setAlias("sec2");
        dataSetSection3 = new PandoraRealRvDataSet<>(Pandora.<DataSet.Data>real());
        dataSetSection3.setAlias("sec3");

        dataSet1 = Pandora.real();
        dataSet.addSub(dataSet1);


        dataSet.addSub(dataSetSection1.getRealDataSet());
        dataSet.addSub(dataSetSection2.getRealDataSet());
        dataSet.addSub(dataSetSection3.getRealDataSet());


//        dataSet.registerDVRelation(SectionHeader.class, new MenuVH2.Creator(null));

        dataSet.registerDVRelation(new DateVhMappingPool.DVRelation<SectionHeader>() {
            private static final String type_l1 = "type_l1";
            private static final String type_l2 = "type_l2";

            @Override
            public Class<SectionHeader> getDataClz() {
                return SectionHeader.class;
            }

            @Override
            public int one2N() {
                return 2;
            }

            @Override
            public String subTypeToken(@NonNull SectionHeader data) {
                if (data.level() == MenuVO2.Level.l1)
                    return type_l1;
                return type_l2;
            }

            @Override
            public ViewHolderCreator getVhCreator(@NonNull String subTypeToken) {
                //对于差异不大的，仅仅对View进行一定的调整，例如修改背景、间距等等，可以定义多个Creator，反射得到View的时候做处理
                //对于差距比较大的，或者本身就是对应到多个ViewHolder的，如下
                if (type_l1.equals(subTypeToken)) {
                    return new SectionVH2.Creator(new SectionVH2.ItemInteract() {
                        @Override
                        public void onSectionItemClicked(int pos, MenuVO2 data) {
                            Toast.makeText(MultiTypeTestActivity.this, "onSectionItemClicked,pos:" + pos + "," + data.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return new MenuVH2.Creator(null);
            }
        });


        dataSetSection1.registerDVRelation(Type1VOImpl.class, new Type1VH.Creator(new Type1VH.ItemInteract() {
            @Override
            public void foo(int pos, Type1VO data) {
                dataSetSection1.removeAtPos(pos);
            }
        }));
        //此处体现了同样的基础数据，在不同使用场景下体现出区别，对比左右两个列表
        dataSetSection1.registerDVRelation(SectionHeader.class, new MenuVH2.Creator(null));

        dataSetSection1.registerDVRelation(Type2VOImpl.class, new Type2VH.Creator(null));
        dataSetSection1.registerDVRelation(Type3VOImpl.class, new Type3VH.Creator(null));
        dataSetSection1.registerDVRelation(Type4VOImpl.class, new Type4VH.Creator(null));
        dataSetSection1.registerDVRelation(Type5VOImpl.class, new Type5VH.Creator(null));


//        dataSet.removeDVRelation(Type1VOImpl.class); //验证下log

        dataSet.registerDVRelation(Type2VOImpl.class, new Type2VH.Creator(null));
        dataSet.registerDVRelation(Type3VOImpl.class, new Type3VH.Creator(null));
        dataSet.registerDVRelation(Type4VOImpl.class, new Type4VH.Creator(null));
        dataSet.registerDVRelation(Type5VOImpl.class, new Type5VH.Creator(null));

        dataSet.registerDVRelation(Type1VOImpl.class, new Type1VH.Creator(new Type1VH.ItemInteract() {
            @Override
            public void foo(int pos, Type1VO data) {
                Toast.makeText(MultiTypeTestActivity.this, "1", Toast.LENGTH_SHORT).show();
                dataSet.removeAtPos(pos);
            }
        }));

        dataSet.removeDVRelation(Type1VOImpl.class);

        dataSet.registerDVRelation(Type1VOImpl.class, new Type1VH.Creator(new Type1VH.ItemInteract() {
            @Override
            public void foo(int pos, Type1VO data) {
                dataSet.removeAtPos(pos);
            }
        }));

        dataSetSection1.add(new SectionHeader(MenuVO2.Level.l1, "此处开始是Section1\r\n对比左右两个列表注册的样式区别  "));
        dataSetSection2.add(new SectionHeader(MenuVO2.Level.l1, "此处开始是Section2"));
        dataSetSection3.add(new SectionHeader(MenuVO2.Level.l1, "此处开始是Section3"));
    }

    /**
     * 添加数据到Section1 中（尾部）
     * 按序一共5个数据
     * SectionHeader
     * type5
     * type4
     * type2
     * type3
     */
    private void addDataIntoSection1() {
        Collection<DataSet.Data> collection = new ArrayList<>();
        collection.add(new SectionHeader(MenuVO2.Level.l2, "one group data in section1,依次为\r\n"
                + "type5\r\ntype4\r\ntype2\r\ntype3\r\n"
                + "add at" + TimeUtil.getCurrentTimeInString()));
        collection.add(new Type5VOImpl(2));
        collection.add(new Type4VOImpl(3));
        collection.add(new Type2VOImpl(4));
        collection.add(new Type3VOImpl(5));

        dataSetSection1.addAll(collection);
    }

    private void addDataIntoSection2() {
        Collection<DataSet.Data> collection = new ArrayList<>();
        String time = TimeUtil.getCurrentTimeInString();
        collection.add(new SectionHeader(MenuVO2.Level.l2, "one group data in section2,依次为\r\n"
                + "type1\r\ntype1\r\n"
                + "add at" + time));
        collection.add(new Type1VOImpl("type1点击可删除" + time));
        collection.add(new Type1VOImpl("整段数据添加到了Section2的尾部" + time));

        dataSetSection2.addAll(collection);
    }

    private void addDataIntoSection3() {
        Collection<DataSet.Data> collection = new ArrayList<>();
        collection.add(new SectionHeader(MenuVO2.Level.l2, "one group data in section3,依次为\r\n"
                + "type5\r\ntype3\r\ntype4\r\ntype2\r\ntype1\r\n"
                + "add at" + TimeUtil.getCurrentTimeInString()));

        collection.add(new Type5VOImpl(2));
        collection.add(new Type3VOImpl(3));
        collection.add(new Type4VOImpl(4));
        collection.add(new Type2VOImpl(5));
        collection.add(new Type1VOImpl("6"));

        dataSetSection3.addAll(collection);
    }
}
