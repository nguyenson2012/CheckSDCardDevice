package nguyenson.com.checksdcarddevice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button mButtonCheckSDCard;
    private TextView mTextviewDirectories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonCheckSDCard=(Button)findViewById(R.id.buttonCheckSDCard);
        mTextviewDirectories=(TextView)findViewById(R.id.textview_directories);

        String directories="";
        for(String directory:StorageUtil.getStorageDirectories(MainActivity.this)){
            directories+=directory+"\n";
        }
        mTextviewDirectories.setText(directories);

        String[] allDirectoryStorage=StorageUtil.getStorageDirectories(MainActivity.this);
        Bitmap imageExample= BitmapFactory.decodeResource(getResources(), R.drawable.lengoctrinh);
        int numberTypeStorageAvailable=getExternalFilesDirs(Environment.DIRECTORY_PICTURES).length;
        for(int i=0;i<numberTypeStorageAvailable;i++){
            StorageUtil.saveImage(imageExample,getExternalFilesDirs(Environment.DIRECTORY_PICTURES)[i]);
//            File parentFile=new File(directory);
//            File[] childDirectory=parentFile.listFiles();
//            String directoryString="";
//            for(File file:childDirectory){
//                directoryString+=file.toString();
//            }
        }

        mButtonCheckSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                String externalStorageState=android.os.Environment.getExternalStorageState();
                Log.e("Storage",externalStorageState+"");
                if(Environment.isExternalStorageRemovable())
                {
                    Toast.makeText(MainActivity.this,"SD Card available",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"SD Card is not available",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

    /**
     * Raturns all available SD-Cards in the system (include emulated)
     *
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standart way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public static String[] getStorageDirectories()
    {
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if(TextUtils.isEmpty(rawEmulatedStorageTarget))
        {
            // Device has physical external storage; use plain paths.
            if(TextUtils.isEmpty(rawExternalStorage))
            {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            }
            else
            {
                rv.add(rawExternalStorage);
            }
        }
        else
        {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                rawUserId = "";
            }
            else
            {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try
                {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                }
                catch(NumberFormatException ignored)
                {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if(TextUtils.isEmpty(rawUserId))
            {
                rv.add(rawEmulatedStorageTarget);
            }
            else
            {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
        {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }
}
