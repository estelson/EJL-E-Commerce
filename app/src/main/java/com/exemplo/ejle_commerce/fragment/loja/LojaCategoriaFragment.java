package com.exemplo.ejle_commerce.fragment.loja;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.DialogFormCategoriaBinding;
import com.exemplo.ejle_commerce.databinding.FragmentLojaCategoriaBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Categoria;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class LojaCategoriaFragment extends Fragment {

    // public static final int REQUEST_GALERIA = 100;

    private FragmentLojaCategoriaBinding binding;

    private AlertDialog dialog;

    private DialogFormCategoriaBinding categoriaBinding;

    private String caminhoImagem = null;

    private Categoria categoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLojaCategoriaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    private void configClicks() {
        binding.btnAddCategoria.setOnClickListener(v -> {
            showDialog();
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        categoriaBinding = DialogFormCategoriaBinding.inflate(LayoutInflater.from(getContext()));

        categoriaBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        categoriaBinding.btnSalvar.setOnClickListener(v -> {
            String nomeCategoria = categoriaBinding.edtCategoria.getText().toString();
            if(!nomeCategoria.isEmpty()) {
                ocultarTeclado();

                if(caminhoImagem != null) {
                    categoriaBinding.progressBar.setVisibility(View.VISIBLE);

                    if (categoria == null) {
                        categoria = new Categoria();
                    }

                    categoria.setNome(nomeCategoria);
                    categoria.setTodas(categoriaBinding.cbTodos.isChecked());

                    salvarImagemFirebase();
                } else {
                    Toast.makeText(getContext(), "Selecione uma imagem para a categoria", Toast.LENGTH_LONG).show();
                }
            } else {
                categoriaBinding.edtCategoria.requestFocus();
                categoriaBinding.edtCategoria.setError("Informe o nome da categoria");
            }
        });

        categoriaBinding.imagemCategoria.setOnClickListener(v -> {
            verificarPermissaoGaleria();
        });

        builder.setView(categoriaBinding.getRoot());

        dialog = builder.create();
        dialog.show();
    }

    private void salvarImagemFirebase() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("categorias")
                .child(categoria.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                String urlImagem = task.getResult().toString();

                categoria.setUrlImagem(urlImagem);
                categoria.Salvar();

                categoria = null;

                dialog.dismiss();
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();

            Toast.makeText(getContext(), "Erro ao fazer upload da imagem. Motivo: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        });
    }

    private void verificarPermissaoGaleria() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permissao negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // startActivityForResult(intent, REQUEST_GALERIA);
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Recupera o caminho da imagem
                    Uri imagemSelecionada = result.getData().getData();
                    caminhoImagem = imagemSelecionada.toString();

                    try {
                        Bitmap bitmap;

                        if(Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagemSelecionada);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), imagemSelecionada);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }

                        categoriaBinding.imagemCategoria.setImageBitmap(bitmap);
                    } catch(Exception e) {
                        Toast.makeText(getContext(), "Erro ao carregar imagem da galeria. Motivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void showDialogPermissaoGaleria(PermissionListener listener, String[] permissoes) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas")
                .setDeniedMessage("Você negou a permissão para acessar a galeria do dispositivo. Deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //binding = null;
    }

    // Oculta o teclado do dispositivo
    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(categoriaBinding.edtCategoria.getWindowToken(), inputMethodManager.HIDE_NOT_ALWAYS);
    }

}