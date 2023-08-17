package cn.demomaster.qdlogger_library.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

import cn.demomaster.qdlogger_library.QDLogger;

public class QDFileUtil {
    static String TAG = QDFileUtil.class.getSimpleName();
    /**
     * 写数据到SD中的文件
     *
     * @param fileName
     * @param write_str
     * @throws IOException
     */
    public static void writeFileSdcardFile(String dirPath, String fileName,
                                           String write_str, boolean append) {
        //Environment.getExternalStorageDirectory(),
        File file = new File(dirPath + File.separator + fileName);
        writeFileSdcardFile(file, write_str.getBytes(), append);
    }

    public static void writeFileSdcardFile(String fileName,
                                           byte[] bytes, boolean append) {
        writeFileSdcardFile(new File(fileName), bytes, append);
    }

    public static void writeFileSdcardFile(File file,
                                           String write_str, boolean append) {
        writeFileSdcardFile(file,write_str.getBytes(),append);
    }

    public static void writeFileSdcardFile(File file,
                                           byte[] bytes, boolean append) {
        FileOutputStream fout = null;
        try {
            if (!file.exists()) {
                createFile(file);
            }
            if (file.exists()) {
                fout = new FileOutputStream(file, append);
                fout.write(bytes);
                fout.flush();
                fout.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    //读SD中的文件
    public static String readFileLine(String path,int lineIndex) {
        FileReader fileReader = null;
        LineNumberReader reader = null;
        String str = "";
        try {
            fileReader = new FileReader(path);
            reader = new LineNumberReader(fileReader);
            reader.setLineNumber(lineIndex);
            W:while ((str = reader.readLine()) != null) {
                if (reader.getLineNumber() == lineIndex+1) {
                    break W;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    // 文件内容的总行数
    static int getTotalLines(String fileName) {
        LineNumberReader reader = null;
        BufferedReader br = null;
        int lines = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))); //使用缓冲区的方法将数据读入到缓冲区中
            reader = new LineNumberReader(br);
            String s = null; //定义行数
            s = reader.readLine();
            while (s != null) //确定行数
            {
                lines++;
                s = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines; //返回行数
    }

    //读SD中的文件
    public static String readSdcardFile(String fileName) {
        String res = null;
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, Charset.forName("UTF-8"));
            //res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            QDLogger.e(e);
        }
        return res;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFromAssets(Context context, String fileName) {
        InputStream is;
        String text = "";
        try {
            is = context.getResources().getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            text = new String(buffer, Charset.forName("UTF-8"));
        } catch (IOException e) {
            QDLogger.e(e);
        }
        return text;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteSingleFile(file.getAbsolutePath());
            } else {
                return deleteDirectory(file.getAbsolutePath());
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath) {
        File file = new File(filePath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                //Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                File tmp = new File("tmp123");
                file.renameTo(tmp);
                return tmp.delete();
            }
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "deleteDirectory: " + filePath + " success！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通过uri获取绝对路径
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static File uriToFile(Uri uri, Context context) {
        if (uri == null || context == null) {
            return null;
        }
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                String buff = "(" +
                        MediaStore.Images.ImageColumns.DATA +
                        "=" +
                        "'" +
                        path +
                        "'" +
                        ")";
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff, null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            return new File(path);
        } else {
            return new File(uri.getPath());
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    public static boolean existsFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String createFile(File file) {
        try {
            if (!file.exists()) {//创建目录之后再创建文件
                createDir(file.getParentFile().getAbsolutePath());
                Log.d(TAG, "创建文件:" + file.getAbsolutePath());
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return file.getAbsolutePath();
    }

    public static String createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            Log.d(TAG, "创建文件夹:" + file.getAbsolutePath());
            file.mkdirs();
        }
        return dirPath;
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 写数据到SD中的文件
     *
     * @param fileName
     * @param write_str
     * @throws IOException
     */
    public static void writeFileSdcardFile(String filePath, String fileName,
                                           String write_str) {
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath + "/" + fileName);
            FileOutputStream fout = new FileOutputStream(file);
            byte[] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            QDLogger.e(e);
        }
    }

    /**
     * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径。前者获取到的就是 /sdcard/Android/data//cache 这个路径，而后者获取到的是 /data/data//cache 这个路径。
     *   注意：这两种方式的缓存都会在卸载app的时候被系统清理
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir == null) {
                cachePath = context.getCacheDir().getPath();
            } else {
                cachePath = externalCacheDir.getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    public static String getVersionName(Context context, String packageName) {
        PackageInfo pi = getPackageInfoByPackageName(context, packageName);
        return pi != null ? pi.versionName : null;
    }

    // 获取本地的版本号
    public static int getVersionCode(Context context) {
        if (context == null) return -1;
        return getVersionCode(context, context.getPackageName());
    }

    // 获取本地的版本号
    public static int getVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfoByPackageName(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return -1;
    }
    /**
     * 根据包名获取版本信息
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getPackageInfoByPackageName(Context context, String packageName) {
        if (context == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            QDLogger.e("未找到安裝包：" + packageName);
        }
        return null;
    }
}
