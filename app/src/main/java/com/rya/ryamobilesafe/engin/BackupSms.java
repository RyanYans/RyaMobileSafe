package com.rya.ryamobilesafe.engin;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BackupSms {

    /**
     * 备份短信文件
     *
     * @param context       上下文环境
     * @param path          储存路径
     * @param callBack      回掉观察者
     */
    public static void backup(Context context, String path, CallBack callBack) {
        Cursor cursor = null;
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(path, "SMSbk1.xml");
            fileOutputStream = new FileOutputStream(file);
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);

            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fileOutputStream, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");

            //设置dialog的最大值
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }
            int count = 0;

            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");

                Thread.sleep(125);
                count++;
                if (callBack != null) {
                    callBack.setProgress(count);
                }
            }
            xmlSerializer.endTag(null, "smss");

            xmlSerializer.endDocument();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fileOutputStream != null) {
                try {
                    cursor.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface CallBack {

        public void setMax(int index);

        public void setProgress(int index);
    }


}
