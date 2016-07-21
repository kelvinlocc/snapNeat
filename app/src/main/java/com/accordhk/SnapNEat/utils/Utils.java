package com.accordhk.SnapNEat.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.ResponseFileUploadSettings;
import com.accordhk.SnapNEat.models.Restaurant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jm on 16/2/16.
 */
public class Utils {

    private String LOGGER_TAG = "Utils";
    public Context mContext;
    public Activity mActivity;

    public Utils (Context c) {
        mContext = c;
    }

    /**
     * Creates base map used in api requests
     * @return
     */
    public Map<String, String> getBaseRequestMap() {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.STR_SELECTED_LANGUAGE, String.valueOf(new SharedPref(mContext).getSelectedLanguage()));

        return map;
    }

    /**
     * Generates an error dialog
     * @param m -> message to be displayed
     * @return
     */
    public AlertDialog getErrorDialog(String m) {
        return getDialog(getStringResource(R.string.common_error), m, Constants.DIALOG_TYPE.OK.getKey());
    }

    /**
     * Generates success dialog
     * @param m -> message to be displayed
     * @return
     */
    public AlertDialog getDialog(String m) {
        return getDialog(getStringResource(R.string.common_success), m, Constants.DIALOG_TYPE.OK.getKey());
    }

    /**
     * Generates positive/success dialog with title
     * @param t -> title of the dialog
     * @param m -> message to be displayed
     * @return
     */
    public AlertDialog getDialog(String t, String m) {
        return getDialog(t, m, Constants.DIALOG_TYPE.OK.getKey());
    }

    /**
     * Generates alert dialog
     * @param t -> title of the dialog
     * @param m -> message to be displayed
     * @param type -> if CANCEL, this will add a negative button in the dialog
     * @return
     */
    public AlertDialog getDialog(String t, String m, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        boolean noTitle = false;
        if(t != null && !t.isEmpty())
            builder.setTitle(t);
        else
            noTitle = true;

        builder.setMessage(m);

        builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if(type == Constants.DIALOG_TYPE.CANCEL.getKey()) {
            builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        AlertDialog dialog = builder.create();

        if(noTitle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    /**
     * Generates a progress dialog
     * @param m -> message to be displayed
     * @return
     */
    public ProgressDialog getProgressDialog(String m) {
        ProgressDialog dialog = new ProgressDialog(mActivity);

        dialog.setMessage(m);

        return dialog;
    }

    public String getStringResource (int id) {
        Log.d(LOGGER_TAG, "getStringResource ID: " + String.valueOf(id));
        return mContext.getResources().getString(id);
    }

    public String getStringResource (int id, int stringId) {
        String str = mContext.getResources().getString(stringId);
        return String.format(mContext.getResources().getString(id), str);
    }

    public Locale getLocale(int locale){
        Locale l = new Locale(Constants.LANGUAGE.ENG.getValue());

        if(locale == Constants.LANGUAGE.TRAD.getKey())
            l = l.TRADITIONAL_CHINESE;
        else if(locale == Constants.LANGUAGE.SIMP.getKey())
            l = l.SIMPLIFIED_CHINESE;

        return l;
    }

    public int getLocale(Locale locale) {
        int i = Constants.LANGUAGE.ENG.getKey();

        if(locale.getLanguage().equals("zh") && locale.getCountry().equalsIgnoreCase("cn")) {
            i = Constants.LANGUAGE.SIMP.getKey();
        } else if (locale.getLanguage().equals("zh") && locale.getCountry().equalsIgnoreCase("tw")) {
            i = Constants.LANGUAGE.TRAD.getKey();
        }

        return i;
    }

    /**
     * Gets equivalent error message of the status code
     * @param status
     * @param defaultMessage
     * @return
     */
    public String getErrorMessage(int status, String defaultMessage) {
        int id = mContext.getResources().getIdentifier("error_status_" + String.valueOf(status), "string", mContext.getPackageName());

        if(id != 0)
            return getStringResource(id);

        return defaultMessage;
    }

    public String getMessageValue(String message) {
        try {
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isValidEmail(String email) {
        if(email == null)
            return false;
        else
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPhone (String phone) {
        if(phone == null)
            return false;
        else
            return Patterns.PHONE.matcher(phone).matches();
    }

    public boolean isAlphaNumeric(String s){
        String pattern= "^[\\w\\s]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;
    }

    /**
     * Checks is value is valid AlphaNumeric
     * @param strId R.string resource
     * @param checkStr String to be evaluated
     * @param req true if required, else false
     * @return
     */
    public String isValidAlphaNumeric(int strId, String checkStr, boolean req){
        String error = "";

        if(req == true) {
            if(checkStr.isEmpty()) {
                return getStringResource(R.string.error_empty, strId);
            }
        } else {
            if(checkStr.isEmpty() == false) {
                if(isAlphaNumeric(checkStr) == false){
                    return getStringResource(R.string.error_invalid, strId);
                }
            }
        }

        return error;
    }

    /**
     * Checks is value is valid Hashtag
     * @param strId R.string resource
     * @param checkStr String to be evaluated
     * @param req true if required, else false
     * @return
     */
    public String isValidHashTag(int strId, String checkStr, boolean req) {
        return isValidHashTag(strId, checkStr, req, false);
    }

    public String isValidHashTag(int strId, String checkStr, boolean req, boolean allowNoPound) {
//        String pattern = "/[#]+[A-Za-z0-9_]+/g";
//        String pattern = "/^([#][\\w\\d\\s( |,)(#)]+)+/g";
        String pattern = "^#[\\w]+";

        if(allowNoPound)
            pattern = "^(#|)[\\w]+";

        String error = "";

        if(req == true) {
            if(checkStr.isEmpty())
                return getStringResource(R.string.error_empty, strId);
        } else {
            if(checkStr.isEmpty() == false) {
//                Log.d("Match pattern? ", ""+checkStr.matches(pattern));
//                if(checkStr.matches(pattern) == false)
//                    return getStringResource(R.string.error_invalid, strId);

                String[] parts = checkStr.split(" ");
                for(String s: parts) {
                    Log.d("Hash: ", s.trim());
                    if(s.trim().isEmpty() == false) {
                        if(s.trim().matches(pattern) == false)
                            return getStringResource(R.string.error_invalid, strId);
                    }
                }
            }
        }

        return error;
    }

    /**
     * Checks if value is a valid comment
     * @param strId
     * @param checkStr
     * @param req
     * @return
     */
    public String isValidComment(int strId, String checkStr, boolean req) {
        String pattern= "^[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\=\\_\\+\\{\\}\\[\\]\\|\\:\\\"\\'\\;\\<\\>\\\\\\,\\.\\?\\/\\~\\`\\w\\s]*$";
        String error = "";

        if(req == true) {
            if(checkStr.isEmpty())
                return getStringResource(R.string.error_empty, strId);
        } else {
            if(checkStr.isEmpty() == false) {
                if(checkStr.matches(pattern) == false)
                    return getStringResource(R.string.error_invalid, strId);
            }
        }

        return error;
    }

    /**
     * Checks is value is valid Mention
     * @param strId R.string resource
     * @param checkStr String to be evaluated
     * @param req true if required, else false
     * @return
     */
    public String isValidMention(int strId, String checkStr, boolean req) {
        String pattern = "^@[\\w]+";
        String error = "";

        if(req == true) {
            if(checkStr.isEmpty())
                return getStringResource(R.string.error_empty, strId);
        } else {
            if(checkStr.isEmpty() == false) {
                String[] parts = checkStr.split(" ");
                for(String s: parts) {
                    Log.d("Mention: ", s.trim());
                    if(s.trim().isEmpty() == false) {
                        if(s.trim().matches(pattern) == false)
                            return getStringResource(R.string.error_invalid, strId);
                    }
                }
            }
        }

        return error;
    }

    /**
     * Validates username
     *
     * @param username -> email to validate
     * @param req -> true if required, otherwise false
     * @return error message -> empty if no error
     */
    public String validateUsername(String username, boolean req) {
        String error = "";
        if(req == true) {
            if(username.isEmpty())
                return getStringResource(R.string.error_empty_username);
        }

        if(!isAlphaNumeric(username)) {
            error = getStringResource(R.string.error_invalid_username);
        }

        return error;
    }

    /**
     * Validates email address
     *
     * @param email -> email to validate
     * @param req -> true if required, otherwise false
     * @return error message -> empty if no error
     */
    public String validateEmail(String email, boolean req) {
        String error = "";
        if(req == true) {
            if(email.isEmpty())
                return getStringResource(R.string.error_empty_email);
        }

        if(!isValidEmail(email)) {
            error = getStringResource(R.string.error_invalid_email);
        }

        return error;
    }

    /**
     * Validate password
     *
     * @param password -> password to validate
     * @param req -> true if required, otherwise false
     * @return error message -> empty if no error
     */
    public String validatePassword(String password, boolean req) {
        String error = "";
        if(req == true) {
            if(password.isEmpty())
                return getStringResource(R.string.error_empty_password);
        }

        if(password.length() < Constants.MINIMUM_PASSWORD_LENGTH) {
            error = String.format(getStringResource(R.string.error_minimum_password), Constants.MINIMUM_PASSWORD_LENGTH);
        }

        //TODO check if need to do validation other than just checking the password length

        return error;
    }

    /**
     * Validate confirm password
     *
     * @param password -> password to validate
     * @return error message -> empty if no error
     */
    public String validateConfirmPassword(String password) {
        String error = "";
        if(password.isEmpty())
            error = getStringResource(R.string.error_empty_conf_password);

        //TODO check if need to do validation other than just checking the password length

        return error;
    }

    /**
     * Validates password and confirm password
     *
     * @param password -> password to validate
     * @param confPassword -> confirm password to validate
     * @return error message -> empty if no error
     */
    public String validatePasswordAndConfPassword(String password, String confPassword) {
        String error = "";

//        error = validatePassword(password, true);
//        if(!error.isEmpty())
//            return error;
//
//        error = validateConfirmPassword(confPassword);
//        if(!error.isEmpty())
//            return error;

        if(password.equals(confPassword) == false)
            error = getStringResource(R.string.error_passwords_not_equal);

        return error;

    }

    /**
     * Process the bitmap for upload
     * @param activity
     * @param bitmap
     * @return
     */
    public Map<String, Object> processBitmap(Activity activity, Bitmap bitmap) {
//        return processBitmap(activity, bitmap, -1);
        return processBitmap(activity, bitmap, -1);
    }

    public Map<String, Object> processBitmap(Activity activity, Bitmap bitmap, int scaleSize) {
        Map<String, Object> result = new HashMap<String, Object>();

        Log.d(LOGGER_TAG, "BITMAP SIZE: " + String.valueOf(bitmap.getWidth()) + "x" + String.valueOf(bitmap.getHeight()));

        Bitmap bMapScaled = bitmap;

        if(scaleSize > -1) {
            if(bitmap.getHeight() > scaleSize || bitmap.getWidth() > scaleSize) {
                bMapScaled = getResizedBitmap(bitmap, scaleSize);
            }
        }

        String path = getImagePathFromURI(activity, Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bMapScaled, "Title", null)));
        Log.d(LOGGER_TAG, "photofilepath: " + path);
        result.put(Constants.PHOTO_PATH, path);

        result.put(Constants.PHOTO_BITMAP, bMapScaled);
        bMapScaled.recycle();
        return result;
    }

    /**
     * Rotates bitmap to correct orientation
     * @param photoFilePath
     * @return
     */
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public String getLocalURIBitmapToBase64(Activity activity, Uri uri) {
        String photoFilePath = getImagePathFromURI(activity, uri);

        if (photoFilePath == null) {
            return null;
        }

        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);

        return convertBitmapToBase64(bm);
    }

    /**
     * Helper to retrieve the path of an image URI
     */
    public String getImagePathFromURI(Activity activity, Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    /**
     * Converts bitmap to base 64 string
     * @param bitmap Bitmap image
     * @return base 64 String
     */
    public String convertBitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            String temp = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
            return temp;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public Map<String, String> generateAuthHeader() {
        Map<String, String> header = new HashMap<String, String>();

        String sessionString = new SharedPref(mContext).getSessionString();
        Log.d(LOGGER_TAG, "SessionString: "+sessionString);
        if(sessionString.isEmpty() == false)
            header.put("Authorization", "SnapNEat:"+sessionString);

        return header;
    }

    public void dismissDialog(ProgressDialog d) {
        if(d.isShowing())
            d.dismiss();
    }

    public enum Mode {
        ALPHA, ALPHANUMERIC, NUMERIC;
    }

    public String randomString(int length, Mode mode) {
        StringBuffer buffer = new StringBuffer();
        String characters = "";

        switch (mode) {
            case ALPHA:
                characters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                break;

            case ALPHANUMERIC:
                characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                break;

            case NUMERIC:
                characters = "1234567890";
                break;
        }

        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }

        return buffer.toString();
    }

    public void resetSearchSettings() {
        SharedPref sharedPref = new SharedPref(mContext);
        sharedPref.setSelectedPostPhotos(new ArrayList<String>());
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISH.getKey(), new ArrayList<String>());
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.SPENDINGS.getKey(), new ArrayList<String>());
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISTRICT.getKey(), new ArrayList<String>());
        sharedPref.setSelectedRestaurant(new Restaurant());
    }

    public void resetPreference() {
        // reset All except language
        SharedPref sharedPref = new SharedPref(mContext);
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISH.getKey(), new ArrayList<String>());
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.SPENDINGS.getKey(), new ArrayList<String>());
        sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISTRICT.getKey(), new ArrayList<String>());
        sharedPref.setSelectedRestaurant(new Restaurant());
        sharedPref.setLoggedInUser(null);
        sharedPref.setIsFacebookUser(false);
        sharedPref.resetFBProfilePic();
        sharedPref.setFileUploadSettings(new ResponseFileUploadSettings());
    }

    public String listToString(List<String> list) {
        return android.text.TextUtils.join(",", list);
    }

    public List<String> stringToList(String sel) {
        if(sel.isEmpty())
            return new ArrayList<String>();

        return new LinkedList<String>(Arrays.asList(sel.split("\\s*,\\s*")));//Arrays.asList(sel.split("\\s*,\\s*"));
    }

    public List<String> listStringToLower(List<String> list) {
        for(int i=0; i < list.size(); i++) {
            list.set(i, list.get(i).toLowerCase());
        }
        return list;
    }

    public String getFileImageExtension(String filename) {
        String ext = "";
        if (filename.contains(".")) {
            ext = filename.substring(filename.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("jpg"))
                return Constants.FILE_TYPE_JPG.toLowerCase();
        }
        return ext.toLowerCase();
    }

    public String validateUploadImage(Map<String, Object> bitmapResult) {
        String filename = bitmapResult.get(Constants.PHOTO_PATH).toString().trim();

        File img = new File(filename);

        SharedPref sharedPref = new SharedPref(mContext);
        int maxFileUploadSize = sharedPref.getFileUploadSettingSize();
        List<String> fileUploadTypes = listStringToLower(sharedPref.getFileUploadSettingTypes());

        Log.d("file size: ", String.valueOf(img.length()));
        Log.d("max allowed: ", String.valueOf(maxFileUploadSize));

        String error = "";
        if(maxFileUploadSize > 0) {
            //if(b.getAllocationByteCount() > maxFileUploadSize) {
            if(img.length() > maxFileUploadSize) {
                error = String.format(getStringResource(R.string.error_file_setting_size), (maxFileUploadSize/1000000));
            }
        }
        if(fileUploadTypes.isEmpty() == false) {
            if(fileUploadTypes.contains(getFileImageExtension(filename)) == false)
                error = getStringResource(R.string.error_file_setting_type);
        }

        return error;
    }

    public final String twoHyphens = "--";
    public final String lineEnd = "\r\n";

    public void buildJsonPart(String boundary, DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: application/json; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
        Log.d("json", parameterValue);
    }

    public void buildFilePart(String boundary, DataOutputStream dataOutputStream, String parameterName, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    public byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] converUriToFileData(Activity activity, Uri uri) {
        String photoFilePath = getImagePathFromURI(activity, uri);

        if (photoFilePath == null) {
            return null;
        }

        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, opts);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * MapWrapperLayout initialization
     * @param dp
     * @return
     */
    public int getPixelsFromDp(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
