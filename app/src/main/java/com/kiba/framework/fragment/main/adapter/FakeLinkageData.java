package com.kiba.framework.fragment.main.adapter;

import com.google.gson.reflect.TypeToken;
import com.kiba.framework.utils.file.FileUtils;
import com.kiba.framework.utils.json.JsonUtils;

import java.util.List;

public class FakeLinkageData {



    public static List<ElemeGroupedItem> getElemeGroupItems() {
        return JsonUtils.Serialize_Gson( FileUtils.readStringFromAssert("linkageData.json","utf-8"),new TypeToken<List<ElemeGroupedItem>>() {
        }.getType());
    }
}
