package com.kiba.framework.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 通讯录工具类
 */
public class ContactsUtils {

    private Activity context;
    //要查询的字段
    private String[] query_all = new String[]{
            ContactsContract.CommonDataKinds.Identity.RAW_CONTACT_ID, //用户id
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //联系人姓名
            ContactsContract.CommonDataKinds.Phone.NUMBER //联系人电话
    };
    private  String name = "AI步惊云";
    private String phone = "15988888888";
    public ContactsUtils(Activity _context)
    {
        context=_context;
    }


    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
            }
        }
    }

    /**
     * 插入电话号码
     */
    public void insert() {

        ContentValues values = new ContentValues();
        Uri uri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContentID = ContentUris.parseId(uri);

        if (!name.equals("")) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContentID);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        if (!phone.equals("")) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContentID);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }

    }
    /**
     * 获取电话
     */
    public void getPhone()
    {
        //参数二：表示要查询的字段，如果为null，表示查询所有的字段
        Cursor cursor1 = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, query_all, null, null, null);
        printQueryResult(cursor1);

    }

    /**
     * 删除电话
     */
    public void deletePhone()
    {
        if (!name.equals("")) {
            context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[]{name});

        }
    }

    public void updatePhone()
    {
        Long rawContactId = 0L;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values1 = new ContentValues();
        values1.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
        if (!name.equals("")) {
            Cursor cursor2 = getContentName(name);
            if (cursor2.moveToFirst()) {
                rawContactId = cursor2.getLong(0);
            }
            resolver.update(ContactsContract.Data.CONTENT_URI, values1, "mimetype=? and raw_contact_id=?"
                    , new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, rawContactId + ""});
            cursor2.close();
        }

    }

    private Cursor getContentName(String name_search) {
        String selections = ContactsContract.Contacts.DISPLAY_NAME + "=?";//查询条件
        String[] selection_args = new String[]{name_search};//查询参数
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, query_all, selections, selection_args, null);
        return cursor;
    }

    private void printQueryResult(Cursor cursor) {
        if (cursor != null) {
            StringBuilder contact= new StringBuilder();
            while (cursor.moveToNext()) {
                String ID = cursor.getString(0);
                String stringName = cursor.getString(1);
                String phone = cursor.getString(2);
                contact.append("\n联系人ID:" + ID + "\n联系人姓名：" + stringName + "\n联系人电话:" + phone);
            }
        }
        cursor.close();
    }

}
