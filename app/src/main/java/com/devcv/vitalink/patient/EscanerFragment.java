package com.devcv.vitalink.patient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.devcv.vitalink.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EscanerFragment extends Fragment {

    private Button buttonScan, buttonSaveDocument;
    private ImageView imageViewScanned;
    private TextView textViewRecognizedText;
    private EditText editTextDocumentTitle;
    private Bitmap imageBitmap;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                        imageViewScanned.setImageBitmap(imageBitmap);
                        recognizeTextFromImage();
                    }
                } else {
                    Toast.makeText(getContext(), "Captura cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_escaner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazamos todas las vistas
        buttonScan = view.findViewById(R.id.buttonScan);
        buttonSaveDocument = view.findViewById(R.id.buttonSaveDocument);
        imageViewScanned = view.findViewById(R.id.imageViewScanned);
        textViewRecognizedText = view.findViewById(R.id.textViewRecognizedText);
        editTextDocumentTitle = view.findViewById(R.id.editTextDocumentTitle);

        // Inicializamos Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        buttonScan.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(takePictureIntent);
        });

        buttonSaveDocument.setOnClickListener(v -> saveDocumentToFirebase());
    }

    private void recognizeTextFromImage() {
        if (imageBitmap == null) return;
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        textViewRecognizedText.setText("Procesando...");

        recognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                    textViewRecognizedText.setText(visionText.getText());
                    // Mostramos los campos para guardar
                    editTextDocumentTitle.setVisibility(View.VISIBLE);
                    buttonSaveDocument.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    textViewRecognizedText.setText("Error al reconocer texto: " + e.getMessage());
                });
    }

    private void saveDocumentToFirebase() {
        String title = editTextDocumentTitle.getText().toString().trim();
        String recognizedText = textViewRecognizedText.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();

        if (title.isEmpty()) {
            editTextDocumentTitle.setError("El título es requerido");
            return;
        }
        if (imageBitmap == null) {
            Toast.makeText(getContext(), "No hay imagen para guardar.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Subir la imagen a Firebase Storage
        // Comprimimos el bitmap a un array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Creamos una referencia única para el archivo en Storage
        String imagePath = "scanned_documents/" + userId + "/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storage.getReference().child(imagePath);

        Toast.makeText(getContext(), "Guardando documento...", Toast.LENGTH_SHORT).show();
        buttonSaveDocument.setEnabled(false); // Deshabilitamos para evitar doble clic

        imageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // 2. Si la imagen se subió, obtenemos su URL de descarga
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // 3. Creamos el documento en Firestore
                        Map<String, Object> documentData = new HashMap<>();
                        documentData.put("documentTitle", title);
                        documentData.put("imageUrl", imageUrl);
                        documentData.put("extractedText", recognizedText);
                        documentData.put("scanDate", new Timestamp(new Date()));
                        documentData.put("uploadedBy", userId);

                        db.collection("users").document(userId).collection("scannedDocuments")
                                .add(documentData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "Documento guardado exitosamente", Toast.LENGTH_LONG).show();
                                    resetScannerView();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al guardar en Firestore.", Toast.LENGTH_SHORT).show();
                                    buttonSaveDocument.setEnabled(true);
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                    buttonSaveDocument.setEnabled(true);
                });
    }

    private void resetScannerView() {
        // Limpiamos la vista para un nuevo escaneo
        editTextDocumentTitle.setText("");
        textViewRecognizedText.setText("El texto del documento aparecerá aquí...");
        imageViewScanned.setImageResource(0); // Limpia la imagen
        editTextDocumentTitle.setVisibility(View.GONE);
        buttonSaveDocument.setVisibility(View.GONE);
        buttonSaveDocument.setEnabled(true);
        imageBitmap = null;
    }
}