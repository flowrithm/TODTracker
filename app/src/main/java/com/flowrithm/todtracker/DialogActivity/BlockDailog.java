package com.flowrithm.todtracker.DialogActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.flowrithm.todtracker.Activity.MainActivity;
import com.flowrithm.todtracker.R;
import com.flowrithm.todtracker.Utils.Utils;
import com.flowrithm.todtracker.Utils.VolleyMultipartRequest;
import com.flowrithm.todtracker.Utils.VolleySingleton;
import com.flowrithm.todtracker.WebApi.Api;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

public class BlockDailog extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etReason)
    EditText etReason;

    @Bind(R.id.img)
    ImageView img;

    @Bind(R.id.btnBlock)
    Button btnBlock;

    @Bind(R.id.txtHeader)
    TextView txtHeader;

    int Status,TransportId,PumpId;
    Uri Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_dailog);
        ButterKnife.bind(this);
        btnBlock.setOnClickListener(this);
        img.setOnClickListener(this);
        Status=getIntent().getIntExtra("BlockStatus",0);
        TransportId=getIntent().getIntExtra("TransportId",0);
        PumpId=getIntent().getIntExtra("PumpId",0);
        if(Status==2){
            txtHeader.setText("Enter Unblock Detail");
            btnBlock.setText("Unblock");
        }else{
            txtHeader.setText("Enter Block Detail");
            btnBlock.setText("Block");
        }
        EasyImage.configuration(this)
                .setImagesFolderName("My app images")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();
    }

    public void Block() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.POST_BLOCK_UNBLOCK, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    if(result.getBoolean("Success")){
                        new AwesomeErrorDialog(BlockDailog.this)
                                .setTitle("Transport Status")
                                .setMessage(result.getString("Message"))
                                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                                .setDialogIconAndColor(R.mipmap.icon_check, R.color.white)
                                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        BlockDailog.this.setResult(RESULT_OK);
                                        BlockDailog.this.finish();
                                    }
                                })
                                .show();
                    }else{
                        new AwesomeErrorDialog(BlockDailog.this)
                                .setTitle("Transport Status")
                                .setMessage(result.getString("Message"))
                                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                                .setButtonText(getString(R.string.dialog_ok_button))
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {

                                    }
                                })
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BlockDailog.this, R.string.Error_Network_Not_Reachable, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("PumpId",PumpId+"");
                map.put("TransportId",TransportId+"");
                map.put("BlockStatus",Status+"");
                map.put("Reason",etReason.getText().toString());
                return map;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> imgData = new HashMap<>();
                if (Image != null) {
                    byte[] Imagedata = convertImageToByte(Image);
                    imgData.put("URL", new DataPart(System.currentTimeMillis() + ".jpg", Imagedata, "image/jpeg"));
                }
                return imgData;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    public byte[] convertImageToByte(Uri uri) {
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnBlock){
            Block();
        }else if(v.getId()==R.id.img){
            EasyImage.openChooserWithGallery(BlockDailog.this, "Select Image", EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Image = Uri.fromFile(new File(imageFile.getAbsolutePath()));
                img.setImageURI(Image);
            }

        });
    }

}
