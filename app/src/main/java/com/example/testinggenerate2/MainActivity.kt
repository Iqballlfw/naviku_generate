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
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.itextpdf.text.Font
import com.itextpdf.text.BaseColor


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
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.zxing.EncodeHintType
import com.itextpdf.text.*
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPageEventHelper




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
    private var pdfFileUri: Uri? = null


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
                    val hints = Hashtable<EncodeHintType, Any>()
                    hints[EncodeHintType.MARGIN] = 0 // Set margin ke 0
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    ivQRCode.setImageBitmap(bmp)
                    val output = etData.text.toString()
                    hasil.text = "$output"
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            }
        }

//        saveImage.setOnClickListener {
//            val bitmap = getImageOfView(ivQRCode)
//            if(bitmap!=null){
//                saveimage(bitmap)
//            }
//        }
//        sharewa.setOnClickListener {
//            shareImage()
//        }
//        pdfFileUri = createPDFFileUri()

//        sharepdf.setOnClickListener {
//            val uri = createPDFFileUri() // Ganti dengan logika pembuatan URI file PDF
//            if (uri != null) {
//                savePDF(uri)
//            }
//        }
        pdfImage.setOnClickListener {
            createPDFFile()
        }
    }

//    private fun shareImage() {
//        // Mengambil tangkapan layar (screenshot) dari cardView
//        val bitmap = getImageOfView(ivQRCode)
//
//        // Menyimpan gambar ke penyimpanan internal menggunakan MediaStore
//        val imageUri = saveImageToInternalStorage(bitmap)
//
//        // Membuat intent untuk mengirim gambar ke WhatsApp
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "image/jpeg"
//        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
//        intent.setPackage("com.whatsapp")
//
//        // Memulai aktivitas share dengan aplikasi WhatsApp
//        startActivity(intent)
//    }


//
//    private fun saveimage(bitmap: Bitmap) {
//        val imageName = "Qrcode${System.currentTimeMillis()}.jpg"
//        var fos : OutputStream? = null
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
//            this.contentResolver?.also { resolver->
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME,imageName)
//                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
//                    put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
//                }
//                val imageUri : Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
//                fos = imageUri?.let{
//                    resolver.openOutputStream(it)
//                }
//            }
//        }
//        else{
//            val imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//            val image = File(imageDirectory,imageName)
//            fos = FileOutputStream(image)
//        }
//        fos.use {
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
//            Toast.makeText(this, "Berhasil mendownload", Toast.LENGTH_LONG).show()
//        }
//    }

//    ngubah card ngebungkus qr code sama text jadi bitmap image
    private fun getImageOfView(ivQRCode: ImageView): Bitmap {
        val bitmap = Bitmap.createBitmap(ivQRCode.width, ivQRCode.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
    ivQRCode.draw(canvas)
        return bitmap
    }

//    save image sementara untuk share qr codenya
//    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
//        // Menyimpan gambar ke penyimpanan internal menggunakan MediaStore
//        val savedImageURL = MediaStore.Images.Media.insertImage(
//            contentResolver,
//            bitmap,
//            "title",
//            "description"
//        )
//
//        // Mengambil URI gambar yang disimpan
//        return Uri.parse(savedImageURL)
//    }

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
                val mDoc = Document(PageSize.A3, 0f, 0f, 100f, 0f) // Mengatur margin menjadi 0
                val writer = PdfWriter.getInstance(mDoc, outputStream)
                val footerFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)


                val eventHandler = object : PdfPageEventHelper() {
                    override fun onEndPage(writer: PdfWriter?, document: Document?) {
                        val pdfContentByte = writer?.directContent

                        val svgInputStream = resources.openRawResource(R.raw.footer) // Ganti dengan ID sumber daya SVG Anda
                        val svg = SVG.getFromInputStream(svgInputStream)
                        val picture = svg.renderToPicture()
                        val footerBitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(footerBitmap)
                        canvas.drawPicture(picture)

                        val outputStream = ByteArrayOutputStream()
                        footerBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        val footerByteArray = outputStream.toByteArray()
                        val footerImage = Image.getInstance(footerByteArray)

                        footerImage.scaleToFit(document?.pageSize?.width ?: 0f, footerImage.height.toFloat()) // Gambar akan memenuhi lebar dokumen dan mengikuti tinggi asli gambar
                        footerImage.setAbsolutePosition(0f, 0f)

                        pdfContentByte?.addImage(footerImage)
                    }
                }

                writer.setPageEvent(eventHandler)

                mDoc.open()

                mDoc.addAuthor("Iqbal")

                val qrCodeWidthCm = 25f
                val qrCodeHeightCm = 25f

                // Convert cm to points (1 cm = 28.35 points)
                val qrCodeWidthPoints = qrCodeWidthCm * 28.35f
                val qrCodeHeightPoints = qrCodeHeightCm * 28.35f

                val bitmap: Bitmap? = getImageOfView(ivQRCode)
                val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, qrCodeWidthPoints.toInt(), qrCodeHeightPoints.toInt(), false) }
                val stream = ByteArrayOutputStream()
                scaledBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                val image = Image.getInstance(byteArray)
                // Menentukan posisi gambar sebagai tengah
                val documentWidth = mDoc.pageSize.width
                val documentHeight = mDoc.pageSize.height
                val imageWidth = image.width.toFloat()
                val imageHeight = image.height.toFloat()
                val offsetX = (documentWidth - imageWidth) / 2
                val offsetY = (documentHeight - imageHeight) / 2 + (documentHeight * 0.2).toFloat() - 70f

                image.setAbsolutePosition(offsetX, offsetY)

                mDoc.add(image)

                val textpdf = etData.text.toString()
                val textFont = Font(Font.FontFamily.HELVETICA, 30f, Font.BOLD, BaseColor.BLACK)

            // Menghitung lebar dan tinggi teks
                val textWidth = textFont.getCalculatedBaseFont(true).getWidthPoint(textpdf, textFont.size)
                val textHeight = textFont.getCalculatedBaseFont(true).getAscentPoint(textpdf, textFont.size) - textFont.getCalculatedBaseFont(true).getDescentPoint(textpdf, textFont.size)

            // Menghitung posisi teks di tengah dokumen
                val textX = (mDoc.pageSize.width - textWidth) / 2
                val textY = ivQRCode.bottom + textHeight - 240

            // Mendapatkan PdfContentByte dari writer
                val canvas = writer.directContent

            // Mulai teks
                canvas.beginText()
                canvas.setFontAndSize(textFont.getCalculatedBaseFont(true), textFont.size)
                canvas.setColorFill(BaseColor.BLACK)

            // Menampilkan teks di bawah gambar
                canvas.showTextAligned(Element.ALIGN_LEFT, textpdf, textX, textY, 0f)

            // Selesai teks
                canvas.endText()

                mDoc.close()



                Toast.makeText(this, "PDF Berhasil Dibuat", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun onShareButtonClick(view: View) {
        // Panggil fungsi savePDF untuk menyimpan PDF
        val uri = createPDFFileUri() // Ubah dengan kode yang menghasilkan URI PDF
        if (uri != null) {
            savePDF(uri)
        }

        // Panggil fungsi untuk membagikan PDF
        if (uri != null) {
            sharePDFViaWhatsApp(uri)
        }
    }

//    private fun sharePDF(uri: Uri) {
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "application/pdf"
//        intent.putExtra(Intent.EXTRA_STREAM, uri)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        startActivity(Intent.createChooser(intent, "Bagikan PDF"))
//    }


    private fun sharePDFViaWhatsApp(uri: Uri?) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        val shareIntent = Intent.createChooser(intent, "Bagikan PDF melalui...")
        shareIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(createWhatsAppIntent(uri), createBluetoothIntent(uri)))

        startActivity(shareIntent)
    }

    private fun createWhatsAppIntent(uri: Uri?): Intent {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "application/pdf"
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri)
        whatsappIntent.setPackage("com.whatsapp")
        return whatsappIntent
    }

    private fun createBluetoothIntent(uri: Uri?): Intent {
        val bluetoothIntent = Intent(Intent.ACTION_SEND)
        bluetoothIntent.type = "application/pdf"
        bluetoothIntent.putExtra(Intent.EXTRA_STREAM, uri)
        bluetoothIntent.setPackage("com.android.bluetooth")
        return bluetoothIntent
    }



    private fun createPDFFileUri(): Uri? {
        val textPDF = etData.text.toString()
        val pdfFileName = "NAVIKU_$textPDF.pdf" // Ganti dengan nama file PDF yang sesuai
        val pdfFile = File(getExternalFilesDir(null), pdfFileName)
        pdfFileUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", pdfFile)
        return pdfFileUri
    }


    private fun generateFileName(): String {
        val textPDF = etData.text.toString()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "NAVIKU_$textPDF.pdf"
    }














}



