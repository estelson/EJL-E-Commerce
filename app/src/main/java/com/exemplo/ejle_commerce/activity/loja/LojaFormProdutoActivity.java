package com.exemplo.ejle_commerce.activity.loja;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.ActivityLojaFormProdutoBinding;
import com.exemplo.ejle_commerce.databinding.BottomSheetFormProdutoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LojaFormProdutoActivity extends AppCompatActivity {

    private ActivityLojaFormProdutoBinding binding;

    private int resultCode = 0;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    private void configClicks() {
        binding.imagemProduto0.setOnClickListener(v -> {
            showBottomSheet(0);
        });

        binding.imagemProduto1.setOnClickListener(v -> {
            showBottomSheet(1);
        });

        binding.imagemProduto2.setOnClickListener(v -> {
            showBottomSheet(2);
        });
    }

    private void showBottomSheet(int imgCode) {
        this.resultCode = imgCode;

        BottomSheetFormProdutoBinding sheetBinding = BottomSheetFormProdutoBinding.inflate(LayoutInflater.from(this));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(sheetBinding.getRoot());

        bottomSheetDialog.show();

        sheetBinding.btnCamera.setOnClickListener(v -> {
            verificarPermissaoCamera();
            bottomSheetDialog.dismiss();
        });

        sheetBinding.btnGaleria.setOnClickListener(v -> {
            verificarPermissaoGaleria();
            bottomSheetDialog.dismiss();
        });

        sheetBinding.btnCancelar.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
    }

    private void verificarPermissaoCamera() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirCamera();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissao negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissao(
                permissionListener,
                new String[]{
                        Manifest.permission.CAMERA,
                },
                "Você negou a permissão para acessar a câmera do dispositivo. Deseja permitir?"
        );
    }

    private void verificarPermissaoGaleria() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissao negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissao(
                permissionListener,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                },
                "Você negou a permissão para acessar a galeria do dispositivo. Deseja permitir?"
        );
    }

    private void showDialogPermissao(PermissionListener listener, String[] permissoes, String msg) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas")
                .setDeniedMessage(msg)
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void abrirCamera() {
        switch (resultCode) {
            case 0:
                resultCode = 3;
                break;
            case 1:
                resultCode = 4;
                break;
            case 2:
                resultCode = 5;
                break;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Erro ao capturar a imagem da câmera do dispositivo. Motivo: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.exemplo.ejle_commerce.fileprovider",
                    photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            resultLauncher.launch(takePictureIntent);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String caminhoImagem;

                    if(resultCode <= 2) { // Galeria
                        // Recupera o caminho da imagem
                        Uri imagemSelecionada = result.getData().getData();

                        switch (resultCode) {
                            case 0:
                                binding.imagemProduto0.setImageBitmap(getBitmap(imagemSelecionada));
                                binding.imagemProduto0Fake.setVisibility(View.GONE);

                                break;
                            case 1:
                                binding.imagemProduto1.setImageBitmap(getBitmap(imagemSelecionada));
                                binding.imagemProduto1Fake.setVisibility(View.GONE);

                                break;
                            case 2:
                                binding.imagemProduto2.setImageBitmap(getBitmap(imagemSelecionada));
                                binding.imagemProduto2Fake.setVisibility(View.GONE);

                                break;
                        }
                    } else { // Câmera
                        File file = new File(currentPhotoPath);
                        caminhoImagem = String.valueOf(file.toURI());

                        switch (resultCode) {
                            case 3:
                                binding.imagemProduto0.setImageURI(Uri.fromFile(file));
                                binding.imagemProduto0Fake.setVisibility(View.GONE);

                                break;
                            case 4:
                                binding.imagemProduto1.setImageURI(Uri.fromFile(file));
                                binding.imagemProduto1Fake.setVisibility(View.GONE);

                                break;
                            case 5:
                                binding.imagemProduto2.setImageURI(Uri.fromFile(file));
                                binding.imagemProduto2Fake.setVisibility(View.GONE);

                                break;
                        }
                    }
                }
            }
    );

    private Bitmap getBitmap(Uri caminhoUri) {
        Bitmap bitmap = null;

        try {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), caminhoUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), caminhoUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Erro ao carregar imagem da galeria. Motivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return bitmap;
    }

}