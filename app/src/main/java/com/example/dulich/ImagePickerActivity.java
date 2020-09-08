package com.example.dulich;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;

public class ImagePickerActivity extends AppCompatActivity {
    //Key khóa tỉ lệ ảnh khi cắt
    private int KHOA_TI_LE_X = 1, KHOA_TI_LE_Y = 1;
    private boolean KHOA_TI_LE = true;
    //Key giới hạn kích thước ảnh khi cắt
    private int MAX_WIDTH = 500, MAX_HEIGHT = 500;
    private  boolean SET_KICH_THUOC_ANH = true;
    //Key chất lượng ảnh khi cắt
    private int CHAT_LUONG_ANH = 80;
    //Key Request khi lựa chọn cách lấy ảnh
    public static final String REQUEST_LUA_CHON = "phuong_thuc_chon";
    public static final int REQUEST_CHUP_ANH = 0;
    public static final int REQUEST_CHON_ANH = 1;

    String ten_file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        int request = i.getIntExtra(REQUEST_LUA_CHON, REQUEST_CHON_ANH);
        switch (request){
            case REQUEST_CHUP_ANH:
                ChupAnh();
                break;
            default:
                ChonAnh();
                break;
        }
    }

    //Giao diện chụp ảnh
    public void ChupAnh() {
        ten_file = System.currentTimeMillis() + ".jpg";
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(ten_file));
        startActivityForResult(i, REQUEST_CHUP_ANH);
    }

    //Giao diện chọn ảnh
    public void ChonAnh() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CHON_ANH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHUP_ANH: {
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(ten_file));
                } else {
                    setResultCancelled();
                }
                break;
            }
            case REQUEST_CHON_ANH: {
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            }
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;
            }
            case UCrop.RESULT_ERROR: {
                Throwable error = UCrop.getError(data);
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                setResultCancelled();
                break;
            }
            default:
                setResultCancelled();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public Uri getCacheImagePath(String ten_file) {
        File path = new File(getExternalCacheDir(), "camera");

        if (!path.exists()) {
            path.mkdirs();
        }

        File image = new File(path, ten_file);

        return FileProvider.getUriForFile(ImagePickerActivity.this, getPackageName() + ".provider", image);
    }

    //Hàm gọi xử lý crop ảnh
    public void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();

        //Set chất lượng ảnh sau khi crop
        options.setCompressionQuality(CHAT_LUONG_ANH);

        options.setToolbarColor(ContextCompat.getColor(this, R.color.color));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.color));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.color));

        //Nếu true thì sẽ gắn tỉ lệ vào
        if (KHOA_TI_LE) {
            options.withAspectRatio(KHOA_TI_LE_X, KHOA_TI_LE_Y);
        }

        //Nếu true thì sẽ giới hạn kích thước ảnh
        if (SET_KICH_THUOC_ANH) {
            options.withMaxResultSize(MAX_WIDTH, MAX_HEIGHT);
        }

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    //Lấy tên file thông qua uri của file
    public String queryName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        assert cursor != null;
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();
        return name;
    }



}
