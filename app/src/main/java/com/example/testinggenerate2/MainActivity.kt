package com.example.testinggenerate2

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore

import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.pdf.PdfWriter
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.itextpdf.text.*


class MainActivity : AppCompatActivity() {

    private lateinit var ivQRCode : ImageView
    private lateinit var etData : EditText
    private lateinit var btnGenerateQRCode : Button
    private lateinit var saveImage : Button
    private lateinit var hasil : TextView
    private lateinit var view: CardView
    private lateinit var sharewa: Button
    private lateinit var sharepdf: Button
    private lateinit var pdfImage : Button
    private val STORAGE_CODE = 1001

    private val createPdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    savePDF(uri)
                } else {
                    Toast.makeText(this, "Gagal membuat file PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById(R.id.card_view)
        ivQRCode = findViewById(R.id.ivQRCode)
        etData = findViewById(R.id.etData)
        btnGenerateQRCode = findViewById(R.id.btnGenerateQRCode)
        saveImage = findViewById(R.id.saveImage)
        pdfImage = findViewById(R.id.pdfImage)
        sharepdf = findViewById(R.id.sharepdf)
        hasil = findViewById(R.id.hasil)
        sharewa = findViewById(R.id.sharewa)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)



        btnGenerateQRCode.setOnClickListener {
            val data = etData.text.toString().trim()
            if (data.isEmpty()){
                Toast.makeText(this, "Masukan Data!", Toast.LENGTH_SHORT).show()
            }else{
                val writer = QRCodeWriter()
                try {
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE,512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width){
                        for (y in 0 until height){
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    ivQRCode.setImageBitmap(bmp)
                    val output = etData.text.toString()
                    hasil.text = "$output"
                }catch (e: WriterException){
                    e.printStackTrace()
                }
            }
        }

        saveImage.setOnClickListener {
            val bitmap = getImageOfView(view)
            if(bitmap!=null){
                saveimage(bitmap)
            }
        }
        sharewa.setOnClickListener {
            shareImage()
        }
        sharepdf.setOnClickListener {
            shareQRPDF()
        }
        pdfImage.setOnClickListener {
            createPDFFile()
        }
    }

    private fun shareImage() {
        // Mengambil tangkapan layar (screenshot) dari cardView
        val bitmap = getImageOfView(view)

        // Menyimpan gambar ke penyimpanan internal menggunakan MediaStore
        val imageUri = saveImageToInternalStorage(bitmap)

        // Membuat intent untuk mengirim gambar ke WhatsApp
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.setPackage("com.whatsapp")

        // Memulai aktivitas share dengan aplikasi WhatsApp
        startActivity(intent)
    }

//    MASIH GAGAL
    private fun shareQRPDF() {
        // Mengambil tangkapan layar (screenshot) dari cardView
        val bitmap = getImageOfView(view)

        // Menyimpan gambar ke penyimpanan internal menggunakan MediaStore
        val imageUri = saveImageToInternalStorage(bitmap)

        // Membuat intent untuk mengirim gambar ke WhatsApp
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.setPackage("com.whatsapp")

        // Memulai aktivitas share dengan aplikasi WhatsApp
        startActivity(intent)
    }

//
    private fun saveimage(bitmap: Bitmap) {
        val imageName = "Qrcode${System.currentTimeMillis()}.jpg"
        var fos : OutputStream? = null
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            this.contentResolver?.also { resolver->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
                }
                val imageUri : Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                fos = imageUri?.let{
                    resolver.openOutputStream(it)
                }
            }
        }
        else{
            val imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDirectory,imageName)
            fos = FileOutputStream(image)
        }
        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            Toast.makeText(this, "Berhasil mendownload", Toast.LENGTH_LONG).show()
        }
    }

//    ngubah card ngebungkus qr code sama text jadi bitmap image
    private fun getImageOfView(view: CardView): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

//    save image sementara untuk share qr codenya
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        // Menyimpan gambar ke penyimpanan internal menggunakan MediaStore
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "title",
            "description"
        )

        // Mengambil URI gambar yang disimpan
        return Uri.parse(savedImageURL)
    }

    private fun createPDFFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_TITLE, generateFileName())

        createPdfLauncher.launch(intent)
    }

    private fun savePDF(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val mDoc = Document(PageSize.A4)
                PdfWriter.getInstance(mDoc, outputStream)
                mDoc.open()

                val data = getImageOfView(view)
                mDoc.addAuthor("Iqbal")


                val bitmap: Bitmap? = getImageOfView(view)
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                val image = Image.getInstance(byteArray)

                mDoc.add(image)

                mDoc.close()

                Toast.makeText(this, "PDF Berhasil Dibuat", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "PDF_$timeStamp.pdf"
    }














}



