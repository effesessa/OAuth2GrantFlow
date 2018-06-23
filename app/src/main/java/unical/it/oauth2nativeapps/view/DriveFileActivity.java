package unical.it.oauth2nativeapps.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import unical.it.oauth2nativeapps.R;
import unical.it.oauth2nativeapps.adapter.FileInfoAdapter;
import unical.it.oauth2nativeapps.model.DriveFile;
import unical.it.oauth2nativeapps.utils.PreferenceUtils;
import unical.it.oauth2nativeapps.utils.SpaceItemDecoration;

public class DriveFileActivity extends AppCompatActivity {

    private static final String TAG = "DriveFileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_file);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(7));
        recyclerView.setHasFixedSize(true);
        FileInfoAdapter fileInfoAdapter = new FileInfoAdapter();
        ArrayList<DriveFile> list = new ArrayList<>();
        list.addAll(PreferenceUtils.getDriveFiles(this));
        fileInfoAdapter.setDriveFiles(list);
        fileInfoAdapter.setContext(this);
        recyclerView.setAdapter(fileInfoAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceUtils.removeAllDriveFile(this);
    }
}
