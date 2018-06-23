package unical.it.oauth2nativeapps.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import unical.it.oauth2nativeapps.R;
import unical.it.oauth2nativeapps.model.DriveFile;
import unical.it.oauth2nativeapps.utils.PreferenceUtils;

public class FileInfoAdapter extends RecyclerView.Adapter<FileInfoAdapter.DriveFileViewHolder> {

    private ArrayList<DriveFile> driveFiles;

    private Context context;

    private ImagePopup imagePopup;

    public void setContext(Context context) {
        this.context = context;
        imagePopup = new ImagePopup(context);
        imagePopup.setHideCloseIcon(true);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int padding = 30;
        int squarepx = Math.min(width, height);
        imagePopup.setWindowHeight(squarepx-padding);
        imagePopup.setWindowWidth(squarepx-padding);
    }

    public void setDriveFiles(ArrayList<DriveFile> driveFiles) {
        this.driveFiles = driveFiles;
    }

    @Override
    public DriveFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_drive_files_items, parent, false);
        return new DriveFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DriveFileViewHolder holder, int position) {
        holder.title.setText(driveFiles.get(position).getTitle());
        if(driveFiles.get(position).getFileExtension().equals("png"))
            holder.image.setImageResource(R.drawable.ic_png_file);
        else if(driveFiles.get(position).getFileExtension().equals("jpg"))
            holder.image.setImageResource(R.drawable.ic_jpg_file);
        else
            holder.image.setImageResource(R.drawable.ic_image_file);
        Picasso.with(context)
                .load(driveFiles.get(position).getDownloadUrl() +
                        "&access_token=" + PreferenceUtils.getOAuthToken(context).getAccess_token())
                .into(holder.popupImage);

    }

    @Override
    public int getItemCount() {
        if (driveFiles != null)
            return driveFiles.size();
        return 0;
    }

    class DriveFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;

        private ImageView image;

        private ImageView popupImage;

        DriveFileViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            image = (ImageView) itemView.findViewById(R.id.imageView);
            popupImage = (ImageView) itemView.findViewById(R.id.popupImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            imagePopup.initiatePopup(popupImage.getDrawable());
            imagePopup.viewPopup();
        }
    }
}