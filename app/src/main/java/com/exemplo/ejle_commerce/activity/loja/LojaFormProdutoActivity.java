package com.exemplo.ejle_commerce.activity.loja;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.adapter.CategoriaDialogAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityLojaFormProdutoBinding;
import com.exemplo.ejle_commerce.databinding.BottomSheetFormProdutoBinding;
import com.exemplo.ejle_commerce.databinding.DialogDeleteBinding;
import com.exemplo.ejle_commerce.databinding.DialogFormProdutoCategoriaBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Categoria;
import com.exemplo.ejle_commerce.model.ImagemUpload;
import com.exemplo.ejle_commerce.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LojaFormProdutoActivity extends AppCompatActivity implements CategoriaDialogAdapter.OnClick {

    private List<String> idCategoriasSelecionadas = new ArrayList<>();

    private ActivityLojaFormProdutoBinding binding;

    private DialogFormProdutoCategoriaBinding categoriaBinding;

    private int resultCode = 0;

    private Produto produto;
    private boolean novoProduto = true;

    private List<ImagemUpload> imagemUploadList = new ArrayList<>();
    private List<Categoria> categoriasList = new ArrayList<>();
    private List<String> categoriasSelecionadasList = new ArrayList<>();

    private String currentPhotoPath;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getExtras();

        configClicks();

        iniciarComponentes();

        recuperarCategorias();
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            produto = (Produto) bundle.getSerializable("produtoSelecionado");

            configProduto();
        }
    }

    private void configProduto() {
        novoProduto = false;

        idCategoriasSelecionadas.addAll(produto.getIdsCategorias());

        Picasso.get().load(produto.getUrlsImagens().get(0).getCaminhoImagem()).into(binding.imagemProduto0);
        Picasso.get().load(produto.getUrlsImagens().get(1).getCaminhoImagem()).into(binding.imagemProduto1);
        Picasso.get().load(produto.getUrlsImagens().get(2).getCaminhoImagem()).into(binding.imagemProduto2);

        binding.imagemProduto0Fake.setVisibility(View.GONE);
        binding.imagemProduto1Fake.setVisibility(View.GONE);
        binding.imagemProduto2Fake.setVisibility(View.GONE);

        binding.edtTitulo.setText(produto.getTitulo());
        binding.edtDescricao.setText(produto.getDescricao());
        binding.edtValorAntigo.setText(String.valueOf(produto.getValorAntigo() * 10));
        binding.edtValorAtual.setText(String.valueOf(produto.getValorAtual() * 10));
    }

    private void configCategoriasEdicao() {
        if(!novoProduto) { // Edição
            for (Categoria categoria : categoriasList) {
                if (produto.getIdsCategorias().contains(categoria.getId())) {
                    categoriasSelecionadasList.add(categoria.getNome());
                }
            }

            Collections.reverse(categoriasSelecionadasList);

            categoriasSelecionadas();
        }
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

        binding.toolbar.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    private void configRv() {
        categoriaBinding.rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        categoriaBinding.rvCategorias.setHasFixedSize(true);

        CategoriaDialogAdapter categoriaDialogAdapter = new CategoriaDialogAdapter(idCategoriasSelecionadas, categoriasList, this);

        categoriaBinding.rvCategorias.setAdapter(categoriaDialogAdapter);
    }

    public void showDialogCategorias(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog2);

        categoriaBinding = DialogFormProdutoCategoriaBinding.inflate(LayoutInflater.from(this));

        categoriaBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        categoriaBinding.btnSalvar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (categoriasList.isEmpty()) {
            categoriaBinding.textInfo.setText("Nenhuma categoria cadastrada");
        } else {
            categoriaBinding.textInfo.setText("");
        }

        categoriaBinding.progressBar.setVisibility(View.GONE);

        configRv();

        builder.setView(categoriaBinding.getRoot());

        dialog = builder.create();
        dialog.show();
    }

    private void recuperarCategorias() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");

        categoriaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    categoriasList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Categoria categoria = ds.getValue(Categoria.class);
                        categoriasList.add(categoria);
                    }

                    configCategoriasEdicao();
                }

                Collections.reverse(categoriasList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void categoriasSelecionadas() {
        StringBuilder categorias = new StringBuilder();

        for (int i = 0; i < categoriasSelecionadasList.size(); i++) {
            if(i != categoriasSelecionadasList.size() - 1) {
                categorias.append(categoriasSelecionadasList.get(i)).append(", ");
            } else {
                categorias.append(categoriasSelecionadasList.get(i));
            }
        }

        if(!categoriasSelecionadasList.isEmpty()) {
            binding.btnCategorias.setText(categorias);
        } else {
            binding.btnCategorias.setText("Nenhuma categoria selecionada");
        }
    }

    public void validarDadosLojaFormProduto(View view) {
        String titulo = binding.edtTitulo.getText().toString().trim();
        String descricao = binding.edtDescricao.getText().toString().trim();
        double valorAntigo = (double) binding.edtValorAntigo.getRawValue() / 100;
        double valorAtual = (double) binding.edtValorAtual.getRawValue() / 100;

        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (valorAtual > 0) {
                    if(!idCategoriasSelecionadas.isEmpty()) {
                        if (produto == null) {
                            produto = new Produto();
                        }

                        produto.setTitulo(titulo);
                        produto.setDescricao(descricao);
                        produto.setValorAtual(valorAtual);

                        if (valorAntigo > 0) { // valorAntigo NÃO é obrigatório
                            produto.setValorAntigo(valorAntigo);
                        }

                        produto.setIdsCategorias(idCategoriasSelecionadas);

                        if (novoProduto) { // Inclusão de produto
                            if (imagemUploadList.size() == 3) {
                                for (int i = 0; i < imagemUploadList.size(); i++) {
                                    salvarImagemFirebase(imagemUploadList.get(i), i);
                                }

                                Toast.makeText(this, "Produto incluído com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                ocultarTeclado();

                                Toast.makeText(this, "Selecione 3 imagens para o produto", Toast.LENGTH_SHORT).show();
                            }
                        } else { // Alteração de produto
                            ocultarTeclado();

                            if (imagemUploadList.size() > 0) {
                                for (int i = 0; i < imagemUploadList.size(); i++) {
                                    salvarImagemFirebase(imagemUploadList.get(i), i);
                                }
                            } else {
                                produto.salvar(false);
                            }

                            Toast.makeText(this, "Produto alterado com sucesso", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ocultarTeclado();

                        Toast.makeText(this, "Selecione pelo menos uma categoria", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.edtValorAtual.requestFocus();
                    binding.edtValorAtual.setError("Informe um valor válido");
                }
            } else {
                binding.edtDescricao.requestFocus();
                binding.edtDescricao.setError("Informe a descrição do produto");
            }
        } else {
            binding.edtTitulo.requestFocus();
            binding.edtTitulo.setError("Informe o título do produto");
        }
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

    private void configUpload(String caminhoImagem) {
        int request = 0;

        switch (resultCode) {
            case 0:
            case 3:
                request = 0;
                break;
            case 1:
            case 4:
                request = 1;
                break;
            case 2:
            case 5:
                request = 2;
                break;
        }

        ImagemUpload imagemUpload = new ImagemUpload(request, caminhoImagem);

        if (!imagemUploadList.isEmpty()) {
            boolean encontrou = false;
            for (int i = 0; i < imagemUploadList.size(); i++) {
                if (imagemUploadList.get(i).getIndex() == request) {
                    encontrou = true;
                }
            }

            if (encontrou) { // Está alterando a imagem na posição X
                imagemUploadList.set(request, imagemUpload);
            } else { // Está incluindo uma imagem na posição X
                imagemUploadList.add(imagemUpload);
            }
        } else {
            imagemUploadList.add(imagemUpload);
        }
    }

    private void salvarImagemFirebase(ImagemUpload imagemUpload, int count) {
        int index = imagemUpload.getIndex();
        String caminhoImagem = imagemUpload.getCaminhoImagem();

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(produto.getId())
                .child("imagem" + index + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                imagemUpload.setCaminhoImagem(task.getResult().toString());

                if(novoProduto) {
                    produto.getUrlsImagens().add(imagemUpload);
                } else {
                    produto.getUrlsImagens().set(index, imagemUpload);
                }

                if (imagemUploadList.size() == count + 1) {
                    produto.salvar(novoProduto);

                    imagemUploadList.clear();

                    if(novoProduto) {
                        finish();
                    }
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao gravar a imagem. Motivo: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        });
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String caminhoImagem;

                    if (resultCode <= 2) { // Galeria
                        // Recupera o caminho da imagem
                        Uri imagemSelecionada = result.getData().getData();

                        try {
                            caminhoImagem = imagemSelecionada.toString();

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

                            configUpload(caminhoImagem);
                        } catch (Exception e) {
                            Toast.makeText(this, "Não foi possível recuperar a imagem da galeria do dispositivo. Motivo:  " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
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

                        configUpload(caminhoImagem);
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

    private void iniciarComponentes() {
        binding.edtValorAntigo.setLocale(new Locale("PT", "br"));
        binding.edtValorAtual.setLocale(new Locale("PT", "br"));

        if(novoProduto) {
            binding.toolbar.textTitulo.setText("Inclusão de produto");
        } else {
            binding.toolbar.textTitulo.setText("Edição de produto");
        }
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtTitulo.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClickListener(Categoria categoria) {
        if(!idCategoriasSelecionadas.contains(categoria.getId())) { // Check
            idCategoriasSelecionadas.add(categoria.getId());
            categoriasSelecionadasList.add(categoria.getNome());
        } else { // Uncheck
            idCategoriasSelecionadas.remove(categoria.getId());
            categoriasSelecionadasList.remove(categoria.getNome());
        }

        categoriasSelecionadas();
    }
}