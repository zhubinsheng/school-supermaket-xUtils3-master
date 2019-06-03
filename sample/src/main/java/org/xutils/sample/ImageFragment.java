package org.xutils.sample;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.sample.vo.ResultObject;
import org.xutils.sample.vo.TScore;
import org.xutils.sample.vo.TStudent;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyouflf on 15/11/4.
 */
@ContentView(R.layout.fragment_image)
public class ImageFragment extends BaseFragment {

   /* private String[] imgSites = {
            "http://image.baidu.com/",
            "http://www.22mm.cc/",
            "http://www.moko.cc/",
            "http://eladies.sina.com.cn/photo/",
            "http://www.youzi4.com/"
    };*/
private  int total; //总价格


    ImageOptions imageOptions;

    @ViewInject(R.id.lv_img)
    private ListView imageListView;

    @ViewInject(R.id.textView7)
    private TextView textView7;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();

        imageListAdapter = new ImageListAdapter();
        imageListView.setAdapter(imageListAdapter);
        loadImgList();
        // 加载url请求返回的图片连接给listview
        // 这里只是简单的示例，并非最佳实践，图片较多时，最好上拉加载更多...
        /*for (String url : imgSites) {
            loadImgList(url);
        }*/
    }

    @Event(value = R.id.lv_img, type = AdapterView.OnItemClickListener.class)
    private void onImageItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this.getActivity(), BigImageActivity.class);
        intent.putExtra("url", imageListAdapter.getItem(position).toString());
        this.getActivity().startActivity(intent);
    }

    private void loadImgList() {
        RequestParams params = new RequestParams("http://192.168.1.109:8088/score/getAllScore");
        params.addBodyParameter("limit","1111");
        params.addBodyParameter("page","1");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type userType=new TypeToken<ResultObject<List<TScore>>>(){}.getType();
                ResultObject<List<TScore>> userResult=gson.fromJson(result,userType);
                List<TScore> tScores =userResult.getData();

              /*  for (int i = 0; i < tScores.size(); i++) {
                    String str = tScores.get(i).getScoreType();
                    imageListAdapter.addSrc(str);
                    str = null;
                }*/
                imageListAdapter.addSrc(tScores);
                imageListAdapter.notifyDataSetChanged();//通知listview更新数据
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private ImageListAdapter imageListAdapter;

    private class ImageListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<TScore> imgSrcList;

        public ImageListAdapter() {
            super();
            mInflater = LayoutInflater.from(getContext());
            imgSrcList = new ArrayList<TScore>();
        }

        public void addSrc(List<TScore> imgSrcList) {
            this.imgSrcList.addAll(imgSrcList);
        }

       /* public void addSrc(String imgUrl) {
            this.imgSrcList.add(imgUrl);
        }
*/
        @Override
        public int getCount() {
            return imgSrcList.size();
        }

        @Override
        public Object getItem(int position) {
            return imgSrcList.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ImageItemHolder holder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.image_item, parent, false);
                holder = new ImageItemHolder();
                x.view().inject(holder, view);
                view.setTag(holder);
            } else {
                holder = (ImageItemHolder) view.getTag();
            }

            holder.imgPb.setProgress(0);
            holder.textView.setText(imgSrcList.get(position).getStudentName());
            holder.textView4.setText("价格："+String.valueOf(imgSrcList.get(position).getScoreValue()));
            x.image().bind(holder.imgItem,
                    imgSrcList.get(position).getScoreType(),
                    imageOptions,
                    new CustomBitmapLoadCallBack(holder));

            holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int price = imgSrcList.get(position).getScoreValue();
                    total = total + price ;
                    textView7.setText("总价格"+String.valueOf(total)+"元，已优惠"+"0"+"元");
                }
            });





            return view;
        }
    }

    private class ImageItemHolder {
        @ViewInject(R.id.img_item)
        private ImageView imgItem;

        @ViewInject(R.id.img_pb)
        private ProgressBar imgPb;

        @ViewInject(R.id.textView2)
        private TextView textView;
        @ViewInject(R.id.textView4)
        private TextView textView4;

        @ViewInject(R.id.button2)
        Button floatingActionButton;
    }

    public class CustomBitmapLoadCallBack implements Callback.ProgressCallback<Drawable> {
        private final ImageItemHolder holder;

        public CustomBitmapLoadCallBack(ImageItemHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onWaiting() {
            this.holder.imgPb.setProgress(0);
        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {
            this.holder.imgPb.setProgress((int) (current * 100 / total));
        }

        @Override
        public void onSuccess(Drawable result) {
            this.holder.imgPb.setProgress(100);
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }

    /**
     * 得到网页中图片的地址
     */
    public static List<String> getImgSrcList(String htmlStr) {
        List<String> pics = new ArrayList<String>();

        String regEx_img = "<img.*?src=\"http://(.*?).jpg\""; // 图片链接地址
        Pattern p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        Matcher m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            String src = m_image.group(1);
            if (src.length() < 100) {
                pics.add("http://" + src + ".jpg");
                //pics.add("http://f.hiphotos.baidu.com/zhidao/pic/item/2fdda3cc7cd98d104cc21595203fb80e7bec907b.jpg");
            }
        }
        return pics;
    }

}
