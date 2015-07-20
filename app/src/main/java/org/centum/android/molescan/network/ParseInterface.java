package org.centum.android.molescan.network;

import android.content.Context;
import android.provider.Settings;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phani on 9/6/2014.
 */
public class ParseInterface {

    private static final String APPLICATION_ID = "6MkCnotxA0Au6o58H5oPnE4Opui8446KTjHV8Zrj";
    private static final String CLIENT_ID = "gVQC2OxNvhC39VQ6nVwVbo6d8SjX9fYeWn2DMXni";

    public static final String ID_KEY = "ID";
    public static final String NEED_COMMENT_KEY = "needComment";
    public static final String IMAGE_KEY = "image";
    public static final String COMMENTS_KEY = "comments";
    public static final String COMMENTS_FROM_KEY = "commentsFrom";
    public static final String UNREAD_KEY = "unread";
    public static final String CLASS_NAME = "Capture";

    private String deviceId = null;

    private static ParseInterface instance = null;

    private Context context;

    private ParseInterface(Context context) {
        this.context = context;
        Parse.initialize(context, APPLICATION_ID, CLIENT_ID);
    }

    public void uploadImage(String path) throws ParseException {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParseFile parseFile = new ParseFile("img.png", bytes);
        parseFile.save();
        ParseObject obj = new ParseObject(CLASS_NAME);
        obj.put(ID_KEY, getID());
        obj.put(NEED_COMMENT_KEY, true);
        obj.put(IMAGE_KEY, parseFile);
        obj.put(COMMENTS_KEY, "");
        obj.put(COMMENTS_FROM_KEY, "");
        obj.put(UNREAD_KEY, true);
        obj.save();
    }

    public List<ParseObject> getNewComments() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_NAME).whereEqualTo(ID_KEY, getID());
        List<ParseObject> objs = query.find();
        List<ParseObject> newComments = new ArrayList<ParseObject>();

        for (ParseObject object : objs) {
            if (object.has(NEED_COMMENT_KEY) && !object.getBoolean(NEED_COMMENT_KEY) && object.getBoolean(UNREAD_KEY)) {
                newComments.add(object);
            }
        }

        return newComments;
    }

    public void markAsComplete(ParseObject obj) throws ParseException {
        obj.put(UNREAD_KEY, false);
        obj.save();
    }

    public static void init(Context context) {
        instance = new ParseInterface(context);
    }

    public static ParseInterface get() {
        return instance;
    }

    private String getID() {
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

}
