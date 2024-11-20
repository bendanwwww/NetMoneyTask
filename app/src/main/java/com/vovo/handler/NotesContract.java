package com.vovo.handler;

import android.net.Uri;

/**
 * @Auther: liuzeheng@zhihu.com
 * @Date: 2024/11/15
 * @Description:
 */

public class NotesContract {
    // 定义 URI 和表名
    public static final String AUTHORITY = "com.vovo.netMoneyTask.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
}

