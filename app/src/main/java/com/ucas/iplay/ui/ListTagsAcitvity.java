package com.ucas.iplay.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.TagModel;
import com.ucas.iplay.ui.adapter.TagsSimpleAdapter;
import com.ucas.iplay.util.HttpUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hoolee
 * 用来展示所有分类tags，并供用户选择关注
 */
public class ListTagsAcitvity extends ActionBarActivity {

    private String[] tagsNames;
    private String[] tagsDesc;
    private TagModel tagsModel[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tags);
        HttpUtil.getAllTags(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("tags");
                    tagsModel = new TagModel[array.length()];
                    tagsNames = new String[array.length()];
                    tagsDesc = new String[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jb = array.getJSONObject(i);
                        System.out.println("jb=" + jb.toString());
                        tagsModel[i] = new TagModel();
                        tagsModel[i].parse(jb);
                        tagsNames[i] = tagsModel[i].tagName;
                        tagsDesc[i] = tagsModel[i].tagName + "的描述";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (tagsModel != null) {
                    List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < tagsNames.length; i++) {
                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("tagImage", R.drawable.ic_launcher);
                        listItem.put("tagName", tagsNames[i]);
                        listItem.put("tagDesc", tagsDesc[i]);
                        listItems.add(listItem);
                    }
                    SimpleAdapter simpleAdapter = new TagsSimpleAdapter(ListTagsAcitvity.this, listItems,
                            R.layout.tags_list,
                            new String[]{"tagImage", "tagName", "tagDesc"},
                            new int[]{R.id.tag_image, R.id.tag_name, R.id.tag_desc, R.id.tag_checkBox},
                            tagsModel);
                    final ListView listView = (ListView) findViewById(R.id.hoolee_tags_list);
                    listView.setAdapter(simpleAdapter);
                } else {
                    Toast.makeText(ListTagsAcitvity.this, "没有可展示的tag", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ListTagsAcitvity.this, "网络请求tag失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}








