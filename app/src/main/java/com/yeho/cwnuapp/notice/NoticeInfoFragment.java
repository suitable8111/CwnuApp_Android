package com.yeho.cwnuapp.notice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yeho.cwnuapp.R;
import com.yeho.cwnuapp.bus.BusDetailActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KimDaeho on 16. 4. 20..
 */
public class NoticeInfoFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private PaserNTThread thread = null;
    private ListView noticeListView = null;
    private ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
    private NoticeAdapter noticeAdapter = null;
    private int mPageNumber;
    private boolean lastItemVisibleFlag = false;
    private int curPage = 1;
    public static NoticeInfoFragment newInstance(int page) {
        NoticeInfoFragment noticeInfoFragment = new NoticeInfoFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        noticeInfoFragment.setArguments(args);


        return noticeInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_notice_info, container, false);

        noticeListView = (ListView)view.findViewById(R.id.notice_info_fragment_listView);
        noticeAdapter = new NoticeAdapter(NoticeInfoFragment.this.getContext(),allAry);
        noticeListView.setAdapter(noticeAdapter);
        parsingBoard();


        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentND = new Intent(view.getContext(), NoticeDetailFragment.class);
                if (allAry.get(position).get("boardid").contains("href")) {
                    String tmpString = allAry.get(position).get("boardid");
                    tmpString = tmpString.substring(tmpString.indexOf("postno="),tmpString.indexOf("where=")-1);
                    tmpString = tmpString.substring(7,tmpString.length());

                    intentND.putExtra("noticeBoarid", tmpString);
                }else {
                    intentND.putExtra("noticeBoarid",allAry.get(position).get("boardid"));
                }

                intentND.putExtra("currentPage",mPageNumber);
                startActivity(intentND);
            }
        });
        noticeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    Log.d("바닥 :", "바닥바닥");
                    curPage++;
                    parsingBoard();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });


        return view;
    }
    private void parsingBoard(){
        mProgressDialog = ProgressDialog.show(NoticeInfoFragment.this.getContext(),"", "정보를 가져오고 있습니다.",true);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        ArrayList<HashMap<String, String>> tmpAry = (ArrayList<HashMap<String,String>>)msg.obj;

                        for (int i = 0; i < tmpAry.size(); i++){
                            allAry.add(tmpAry.get(i));
                        }
                        noticeAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                mProgressDialog.dismiss();
            }
        };
        thread = new PaserNTThread(handler);
        thread.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("someInt");
    }

    private class PaserNTThread extends Thread {
        ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapA;
        ArrayList<String> flagList = new ArrayList<>();
        Handler handler = null;

        public PaserNTThread(Handler handler){
            this.handler = handler;
            this.flagList.add("boardid");
            this.flagList.add("title");
            this.flagList.add("name");
            this.flagList.add("posttime");
            this.flagList.add("count");
        }

        @Override
        public void run() {
            Source source = null;

            try {
                //와글 홈 공지
                String urlString = "http://portal.changwon.ac.kr/portalMain/mainonHomePostList.do";
                switch (mPageNumber){
                    case 0:
                        //와글 홈공지
                        urlString = "http://portal.changwon.ac.kr/portalMain/mainonHomePostList.do?currPage="+curPage;
                        break;
                    case 1:
                        //학사안내
                        urlString = "http://portal.changwon.ac.kr/homePost/list.do?common=portal&homecd=portal&bno=3305&currPage="+curPage;
                        break;
                    case 2:
                        //공지사항
                        urlString = "http://portal.changwon.ac.kr/homePost/list.do?common=portal&homecd=portal&bno=1291&currPage="+curPage;
                        break;
                    case 3:
                        //모집안내
                        urlString = "http://portal.changwon.ac.kr/homePost/list.do?common=portal&homecd=portal&bno=1293&currPage="+curPage;
                        break;
                }


                URL url = new URL(urlString);
                InputStream is = url.openStream();
                source = new Source(new InputStreamReader(is,"UTF-8"));
                source.fullSequentialParse();
                boolean noticeBool = false;
                Element table = (Element)source.getAllElements(HTMLElementName.TABLE).get(0);
                List<Element> td = (List<Element>)table.getAllElements(HTMLElementName.TD);
                if (mPageNumber == 0){
                    int count = 0;
                    for (Element li : td){

                        switch (count % 5){
                            case 0:
                                mapA = new HashMap<String, String >();
                                mapA.put(flagList.get(count % 5),li.getContent().toString());
                                break;
                            case 1:
                                Element tmp = li.getAllElements(HTMLElementName.A).get(0);
                                mapA.put(flagList.get(count % 5),tmp.getContent().toString());
                                break;
                            case 2:
                                Element tmp2 = li.getAllElements(HTMLElementName.A).get(0);
                                mapA.put(flagList.get(count % 5),tmp2.getContent().toString());
                                break;
                            case 3:
                                mapA.put(flagList.get(count % 5),li.getContent().toString());
                                break;
                            case 4:
                                mapA.put(flagList.get(count % 5),li.getContent().toString());
                                allAry.add(mapA);
                                break;
                            default:
                                break;
                        }

                        count++;
                    }
                }else if (mPageNumber == 1){

                    int count = 0;
                    for (Element li : td){

                        switch (count % 5){
                            case 0:
                                //게시번호
                                mapA = new HashMap<String, String >();
                                if (li.getContent().toString().contains("alt=")) {
                                    noticeBool = true;
                                }else {
                                    mapA.put(flagList.get(count % 5), li.getContent().toString());
                                }
                                break;
                            case 1:
                                //제목
                                if (noticeBool){
                                    mapA.put(flagList.get((count % 5)-1),li.getContent().toString());
                                    noticeBool = false;
                                }
                                Element tmp = li.getAllElements(HTMLElementName.A).get(0);
                                mapA.put(flagList.get(count % 5),tmp.getContent().toString());
                                break;
                            case 2:
                                //글쓴이
                                Element tmp2 = li.getAllElements(HTMLElementName.SPAN).get(0);
                                mapA.put(flagList.get(count % 5),tmp2.getContent().toString());

                                break;
                            case 3:
                                //작성일
                                mapA.put(flagList.get(count % 5),li.getContent().toString());
                                break;
                            case 4:
                                //글쓴이
                                mapA.put(flagList.get(count % 5),li.getContent().toString());
                                allAry.add(mapA);
                                break;
                            default:
                                break;
                        }

                        count++;
                    }

                } else {
                    int count = 0;
                    for (Element li : td){

                        switch (count % 4){
                            case 0:
                                //게시번호
                                mapA = new HashMap<String, String >();
                                if (li.getContent().toString().contains("alt=")) {
                                    noticeBool = true;
                                }else {
                                    mapA.put(flagList.get(count % 4), li.getContent().toString());
                                }
                                break;
                            case 1:
                                //제목
                                if (noticeBool){
                                    mapA.put(flagList.get((count % 4)-1),li.getContent().toString());
                                    noticeBool = false;
                                }
                                Element tmp = li.getAllElements(HTMLElementName.A).get(0);
                                mapA.put(flagList.get(count % 4),tmp.getContent().toString());
                                break;
                            case 2:
                                mapA.put(flagList.get((count % 4)+1),li.getContent().toString());
                                break;
                            case 3:
                                //작성일
                                mapA.put(flagList.get((count % 4)+1),li.getContent().toString());
                                mapA.put(flagList.get(2),"창원대");
                                allAry.add(mapA);
                                break;
                            default:
                                break;
                        }


                        count++;
                    }
                }


                Message msg = new Message();
                msg.what = 0;
                msg.obj = this.allAry;
                this.handler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Message msg = new Message();
                msg.what = 1;

                this.handler.sendMessage(msg);
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                Message msg = new Message();
                msg.what = 1;

                this.handler.sendMessage(msg);
                e.printStackTrace();
            } catch (IOException e) {
                Message msg = new Message();
                msg.what = 1;

                this.handler.sendMessage(msg);
                e.printStackTrace();
            }

        }
    }
}
