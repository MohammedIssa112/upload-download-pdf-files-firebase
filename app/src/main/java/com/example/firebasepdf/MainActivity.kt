package com.example.firebasepdf

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    private lateinit var uploadBtn: Button
    private lateinit var downloadBtn: Button

    private val REQUEST_CODE_UPLOAD_PDF = 1
    private var pdfUri: Uri? = null

    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBtn = findViewById(R.id.uploadButton)
        downloadBtn = findViewById(R.id.downloadButton)

        storageReference = FirebaseStorage.getInstance().reference

        uploadBtn.setOnClickListener {

            selectPdfFile()
        }

        downloadBtn.setOnClickListener {
            downloadPdfFile()
        }
    }

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, REQUEST_CODE_UPLOAD_PDF)
    }

    private fun uploadPdfFile() {
        if (pdfUri == null) {
            Toast.makeText(this, "Please select a PDF file to upload", Toast.LENGTH_SHORT).show()
            return
        }
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("upload pdf")
        mProgressDialog.setMessage("Waiting for upload data")
        mProgressDialog.show()
        val fileName = "my_file.pdf"

        val fileReference = storageReference.child("pdfs/$fileName")
        fileReference.putFile(pdfUri!!)
            .addOnSuccessListener {
                mProgressDialog.cancel()

                Toast.makeText(this, "uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadPdfFile() {
        val fileName = "my_file.pdf"
        val fileReference = storageReference.child("pdfs/$fileName")
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Download pdf")
        mProgressDialog.setMessage("Waiting for Download ")
        mProgressDialog.show()

        fileReference.downloadUrl
            .addOnSuccessListener { uri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                mProgressDialog.cancel()

                startActivity(intent)

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "File download failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPLOAD_PDF && resultCode == Activity.RESULT_OK && data != null) {
            pdfUri = data.data
            uploadPdfFile()
        }
    }
}

