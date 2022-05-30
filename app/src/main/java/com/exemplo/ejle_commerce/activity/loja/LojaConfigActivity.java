package com.exemplo.ejle_commerce.activity.loja;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLojaConfigBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Loja;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class LojaConfigActivity extends AppCompatActivity {

    private ActivityLojaConfigBinding binding;

    private Loja loja;

    private String caminhoImagem = null;
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imagemSelecionada = result.getData().getData();
                    caminhoImagem = imagemSelecionada.toString();
                    binding.imgLogo.setImageBitmap(getBitmap(imagemSelecionada));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperarLoja();

        iniciarComponentes();

        configClicks();
    }

    private void configClicks() {
        binding.include.textTitulo.setText("Configurações");

        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.imgLogo.setOnClickListener(v -> {
            verificarPermissaoGaleria();
        });

        binding.btnSalvar.setOnClickListener(v -> {
            if(loja != null) {
                validarDados();
            } else {
                Toast.makeText(this, "Ainda estamos recuperando as informações da loja. Por favor aguarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void recuperarLoja() {
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");

        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loja = snapshot.getValue(Loja.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        if(loja.getUrlLogo() != null) {
            Picasso.get().load(loja.getUrlLogo()).into(binding.imgLogo);
        }

        if(loja.getNome() != null) {
            binding.edtLoja.setText(loja.getNome());
        }

        if(loja.getCnpj() != null) {
            binding.edtCNPJ.setText(loja.getCnpj());
        }

        if(loja.getPedidoMinimo() != 0) {
            binding.edtPedidoMinimo.setText(String.valueOf(loja.getPedidoMinimo() * 10));
        }

        if(loja.getFreteGratis() != 0) {
            binding.edtFrete.setText(String.valueOf(loja.getFreteGratis() * 10));
        }

        if(loja.getPublicKey() != null) {
            binding.edtPublicKey.setText(loja.getPublicKey());
        }

        if(loja.getAccessToken() != null) {
            binding.edtAccessToken.setText(loja.getAccessToken());
        }

        if(loja.getParcelas() != 0) {
            binding.edtQtdeParcelas.setText(String.valueOf(loja.getParcelas()));
        }
    }

    private void validarDados() {
        String nomeLoja = binding.edtLoja.getText().toString().trim();
        String cnpj = binding.edtCNPJ.getMasked();
        double pedidoMinimo = (double) binding.edtPedidoMinimo.getRawValue() / 100;
        double freteGratis = (double) binding.edtFrete.getRawValue() / 100;
        String publicKey = binding.edtPublicKey.getText().toString().trim();
        String accessToken = binding.edtAccessToken.getText().toString().trim();

        String parcelasStr = binding.edtQtdeParcelas.getText().toString().trim();
        int parcelas = 0;
        if(!parcelasStr.isEmpty()) {
            parcelas = Integer.parseInt(binding.edtQtdeParcelas.getText().toString().trim());
        }

        if(!nomeLoja.isEmpty()) {
            if(!cnpj.isEmpty()) {
                if(cnpj.length() == 18) {
                    if(!publicKey.isEmpty()) {
                        if(!accessToken.isEmpty()) {
                            if(parcelas > 0 && parcelas <= 12) {
                                ocultarTeclado();

                                loja.setNome(nomeLoja);
                                loja.setCnpj(cnpj);
                                loja.setPedidoMinimo(pedidoMinimo);
                                loja.setFreteGratis(freteGratis);
                                loja.setPublicKey(publicKey);
                                loja.setAccessToken(accessToken);
                                loja.setParcelas(parcelas);

                                if(caminhoImagem != null) {
                                    salvarImagemFirebase();

                                    Toast.makeText(this, "Informações atualizadas com sucesso", Toast.LENGTH_SHORT).show();
                                } else if(loja.getUrlLogo() != null) {
                                    loja.salvar();

                                    Toast.makeText(this, "Informações atualizadas com sucesso", Toast.LENGTH_SHORT).show();
                                } else {
                                    ocultarTeclado();

                                    Toast.makeText(this, "Selecione uma logomarca para a loja", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.edtQtdeParcelas.requestFocus();
                                binding.edtQtdeParcelas.setError("Informe uma quantidade de parcelas entre 1 e 12");
                            }
                        } else {
                            binding.edtAccessToken.requestFocus();
                            binding.edtAccessToken.setError("Informe o Access Token");
                        }
                    } else {
                        binding.edtPublicKey.requestFocus();
                        binding.edtPublicKey.setError("Informe a Public Key");
                    }
                } else {
                    binding.edtCNPJ.requestFocus();
                    binding.edtCNPJ.setError("CNPJ inválido");
                }
            } else {
                binding.edtCNPJ.requestFocus();
                binding.edtCNPJ.setError("Informe o CNPJ da loja");
            }
        } else {
            binding.edtLoja.requestFocus();
            binding.edtLoja.setError("Informe o nome da loja");
        }
    }

    private void salvarImagemFirebase() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("loja")
                .child(loja.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                loja.setUrlLogo(task.getResult().toString());

                loja.salvar();

                caminhoImagem = null;
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao gravar a imagem. Motivo: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
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
        binding.edtPedidoMinimo.setLocale(new Locale("PT", "br"));
        binding.edtFrete.setLocale(new Locale("PT", "br"));
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtPedidoMinimo.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}