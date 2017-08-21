package com.example.cootek.newfastframe;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.example.commonlibrary.utils.FileUtil;
import com.example.cootek.newfastframe.lrc.LrcRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by COOTEK on 2017/8/10.
 */

public class MusicUtil {


    public static final Integer[] RANK_TYPE_LIST = new Integer[]{
            1, 2, 6, 7, 8, 9, 11, 14, 20, 21, 22, 23, 24, 25
    };

    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static String musicLyricDir = FileUtil.getDefaultCacheFile(MainApplication.getInstance()).getAbsolutePath() + "/music/lyric/";

    public static String getLyricPath(long longId) {
        return getMusicLrcCacheDir() + longId;
//        return musicLyricDir + longId + "";
    }


    public static String getMusicLrcCacheDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/music/lrc";
    }


    public static String getMusicImageCacheDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/music/image";
    }


    public static List<LrcRow> parseLrcContent(File file) {
        List<LrcRow> rows = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is == null) {
                return null;
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            rows = getLrcRows(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private static List<LrcRow> getLrcRows(String content) {

        if (TextUtils.isEmpty(content)) {
            return null;
        }
        BufferedReader br = new BufferedReader(new StringReader(content));

        List<LrcRow> lrcRows = new ArrayList<>();
        String lrcLine;
        try {
            while ((lrcLine = br.readLine()) != null) {
                List<LrcRow> rows = LrcRow.createRows(lrcLine);
                if (rows != null && rows.size() > 0) {
                    lrcRows.addAll(rows);
                }
            }
            Collections.sort(lrcRows);
            int len = lrcRows.size();
            for (int i = 0; i < len - 1; i++) {
                lrcRows.get(i).setTotalTime(lrcRows.get(i + 1).getTime() - lrcRows.get(i).getTime());
            }
            lrcRows.get(len - 1).setTotalTime(5000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lrcRows;
    }
}